package com.mrcodesniper.design;

import java.io.DataInputStream;
import java.io.IOException;

public class StreamUtils {

    /**
     * Decodes a UTF-8 string from the DataInputStream provided. @link(DataInoutStream#readUTF()) should be no longer used, because  @link(DataInoutStream#readUTF())
     * does not decode UTF-16 surrogate characters correctly.
     *
     * @param input The input stream from which to read the encoded string
     * @return a decoded String from the DataInputStream
     * decoding the encoded string.
     */
    protected String decodeUTF8(DataInputStream input) throws IOException
    {
        int encodedLength;
        try {
            encodedLength = input.readUnsignedShort();

            byte[] encodedString = new byte[encodedLength];
            input.readFully(encodedString);

            return new String(encodedString, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
