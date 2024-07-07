package com.everycare.backend.domain.member.exception;

import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(){
        super(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}
