package com.fenixcommunity.centralspace.app.rest.caller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class RestTemplateHelper {

    public static HttpEntity<Object> createHttpEntityWithHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(mediaType));
        return new HttpEntity<>(headers);
    }

}
