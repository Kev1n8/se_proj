package com.codeisright.attendance.utils;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class RandomIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // 生成随机数（6位）
        int randomId = new Random().nextInt(900000) + 100000;
        return String.valueOf(randomId);
    }
}
