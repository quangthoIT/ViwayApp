package com.example.test;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class InputStreamRequestBody extends RequestBody {
    private final String contentType;
    private final InputStream inputStream;

    public InputStreamRequestBody(String contentType, InputStream inputStream) {
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[2048];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            sink.write(buffer, 0, read);
        }
        inputStream.close();
    }
}

