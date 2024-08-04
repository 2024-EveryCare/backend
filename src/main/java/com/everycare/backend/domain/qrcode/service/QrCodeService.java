package com.everycare.backend.domain.qrcode.service;

import java.io.IOException;
import com.google.zxing.WriterException;

public interface QrCodeService {
    byte[] generateQrCode(String link) throws IOException, WriterException;
}