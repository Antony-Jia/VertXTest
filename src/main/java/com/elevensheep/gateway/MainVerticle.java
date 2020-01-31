package com.elevensheep.gateway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start();

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
    //.setSsl(true)
    //.setKeyStoreOptions(new JksOptions().setPath("server.jks").setPassword("123456")); // (13)

  // create http server
  vertx.createHttpServer(httpServerOptions)
    .requestHandler(router)
    .listen(port, host, ar -> { // (14)
      if (ar.succeeded()) {
        //publishApiGateway(host, port);
        startPromise.complete();
        logger.debug("API Gateway is running on port " + port);
      } else {
        startPromise.fail(ar.cause());
      }
    });

  }


  private void apiVersion(RoutingContext context) {
    context.response()
      .end(new JsonObject().put("version", "v1").encodePrettily());
  }

  private void dispatchRequests(RoutingContext context) {
    
  }

}
