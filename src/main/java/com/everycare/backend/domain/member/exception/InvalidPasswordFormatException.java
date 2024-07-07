package com.everycare.backend.domain.member.exception;

import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.exception.BusinessException;

public class InvalidPasswordFormatException extends BusinessException {
    public InvalidPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}
