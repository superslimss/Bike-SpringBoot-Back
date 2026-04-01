package com.bike.util;

import java.util.List;

public class GeoUtil {

    /**
     * 射线法：判断点(lat,lng)是否在多边形内
     * polygon: List<double[]> 每个元素是 [lat,lng]
     */
    public static boolean pointInPolygon(double lat, double lng, List<double[]> polygon) {
        boolean inside = false;
        int n = polygon.size();
        if (n < 3) return false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = polygon.get(i)[0], xi = polygon.get(i)[1];
            double yj = polygon.get(j)[0], xj = polygon.get(j)[1];

            boolean intersect = ((yi > lat) != (yj > lat))
                    && (lng < (xj - xi) * (lat - yi) / ((yj - yi) + 1e-12) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }
}