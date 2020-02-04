package com.elevensheep.gateway.manage.serviceImpl;

import com.elevensheep.gateway.manage.service.Manage;
import com.elevensheep.gateway.manage.service.ManageService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class ManageServiceImpl implements ManageService {

    public ManageService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }

    public ManageService addManage(Manage Manage, Handler<AsyncResult<Void>> resultHandler) {
        return this;
    }

    public ManageService retrieveManage(String ManageId, Handler<AsyncResult<JsonObject>> resultHandler) {
        return this;
    }
}