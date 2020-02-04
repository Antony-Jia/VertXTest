package com.elevensheep.gateway.manage.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Manage {
    private String payId;
    private Double payAmount;
    private Short paySource;
    private Long ManageTime;

    public Manage() {
        // Empty constructor
    }

    public Manage(JsonObject json) {
        // ManageConverter.fromJson(json, this);
    }

    public Manage(Manage other) {
        this.payId = other.payId;
        this.payAmount = other.payAmount;
        this.paySource = other.paySource;
        this.ManageTime = other.ManageTime;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        // ManageConverter.toJson(this, json);
        return json;
    }

    public Manage(Long payCounter, Double payAmount, Short paySource) {
        initId(payCounter);
        this.payAmount = payAmount;
        this.paySource = paySource;
        this.ManageTime = System.currentTimeMillis();
    }

    public String getPayId() {
        return payId;
    }

    public Manage setPayId(String payId) {
        this.payId = payId;
        return this;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public Manage setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
        return this;
    }

    public Short getPaySource() {
        return paySource;
    }

    public Manage setPaySource(Short paySource) {
        this.paySource = paySource;
        return this;
    }

    public Long getManageTime() {
        return ManageTime;
    }

    public Manage setManageTime(Long ManageTime) {
        this.ManageTime = ManageTime;
        return this;
    }

    void initId(Long counter) {
        if (counter < 0) {
            throw new IllegalStateException("Negative counter");
        }
        if (this.payId != null && !this.payId.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String datePrefix = LocalDate.now().format(formatter);
            int mod = 12 - (int) Math.sqrt(counter);
            char[] zeroChars = new char[mod];
            for (int i = 0; i < mod; i++) {
                zeroChars[i] = '0';
            }
            String zs = new String(zeroChars);
            this.payId = datePrefix + zs + counter;
        }
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}