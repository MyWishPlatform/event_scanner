package com.glowstick.neocli4j;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

public class Application {
    public static void main(String[] args) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            NeoClient neoCli = new NeoClientImpl(httpClient, new URI("http://pyrpc1.neeeo.org:10332"));
            neoCli.getBlock("0x0127b06ab2bad58eb4bd476d6ca3103a21a01492cf770b9cdff5e23b1a7e7c94");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
