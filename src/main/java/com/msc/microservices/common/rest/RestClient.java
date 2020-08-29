package com.msc.microservices.common.rest;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * rest请求客户端
 *
 * @author zjl
 */
public class RestClient extends RestTemplate {
    public RestClient(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }
}
