package com.elevensheep.gateway.graphql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphqlVerticle extends AbstractVerticle{

private final Logger logger = LogManager.getLogger(GraphqlVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        logger.debug("");
    }
}