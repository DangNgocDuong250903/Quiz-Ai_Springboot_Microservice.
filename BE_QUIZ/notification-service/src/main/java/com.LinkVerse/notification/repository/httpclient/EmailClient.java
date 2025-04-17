package com.LinkVerse.notification.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.LinkVerse.notification.dto.request.EmailRequest;
import com.LinkVerse.notification.dto.response.EmailResponse;

import feign.Headers;
import feign.RequestLine;

@FeignClient(name = "email-client", url = "${notification.email.brevo-url}")
public interface EmailClient {
    @RequestLine("POST /send")
    @Headers("Content-Type: application/json")
    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponse sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailRequest body);
}
