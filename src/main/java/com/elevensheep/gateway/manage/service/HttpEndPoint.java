package com.elevensheep.gateway.manage.service;

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

	public HttpEndPoint() {
	}

}