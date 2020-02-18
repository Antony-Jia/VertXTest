package com.elevensheep.gateway.cache;

import com.elevensheep.gateway.cache.Impl.RedisCacheServiceImpl;
import com.elevensheep.gateway.manage.service.Manage;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.RedisClient;
import java.util.List;
import java.util.Map;




public interface RouteCacheService {
   
  static RouteCacheService createService(Vertx vertx, RedisClient client) {
    return new RedisCacheServiceImpl(vertx, client);
  }

  public Future<List<Manage>> getRoute(String url);

  public Future<Response> getValue(String key);

  public Future<Response> update(String key, String value);

  public Future<Response> getMap(String key);

  public Future<Response> updateMap(Map<String, String> map);
}