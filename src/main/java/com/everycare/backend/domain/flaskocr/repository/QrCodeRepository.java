package com.everycare.backend.domain.flaskocr.repository;

import java.io.IOException;
import com.google.zxing.WriterException;

public interface QrCodeRepository {
    byte[] generateQrCode(String link) throws IOException, WriterException;
}