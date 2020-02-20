package com.elevensheep.gateway.manage.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

@VertxGen
@ProxyGen
public interface ManageService {

    /**
     * The name of the event bus service.
     */
    String SERVICE_NAME = "Manage-eb-service";

    /**
     * The address on which the service is published.
     */
    String SERVICE_ADDRESS = "service.Manage";

    /**
     * Initialize the persistence.
     */
    @Fluent
    public ManageService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    /**
     * Add a Manage to the persistence.
     */
    @Fluent
    public ManageService addManage(Manage Manage, Handler<AsyncResult<Void>> resultHandler);

    /**
     * Retrieve the Manage with certain `url`.
     */
    @Fluent
    public ManageService retrieveManage(String name, Handler<AsyncResult<JsonArray>> resultHandler);



    @Fluent
    public ManageService retrieveAllManage(Handler<AsyncResult<JsonArray>> resultHandler);

}