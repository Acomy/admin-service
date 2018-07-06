package com.bossien.adminservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 微服务之间调用帮助类
 * @author gb
 */
@Component
public class RestHelper {

    @Autowired
    public RestTemplate restTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private String serviceName;

    private String url;


    public static final String SERVICEDESC = "服务名不能为空";

    public static final String URLDESC = "URL不能为空";

    public static final String REQUESTDESC = "HTTPREQUEST不能为空";

    public RestHelper combinedAssembly(Parameter parameter) {
        this.serviceName = parameter.serviceName;
        this.url = parameter.url;
        return this;
    }


    /**
     * @param url          地址
     * @param serviceName  服务名
     * @param parameter    请求参数
     * @param responseType 返回类型
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public <T> T postForObject(String url, String serviceName, Object parameter, Class<T> responseType) throws JsonProcessingException {
        checkParameterByAssert();
        this.serviceName = serviceName;
        this.url = url;
        String value = mapper.writeValueAsString(parameter);
        HttpEntity<String> requestEntity = new HttpEntity<String>(value, initializationRequestHeads());
        ResponseEntity<T> tResponseEntity = restTemplate.postForEntity(requestUrls(), requestEntity, responseType);
        return tResponseEntity.getBody();
    }

    /**
     * @param url          地址
     * @param serviceName  服务名
     * @param responseType 返回类型
     * @param uriVariables 请求参数
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public <T> T getForObject(String url, String serviceName,Class<T> responseType, Map<String, ?> uriVariables) throws JsonProcessingException {
        checkParameterByAssert();
        this.serviceName = serviceName;
        this.url = url;
        HttpEntity<String> requestEntity = new HttpEntity<String>(initializationRequestHeads());
        ResponseEntity<T> exchange = restTemplate.exchange(requestUrls(), HttpMethod.GET, requestEntity, responseType, uriVariables);
        return exchange.getBody();
    }

    public <T> T postForObject(Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> tResponseEntity = restTemplate.exchange(requestUrls(), HttpMethod.POST, requestEntity, responseType);
        return tResponseEntity.getBody();
    }

    public <T> ResponseEntity<T> postForEntity(Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> tResponseEntity = restTemplate.exchange(requestUrls(), HttpMethod.POST, requestEntity, responseType);
        return tResponseEntity;
    }

    public <T> T postForObject(Object parameter, Class<T> responseType) throws JsonProcessingException {
        isNull(serviceName, SERVICEDESC);
        isNull(url, URLDESC);
        String value = mapper.writeValueAsString(parameter);
        HttpEntity<String> requestEntity = new HttpEntity<String>(value, initializationRequestHeads());
        ResponseEntity<T> tResponseEntity = restTemplate.exchange(requestUrls(), HttpMethod.POST, requestEntity, responseType);
        return tResponseEntity.getBody();
    }

    public <T> ResponseEntity<T> postForEntity(Object parameter, Class<T> responseType) throws JsonProcessingException {
        String asString = mapper.writeValueAsString(parameter);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>(asString, initializationRequestHeads());
        ResponseEntity<T> tResponseEntity = restTemplate.exchange(requestUrls(), HttpMethod.POST, stringHttpEntity, responseType);
        return tResponseEntity;
    }

    /**
     * @param responseType 返回类型
     * @param uriVariables 参数
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public <T> T getForObject(Map<String, ?> uriVariables, Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> exchange = restTemplate.exchange(requestUrls(), HttpMethod.GET, requestEntity, responseType, uriVariables);
        return exchange.getBody();
    }

    public <T> ResponseEntity<T> getForEntity(Map<String, ?> uriVariables, Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> exchange = restTemplate.exchange(requestUrls(), HttpMethod.GET, requestEntity, responseType, uriVariables);
        return exchange;
    }

    /**
     * @param responseType 返回类型
     * @param
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public <T> T getForObject(Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> exchange = restTemplate.exchange(requestUrls(), HttpMethod.GET, requestEntity, responseType);
        return exchange.getBody();
    }

    public <T> ResponseEntity<T> getForEntity(Class<T> responseType) throws JsonProcessingException {
        HttpEntity<String> requestEntity = getHttpEntity();
        ResponseEntity<T> exchange = restTemplate.exchange(requestUrls(), HttpMethod.GET, requestEntity, responseType);
        return exchange;
    }

    private HttpEntity<String> getHttpEntity() {
        isNull(serviceName, SERVICEDESC);
        isNull(url, URLDESC);
        return new HttpEntity<String>(initializationRequestHeads());
    }


    private HttpHeaders initializationRequestHeads() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    private String requestUrls() {
        return "http://" + serviceName + url;
    }

    private void checkParameterByAssert() {
        isNull(serviceName, SERVICEDESC);
        isNull(url, URLDESC);
    }

    private void isNull(Object o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(o + message);
        }
    }

    public static class Parameter {
        private String serviceName;

        private String url;


        public Parameter addServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Parameter addUrl(String url) {
            this.url = url;
            return this;
        }

    }
}
