package com.ibm.liberty.starter.it.api.v1.utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class DownloadZip {
    public static Response get(String queryString) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?" + queryString;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        return response;
    }
}
