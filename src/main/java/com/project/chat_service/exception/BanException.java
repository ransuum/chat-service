package com.project.chat_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BanException extends RuntimeException{
    public BanException(String ex) {
        super(ex);
    }
}
