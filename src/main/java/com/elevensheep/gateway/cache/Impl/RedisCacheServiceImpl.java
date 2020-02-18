package com.elevensheep.gateway.cache.Impl;

import com.elevensheep.gateway.cache.RouteCacheService;
import com.elevensheep.gateway.manage.service.Manage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.RedisClient;
import java.util.List;
import java.util.Map;


public class RedisCacheServiceImpl implements RouteCacheService{

    private final RedisAPI redisApi;

    public RedisCacheServiceImpl(Vertx vertx, RedisClient client) {
        this.redisApi = RedisAPI.api(client);
    }

    @Override
    public Future<List<Manage>> getRoute(String url){
        return null;
    }

	@Override
	public Future<Response> getValue(String key) {
        // TODO Auto-generated method stub
        Promise<Response> promise = Promise.promise();
        this.redisApi.get(key, handler->{
            if(handler.succeeded()){
                promise.complete(handler.result());
            }else{
                promise.fail(handler.cause());
            }
        });
		return promise.future();
	}

	@Override
	public Future<Response> update(String key, String value) {
		// TODO Auto-generated method stub
        return this.redisApi.setnx(key, value);
	}

	@Override
	public Future<Response> getMap(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Response> updateMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		return null;
	}
}