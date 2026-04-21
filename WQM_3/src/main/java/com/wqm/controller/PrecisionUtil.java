package com.wqm.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PrecisionUtil {

    private PrecisionUtil() {
    }

    public static Double round3(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
