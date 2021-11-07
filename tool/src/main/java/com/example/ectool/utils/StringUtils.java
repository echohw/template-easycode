package com.example.ectool.utils;

/**
 * Created by AMe on 2021-09-22 20:58.
 */
public class StringUtils {
    
    public static boolean isNotBlank(String str) {
        return str != null && !"".equals(str.trim());
    }
    
}
