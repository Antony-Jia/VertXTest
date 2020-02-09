package com.elevensheep.gateway.cache.Impl;

import com.elevensheep.gateway.cache.RouteCacheService;
import com.elevensheep.gateway.manage.service.Manage;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.impl.RedisClient;
import java.util.List;


public class RedisCacheServiceImpl implements RouteCacheService{

    private final RedisAPI redisApi;

    public RedisCacheServiceImpl(Vertx vertx, RedisClient client) {
        this.redisApi = RedisAPI.api(client);
    }

    @Override
    public Future<List<Manage>> getRoute(String url){
        return null;
    }
}