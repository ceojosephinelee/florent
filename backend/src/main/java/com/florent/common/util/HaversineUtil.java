package com.florent.common.util;

import java.math.BigDecimal;

public final class HaversineUtil {

    private static final double EARTH_RADIUS_KM = 6_371.0;

    private HaversineUtil() {}

    public static boolean isWithinRadius(
            BigDecimal lat1, BigDecimal lng1,
            BigDecimal lat2, BigDecimal lng2,
            double radiusKm) {
        return distance(lat1, lng1, lat2, lng2) <= radiusKm;
    }

    public static double distance(
            BigDecimal lat1, BigDecimal lng1,
            BigDecimal lat2, BigDecimal lng2) {
        double radLat1 = Math.toRadians(lat1.doubleValue());
        double radLat2 = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                 + Math.cos(radLat1) * Math.cos(radLat2)
                 * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
