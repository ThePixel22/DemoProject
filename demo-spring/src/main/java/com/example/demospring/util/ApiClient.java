package com.example.demospring.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApiClient {
    private String basePath;
    private final RestTemplate restTemplate;
    //private final HttpHeaders defaultHeaders = new HttpHeaders();
    private HttpStatus statusCode;
    private MultiValueMap<String, String> responseHeaders;


    public ApiClient(String basePath) {
        this.basePath = basePath;
        this.restTemplate = buildRestTemplate();
    }

    public <T> T invokeAPI(String path, HttpMethod method, MultiValueMap<String, String> queryParams,
                           Map<String, Object> uriVariables, Object body, HttpHeaders headerParams,
                           MultiValueMap<String, Object> formParams, List<MediaType> accept,
                           MediaType contentType, String[] authNames, ParameterizedTypeReference<T> returnType,
                           boolean isPrintLog) throws RestClientException {
        //updateParamsForAuth(authNames, queryParams, headerParams);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(basePath).path(path);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }

        if (uriVariables != null) {
            builder.uriVariables(uriVariables);
        }

        final RequestEntity.BodyBuilder requestBuilder = RequestEntity.method(method, builder.build().toUriString());
        if (accept != null) {
            requestBuilder.accept(accept.toArray(new MediaType[accept.size()]));
        }
        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }

        //addHeadersToRequest(headerParams, requestBuilder);
        //addHeadersToRequest(defaultHeaders, requestBuilder);

        RequestEntity<Object> requestEntity = requestBuilder.body(selectBody(body, formParams, contentType));

        if (isPrintLog) {
            log.info("\nRequest: {} \n", requestEntity);
        }

        ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, returnType);
        responseEntity.getStatusCode();
        if (isPrintLog) {
            log.info("\nResponse : {} \n", responseEntity);
        }
        statusCode = responseEntity.getStatusCode();
        responseHeaders = responseEntity.getHeaders();

        return responseEntity.getBody();
    }

    public List<MediaType> selectHeaderAccept(String[] accepts) {
        if (accepts.length == 0) {
            return null;
        }
        for (String accept : accepts) {
            MediaType mediaType = MediaType.parseMediaType(accept);
            if (isJsonMime(mediaType)) {
                return Collections.singletonList(mediaType);
            }
        }
        return MediaType.parseMediaTypes(StringUtils.joinWith(",", accepts));
    }

    public MediaType selectHeaderContentType(String[] contentTypes) {
        if (contentTypes.length == 0) {
            return MediaType.APPLICATION_JSON;
        }
        for (String contentType : contentTypes) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            if (isJsonMime(mediaType)) {
                return mediaType;
            }
        }
        return MediaType.parseMediaType(contentTypes[0]);
    }

    protected Object selectBody(Object obj, MultiValueMap<String, Object> formParams, MediaType contentType) {
        boolean isForm = MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType) || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType);
        return isForm ? formParams : obj;
    }

    protected void addHeadersToRequest(HttpHeaders headers, RequestEntity.BodyBuilder requestBuilder) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue();
            for (String value : values) {
                if (value != null) {
                    requestBuilder.header(entry.getKey(), value);
                }
            }
        }
    }

    protected RestTemplate buildRestTemplate() {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        //String timeoutStr = "30";
        int timeout = 30000;
        httpRequestFactory.setConnectionRequestTimeout(timeout);
        httpRequestFactory.setConnectTimeout(timeout);
        httpRequestFactory.setReadTimeout(timeout);

        try {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            httpRequestFactory.setHttpClient(httpClient);
        } catch (Exception e) {
            log.error("Error: ", e);
        }

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        // This allows us to read the response more than once - Necessary for debugging.
        DefaultResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
        restTemplate.setErrorHandler(errorHandler);
        //restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        //setConnectionTimeout(restTemplate, 60);
        return restTemplate;
    }

    public boolean isJsonMime(MediaType mediaType) {
        return mediaType != null && (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || mediaType.getSubtype().matches("^.*\\+json[;]?\\s*$"));
    }
}
