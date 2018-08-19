package com.peaceful.config.center.client;

import com.alibaba.fastjson.JSON;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

/**
 * Created by Jun on 2018/8/19.
 */
public class ServerIO {

    private String serverAddress;
    private String categoryId;

    public ServerIO(String serverAddress, String categoryName) {
        this.serverAddress = serverAddress;
        NameValuePair c = new BasicNameValuePair("cname", categoryName);
        ServerResponse response = request("get-categoryId", c);
        if (response.getCode() == 500) {
            throw new KVConfigException("init KVConfig instance fail, categoryName[" + categoryName + "] is not exist");
        }
        categoryId = String.valueOf(response.getData());
    }

    public String getValue(String key) {
        NameValuePair k = new BasicNameValuePair("k", key);
        NameValuePair c = new BasicNameValuePair("c", categoryId);
        ServerResponse response = request("get", c, k);
        if (response.getCode() == 200) {
            return response.getData().toString();
        } else if (response.getCode() == 500) {
            throw new KVConfigException(key + " is not exist!");
        } else {
            throw new KVConfigException(response.toString());
        }
    }

    public void putValue(String key, String value) {
        NameValuePair c = new BasicNameValuePair("c", categoryId);
        NameValuePair k = new BasicNameValuePair("k", key);
        NameValuePair v = new BasicNameValuePair("v", value);
        ServerResponse response = request("put", c, k, v);
        if (response.getCode() == 200) {
            return;
        } else if (response.getCode() == 500) {
            throw new KVConfigException(key + " is not exist!");
        } else {
            throw new KVConfigException(response.toString());
        }
    }

    private ServerResponse request(String url, NameValuePair... nameValuePairs) {
        String jsonData = null;
        try {
            jsonData = Request.Post(serverAddress + "/cs/" + url).bodyForm(nameValuePairs).execute().returnContent().asString();
        } catch (IOException e) {
            throw new RuntimeException("remote server error", e);
        }
        ServerResponse response = JSON.parseObject(jsonData, ServerResponse.class);
        return response;
    }
}
