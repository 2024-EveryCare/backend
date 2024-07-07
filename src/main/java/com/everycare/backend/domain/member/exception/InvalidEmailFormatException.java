package com.everycare.backend.domain.member.exception;

import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.exception.BusinessException;

public class InvalidEmailFormatException extends BusinessException {
    public InvalidEmailFormatException(){
        super(ErrorCode.INVALID_EMAIL_FORMAT);
    }
}
