package com.elevensheep.gateway;

import com.elevensheep.gateway.manage.ManageVerticle;
import com.elevensheep.gateway.test.Test1Verticle;
import com.elevensheep.gateway.test.Test2Verticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter.Result;








public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start();
    EventBus eBus = vertx.eventBus();
    vertx
        .deployVerticle("com.elevensheep.gateway.gateway.GatewayVerticle",
            new DeploymentOptions().setInstances(1).setConfig(
                new JsonObject().put("api.gateway.http.address", "localhost").put("api.gateway.http.port", 8788)),
            re -> {
              if (re.succeeded()) {
                vertx.deployVerticle(new ManageVerticle(), manage->{
                  startPromise.complete();
                  logger.info("started sucess");
                });
              } else {
                logger.error("wrong");
                startPromise.fail("error");
              }
            });

    
    Future<String> f2 =   vertx.deployVerticle(new Test2Verticle());
    Future<String> f1 =   vertx.deployVerticle(new Test1Verticle());

    CompositeFuture.all(f1, f2).setHandler(result -> {
      // business as usual
      logger.debug(Result.values());
    });
    //  vertx.deployVerticle(new Test1Verticle());
  }

}
