package com.elevensheep.gateway.manage.service;

import com.elevensheep.gateway.manage.service.HttpEndPointConverter;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class HttpEndPoint {

    //host
    private String host;

    //port
    private String port;

    //path
    private String path;

    //gateWay url Id;
    private String urlId;

    //config key
    private String configId;

    //weight
    private Integer weight;


    public HttpEndPoint() {
    
    }

    public HttpEndPoint( JsonObject json){
        HttpEndPointConverter.fromJson(json, this);
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        HttpEndPointConverter.toJson(this, json);
        return json;
    }


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
    }
    
    public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}