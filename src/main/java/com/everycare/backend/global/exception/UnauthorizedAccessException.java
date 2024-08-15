package com.everycare.backend.global.exception;

import com.everycare.backend.global.common.ErrorCode;

public class UnauthorizedAccessException extends BusinessException{
    public UnauthorizedAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
