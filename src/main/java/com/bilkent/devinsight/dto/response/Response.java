package com.bilkent.devinsight.dto.response;

import com.bilkent.devinsight.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Response {

    public static ResponseEntity<Object> create(String message, HttpStatus status, Object data) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status.value());
        response.put("data", data);

        return new ResponseEntity<Object>(response, status);
    }

    public static ResponseEntity<Object> create(String message, HttpStatus status) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status.value());

        return new ResponseEntity<Object>(response, status);
    }

    public static ResponseEntity<Object> create(String message, int status) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status);

        return new ResponseEntity<Object>(response, HttpStatusCode.valueOf(status));
    }


    public static ResponseEntity<Object> create(BaseException baseException) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        HttpStatus httpStatus = baseException.getHttpStatus();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", baseException.getMessage());
        response.put("status", httpStatus.value());

        return new ResponseEntity<Object>(response, httpStatus);
    }


}