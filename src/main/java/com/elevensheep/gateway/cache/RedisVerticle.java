package com.elevensheep.gateway.cache;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RedisVerticle extends AbstractVerticle{
    private final Logger LOGGER = LogManager.getLogger(RedisVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    EventBus eb = vertx.eventBus();

    Redis.createClient(vertx, config().getString("redis.path"))
      .connect(onConnect -> {
        if (onConnect.succeeded()) {
          LOGGER.debug("redis success");
          RedisConnection redisConnection = onConnect.result();
          RedisAPI redisAPI = RedisAPI.api(redisConnection);

          eb.consumer("redis-get", result ->{
            redisAPI.get(result.body().toString(), res  ->{
              if(res.succeeded()){
                LOGGER.debug(res.result().toString());
                result.reply(res.result().toString());
              }
              else {
                LOGGER.debug("write failed");
              }
            });
          });
          startPromise.complete();
        }else{
          startPromise.fail(onConnect.cause());
          LOGGER.error("redis connect failed" + onConnect.cause());
        }
      });

  }
}