package com.saisaiwa.tspi.nas.common.util;

import cn.hutool.core.lang.id.NanoId;

import java.util.Random;

public class NanoIdUtil {

    private static final Random RANDOM = new Random();
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String requestTraceId() {
        return randomNanoId(10);
    }

    public static String randomNanoId(int length) {
        return NanoId.randomNanoId(RANDOM, ALPHABET, length);
    }

}
