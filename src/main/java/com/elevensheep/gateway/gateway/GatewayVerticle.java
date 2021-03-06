package com.elevensheep.gateway.gateway;

import java.util.HashMap;
import java.util.Map;

import com.elevensheep.gateway.loadbalance.LoadBalance;
import com.elevensheep.gateway.manage.service.ManageService;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class GatewayVerticle extends AbstractVerticle {

    private final Logger logger = LogManager.getLogger(GatewayVerticle.class);

    private CircuitBreaker circuitBreaker;
    private HttpClient httpclient;
    private ManageService service;

    private Map<String, LoadBalance> loadBalanceStrategy = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start();

        ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx).setAddress("manage-sql-service");

        this.service = builder.build(ManageService.class);

        circuitBreaker = CircuitBreaker.create("my-circuit-breaker", vertx,
                new CircuitBreakerOptions().setMaxFailures(5) // number of failure before opening the circuit
                        .setTimeout(2000) // consider a failure if the operation does not succeed in time
                        .setFallbackOnFailure(true) // do we call the fallback on failure
                        .setResetTimeout(10000) // time spent in open state before attempting to re-try
        );
        httpclient = vertx.createHttpClient();

        // get HTTP host and port from configuration, or use default value
        String host = config().getString("api.gateway.http.address", "localhost");
        int port = config().getInteger("api.gateway.http.port", 8787); // (1)

        Router router = Router.router(vertx); // (2)

        // body handler
        router.route().handler(BodyHandler.create()); // (4)

        // version handler
        router.get("/api/v").handler(this::apiVersion); // (5)

        // api dispatcher
        router.route("/api/*").handler(this::dispatchRequests); // (10)

        // static content
        router.route("/*").handler(StaticHandler.create()); // (12)

        // enable HTTPS
        HttpServerOptions httpServerOptions = new HttpServerOptions();
        // .setSsl(true)
        // .setKeyStoreOptions(new
        // JksOptions().setPath("server.jks").setPassword("123456")); // (13)

        // create http server
        vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(port, host, ar -> { // (14)
            if (ar.succeeded()) {
                // publishApiGateway(host, port);
                startPromise.complete();
                logger.debug("API Gateway is running on port " + port);
            } else {
                startPromise.fail(ar.cause());
            }
        });

    }

    private void apiVersion(RoutingContext context) {
        context.response().end(new JsonObject().put("version", "v1").encodePrettily());
    }

    private void dispatchRequests(RoutingContext context) {
        int initialOffset = 5; // length of '/api'
        String path = context.request().uri();
        logger.debug("dispatch" + path);
        circuitBreaker.execute(future -> {
            String prefix = (path.substring(initialOffset).split("/"))[0];
            this.service.retrieveManage(prefix, resultHandler->{
                if(resultHandler.succeeded()){
                    JsonArray jsonArray = resultHandler.result();
                    doDispatch(context, jsonArray.getJsonObject(0).getString("url"), httpclient, future);
                }
                else{
                    future.fail("not found");
                }
            });
        }).setHandler(ar -> {
            if (ar.failed()) {
                logger.error("failer" + ar.cause());
                context.response().setStatusCode(502).putHeader("content-type", "application/json")
                        .end(new JsonObject().put("error", "bad_gateway")
                                // .put("message", ex.getMessage())
                                .encodePrettily());
            }
        });
    }

    private void doDispatch(RoutingContext context, String path, HttpClient client, Promise<Object> cbFuture) {

        // requestOptions.setURI(uri)
        logger.debug("dispatch path is " + path);
        HttpClientRequest toReq = client.requestAbs(context.request().method(), path, response -> {
            HttpClientResponse httpClientResponse = response.result();
            if (response.succeeded()) {
                httpClientResponse.bodyHandler(body -> {
                    if (httpClientResponse.statusCode() >= 500) {
                        cbFuture.fail("get service failed " + path);
                    } else {
                        HttpServerResponse toRsp = context.response().setStatusCode(httpClientResponse.statusCode());
                        httpClientResponse.headers().forEach(header -> {
                            toRsp.putHeader(header.getKey(), header.getValue());
                        });
                        toRsp.end(body);
                        cbFuture.complete();
                    }
                });
            } else if (response.failed()) {
                logger.debug("message failed");
                logger.debug(response.cause().getMessage());
            }
            client.close();
        });

        // set headers
        context.request().headers().forEach(header -> { // (2)
            toReq.putHeader(header.getKey(), header.getValue());
        });
        // send request
        if (context.getBody() == null) { // (3)
            toReq.end();
        } else {
            toReq.end(context.getBody());
        }
    }

    private void getLink(String name, Handler<AsyncResult<String>> resultHandler){
        this.service.retrieveManage(name, hander->{
            if(hander.succeeded()){
                JsonArray jsonArray = hander.result();
                LoadBalance lb = loadBalanceStrategy.get(name);
                if(null == lb){
                    lb = new LoadBalance(name);
                }
                String url = lb.getUrl(jsonArray);
                resultHandler.handle(Future.succeededFuture(url));
            }else{
                resultHandler.handle(Future.failedFuture("retrieve url failed"));
            }
        });
        return;
    }
}
