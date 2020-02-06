package com.elevensheep.gateway.manage;

import com.elevensheep.gateway.manage.service.ManageService;
import com.elevensheep.gateway.manage.serviceImpl.ManageServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


public class ManageVerticle extends AbstractVerticle {

    private MySQLConnectOptions connectOptions;
    private MySQLPool client;
    private EventBus eventBus;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost").setDatabase("gateway")
                .setUser("root").setPassword("");
        // Pool Options
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        // Create the pool from the data object
        client = MySQLPool.pool(vertx, connectOptions, poolOptions);

        eventBus = vertx.eventBus();

        ManageService manageService = new ManageServiceImpl();

        new ServiceBinder(vertx).setAddress("manage-sql-service").register(ManageService.class, manageService);

        MessageConsumer<String> consumer = eventBus.consumer("api.manange.paths");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
            client.getConnection(conn -> {

                client.preparedQuery("SELECT * FROM user WHERE username=?", Tuple.of("jia"), ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> rows = ar.result();
                        System.out.println("Got " + rows.size() + " rows ");
                        message.reply(rows);
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }
                });
            });
        });

        startPromise.complete();
    }

}
