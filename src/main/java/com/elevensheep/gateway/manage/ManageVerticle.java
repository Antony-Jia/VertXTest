package com.elevensheep.gateway.manage;

import com.elevensheep.gateway.manage.service.ManageService;
import com.elevensheep.gateway.manage.serviceImpl.ManageServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ManageVerticle extends AbstractVerticle {

    private EventBus eventBus;
    private ManageService manageService;
    private final Logger logger = LogManager.getLogger(ManageVerticle.class);

    private MySQLPool client;

    private MySQLConnectOptions connectOptions;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        
        eventBus = vertx.eventBus();

        this.connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost").setDatabase("gateway")
                .setUser("root").setPassword("");
        // Pool Options
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        // Create the pool from the data object
        this.client = MySQLPool.pool(vertx, this.connectOptions, poolOptions);

        this.manageService = new ManageServiceImpl(this.client);

        //add event bus
        new ServiceBinder(vertx).setAddress("manage-sql-service").register(ManageService.class, manageService);
        manageService.initializePersistence(h->{
            if(h.succeeded()){
                startPromise.complete();
            }else{
                startPromise.fail("JDCD init failed");
            }
        });

        // get HTTP host and port from configuration, or use default value
        String host = config().getString("api.gateway.http.address", "localhost");
        int port = config().getInteger("api.gateway.http.port", 8786); // (1)

        Router router = Router.router(vertx); // (2)

        // body handler
        router.route().handler(BodyHandler.create()); // (4)

        // version handler
        router.get("/manage/v").handler(this::apiVersion); // (5)
        
        router.get("/manage/getAllHttpEndpoint").handler(this::getAllHttpEndpoint);

        HttpServerOptions httpServerOptions = new HttpServerOptions();
        // .setSsl(true)
        // .setKeyStoreOptions(new
        // JksOptions().setPath("server.jks").setPassword("123456")); // (13)

        // create http server
        vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(port, host, ar -> { // (14)
            if (ar.succeeded()) {
                // publishApiGateway(host, port);
                logger.debug("API Gateway Manage is running on port " + port);
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        stop();
        stopPromise.complete();
    }

    private void apiVersion(RoutingContext context) {
        context.response().end(new JsonObject().put("version", "v1").encodePrettily());
    }

    private void getAllHttpEndpoint(RoutingContext context) {
        logger.debug("getallhttpendpoint");
        manageService.retrieveAllManage(h->{
            if(h.succeeded()){
                logger.debug(h.result());
                context.response().end(h.result().encodePrettily());
            }else{
                context.response().setStatusCode(500);
                context.response().end();
            }
        });
    }

}
