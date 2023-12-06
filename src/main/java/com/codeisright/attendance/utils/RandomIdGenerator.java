package com.codeisright.attendance.utils;

import java.util.Random;

public class RandomIdGenerator {
    private final static Random rand = new Random();


    public static String generate() {
        // 生成随机数（6位）
        int randomId = rand.nextInt(900000) + 100000;
        return String.valueOf(randomId);
    }
}