package com.darkfactory.education.identityaccess.auth;

import java.io.ByteArrayOutputStream;

final class Base32Codec {

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

    private Base32Codec() {
    }

    static String encode(byte[] data) {
        if (data.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = data[0] & 0xFF;
        int next = 1;
        int bitsLeft = 8;

        while (bitsLeft > 0 || next < data.length) {
            if (bitsLeft < 5) {
                if (next < data.length) {
                    buffer <<= 8;
                    buffer |= data[next++] & 0xFF;
                    bitsLeft += 8;
                } else {
                    int pad = 5 - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }

            int index = (buffer >> (bitsLeft - 5)) & 0x1F;
            bitsLeft -= 5;
            builder.append(ALPHABET[index]);
        }

        return builder.toString();
    }

    static byte[] decode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TOTP secret must not be blank.");
        }

        String normalized = value.trim()
                .replace("=", "")
                .replace(" ", "")
                .replace("-", "")
                .toUpperCase();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int buffer = 0;
        int bitsLeft = 0;

        for (int index = 0; index < normalized.length(); index++) {
            int current = decodeCharacter(normalized.charAt(index));
            buffer = (buffer << 5) | current;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                outputStream.write((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return outputStream.toByteArray();
    }

    private static int decodeCharacter(char character) {
        if (character >= 'A' && character <= 'Z') {
            return character - 'A';
        }
        if (character >= '2' && character <= '7') {
            return character - '2' + 26;
        }
        throw new IllegalArgumentException("Unsupported Base32 character: " + character);
    }
}

