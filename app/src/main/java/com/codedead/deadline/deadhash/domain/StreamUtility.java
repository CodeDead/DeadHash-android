package com.codedead.deadline.deadhash.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamUtility {

    private static final int BASE_BUFFER_SIZE = 1024;

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        final byte[] bytes = new byte[BASE_BUFFER_SIZE];
        int count;

        while ((count = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, count);
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }
}
