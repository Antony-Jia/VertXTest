package com.elevensheep.gateway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import com.elevensheep.gateway.test.Test1Verticle;
import com.elevensheep.gateway.test.Test2Verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start();

    vertx
        .deployVerticle("com.elevensheep.gateway.gateway.GatewayVerticle",
            new DeploymentOptions().setInstances(1).setConfig(
                new JsonObject().put("api.gateway.http.address", "localhost").put("api.gateway.http.port", 8788)),
            re -> {
              if (re.succeeded()) {
                logger.info("started sucess");
              } else {
                logger.error("wrong");
              }
            });

    // vertx.deployVerticle(new Test1Verticle());
    // vertx.deployVerticle(new Test2Verticle());
  }

}
