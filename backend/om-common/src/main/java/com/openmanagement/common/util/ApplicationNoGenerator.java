package com.openmanagement.common.util;

import cn.hutool.core.util.IdUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ApplicationNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ApplicationNoGenerator() {
    }

    public static String next(String prefix) {
        return prefix + LocalDateTime.now().format(DATE_FORMATTER) + IdUtil.getSnowflakeNextIdStr();
    }
}
