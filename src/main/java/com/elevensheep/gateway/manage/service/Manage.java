package com.elevensheep.gateway.manage.service;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@DataObject(generateConverter = true)
public class Manage {
    private String Id;
    private String host;
    private String url;

    public Manage() {
        // Empty constructor
    }

    public Manage(JsonObject json) {
        ManageConverter.fromJson(json, this);
    }

    public Manage(Manage other) {
        this.Id = other.Id;
        this.host = other.host;
        this.url = other.url;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ManageConverter.toJson(this, json);
        return json;
    }

    public Manage(int payCounter, String host, String url) {
        initId(payCounter);
        this.host = host;
        this.url = url;
        //this.userId = System.currentTimeMillis();
    }

    public String getId() {
        return Id;
    }

    public Manage setId(String Id) {
        this.Id = Id;
        return this;
    }

    public String gethost() {
        return host;
    }

    public Manage sethost(String host) {
        this.host = host;
        return this;
    }

    public String geturl() {
        return url;
    }

    public Manage seturl(String url) {
        this.url = url;
        return this;
    }

    void initId(int counter) {
        if (counter < 0) {
            throw new IllegalStateException("Negative counter");
        }
        if (this.Id != null && !this.Id.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String datePrefix = LocalDate.now().format(formatter);
            int mod = 12 - (int) Math.sqrt(counter);
            char[] zeroChars = new char[mod];
            for (int i = 0; i < mod; i++) {
                zeroChars[i] = '0';
            }
            String zs = new String(zeroChars);
            this.Id = datePrefix + zs + counter;
        }
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}