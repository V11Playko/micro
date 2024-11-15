package com.micro.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface IPdfService {
    ByteArrayOutputStream generatePdf(Long pensumId) throws IOException;
}
