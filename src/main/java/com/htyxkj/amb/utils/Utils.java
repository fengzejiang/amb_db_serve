package com.htyxkj.amb.utils;

import org.springframework.util.StringUtils;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 19:26
 */
public class Utils {
    public static final char UNDERLINE_CHAR = '_';
    /**
     * 下划线转驼峰
     *
     * @param underlineStr
     * @return
     */
    public static String underline2Camel(String underlineStr) {

        if (underlineStr ==null||underlineStr.equals("")) {

            return null;
        }

        int len = underlineStr.length();
        StringBuilder strb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {

            char c = underlineStr.charAt(i);
            if (c == UNDERLINE_CHAR && (++i) < len) {

                c = underlineStr.charAt(i);
                strb.append(Character.toUpperCase(c));
            } else {

                strb.append(c);
            }
        }
        return strb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param camelStr
     * @return
     */
    public static String camel2Underline(String camelStr) {

        if (camelStr ==null||camelStr.equals("")) {

            return null;
        }

        int len = camelStr.length();
        StringBuilder strb = new StringBuilder(len + len >> 1);
        for (int i = 0; i < len; i++) {

            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {

                strb.append(UNDERLINE_CHAR);
                strb.append(Character.toLowerCase(c));
            } else {

                strb.append(c);
            }
        }
        return strb.toString();
    }
}
