package com.nazir.realtimechat.common.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
