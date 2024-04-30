package com.micro.demo.service;

import java.io.IOException;

public interface IPdfService {
    void generatePdf(Long pensumId) throws IOException;
}
