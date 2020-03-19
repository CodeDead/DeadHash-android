package com.codedead.deadhash.domain.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamUtility {

    private static final int BASE_BUFFER_SIZE = 1024;

    /**
     * Initialize a new StreamUtility
     */
    private StreamUtility() {
        // Empty constructor
    }

    /**
     * Copy a stream
     *
     * @param inputStream  The InputStream that should be copied
     * @param outputStream The OutputSteam that should contain the copied data
     * @throws IOException When the Stream could not be copied
     */
    public static void copyStream(final InputStream inputStream, final OutputStream outputStream) throws IOException {
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
