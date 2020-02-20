package com.elevensheep.gateway.manage.serviceImpl;

import com.elevensheep.gateway.manage.service.Manage;
import com.elevensheep.gateway.manage.service.ManageService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManageServiceImpl implements ManageService {

    private final Logger logger = LogManager.getLogger(ManageServiceImpl.class);

    
    private MySQLPool client;

    public ManageServiceImpl(MySQLPool client){
        this.client = client;
    }
    
    @Override
    public ManageService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        logger.debug("initializePersistence");

        resultHandler.handle(Future.succeededFuture());

        return this;
    }

    @Override
    public ManageService addManage(Manage Manage, Handler<AsyncResult<Void>> resultHandler) {
        logger.debug("addManage");
        return this;
    }


    @Override
    public ManageService retrieveManage(String name, Handler<AsyncResult<JsonArray>> resultHandler) {
        logger.debug("retrieveManage");

        client.getConnection(conn -> {
            if(conn.succeeded()){
                conn.result().preparedQuery("SELECT * FROM routename WHERE host=?", Tuple.of(name), ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> rows = ar.result();
                        logger.debug("Got " + rows.size() + " rows ");
                        JsonArray jsonArray = new JsonArray();
                        for(Row row: rows){
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.put("id", row.getValue(0));
                            jsonObject.put("url", row.getValue(1));
                            jsonObject.put("host", row.getValue(2));
                            jsonObject.put("userid", row.getValue(3));
                            jsonArray.add(jsonObject);
                        }
                        resultHandler.handle(Future.succeededFuture(jsonArray));
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause().getMessage()));
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }
                    conn.result().close();
                });
            }else{
                resultHandler.handle(Future.failedFuture(conn.cause().getMessage()));
            }

        });
        return this;
    }

    @Override
    public ManageService retrieveAllManage(Handler<AsyncResult<JsonArray>> resultHandler) {
        
        client.getConnection(conn -> {
            if(conn.succeeded()){
                conn.result().preparedQuery("SELECT * FROM routename", ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> rows = ar.result();
                        logger.debug("Got " + rows.size() + " rows ");
                        JsonArray jsonArray = new JsonArray();
                        for(Row row: rows){
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.put("id", row.getValue(0));
                            jsonObject.put("url", row.getValue(1));
                            jsonObject.put("host", row.getValue(2));
                            jsonObject.put("userid", row.getValue(3));
                            jsonArray.add(jsonObject);

                        }
                        resultHandler.handle(Future.succeededFuture(jsonArray));
                        
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause().getMessage()));
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }
                    conn.result().close();
                });
            }else{
                resultHandler.handle(Future.failedFuture(conn.cause().getMessage()));
            }
        });
        return this;
    }
}