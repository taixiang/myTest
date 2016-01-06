package com.overtake.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtil {

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(origin.getBytes()));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getApiName(String url) {
        int posstart = url.indexOf("/");
        int posend = url.indexOf("?");
        posstart = posstart > 0 ? posstart : 0;
        String apiname = posend > 0 ? url.substring(posstart, posend) : url.substring(posstart);
        return apiname;
    }

	/*
     * public static boolean checkEmail(String email) { String tag1 = "@";
	 * String tag2 = "."; if ((email.indexOf(tag1) != -1) &&
	 * (email.indexOf(tag2) != -1)) return true; else { return false; } }
	 */

    public static String toUtf8(String str) {
        try {
            byte[] strb = null;
            strb = str.getBytes("UTF-8");
            String newStr = new String(strb);
            return newStr;
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String removeAllToken(String originalString, String token) {
        int index = originalString.indexOf(token);
        while (index >= 0) {
            String str1 = originalString.substring(0, index);
            String str2 = originalString.substring(index + token.length());
            if (index == 0) {
                originalString = str2;
            } else if (index + token.length() == originalString.length()) {
                originalString = str1;
            } else {
                originalString = str1 + str2;
            }
            index = originalString.indexOf(token);
        }
        return originalString;
    }

    public static String replaceTokenWith(String originalString, String token, String replacement) {
        int index = originalString.indexOf(token);
        if (index != -1) {
            String ret = originalString.substring(0, index) + replacement + originalString.substring(index + token.length());
            return ret;
        } else {
            return originalString;
        }
    }

    public boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isChinese(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char word = str.charAt(i);
            if ((word >= 0x4e00) && (word <= 0x9fbb)) {
                continue;// �Ǻ���
            }
            return false;
        }
        return true;
    }

    public static boolean isContainChinese(String str) {
        if (str == null || str.trim().length() <= 0) {
            return false;
        }

        int len = str.length();
        for (int i = 0; i < len; i++) {
            char word = str.charAt(i);
            if ((word >= 0x4e00) && (word <= 0x9fbb)) {
                return true;
            }
        }
        return false;
    }

    public static int charLength(String str) {
        int size = str.length();
        int len = 0;
        for (int i = 0; i < size; i++) {
            char c = str.charAt(i);
            if ((c >= 0x4e00) && (c <= 0x9fbb)) {
                len += 2;// �Ǻ���
            } else {
                len += 1;
            }
        }
        return len;
    }

    /**
     * tell whether is string email format or not
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        if (TextUtils.isEmpty(strEmail)) {
            return false;
        }

        String strPattern = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * Parse the url string and return the host name<br>
     * For example:
     * <p/>
     * URL is "kaixin001://<u>qq_login</u>?access_token=abcdefgh"<br>
     * and the host is "<b>qq_login</b>"
     *
     * @param url original url string
     * @return
     */
    public static String getUrlHost(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        final String START_TAG = "//";
        final String END_TAG = "?";

        int start = url.indexOf(START_TAG);
        if (start < 0) {
            return null;
        }
        start += START_TAG.length();

        int end = url.indexOf(END_TAG, start);
        if (end < 0) {
            return url.substring(start);
        } else {
            return url.substring(start, end);
        }
    }

    public static String join(Collection<String> s, String delimiter) {
        if (s == null || s.isEmpty()) return "";
        Iterator<String> it = s.iterator();
        StringBuilder builder = new StringBuilder(it.next());
        while (it.hasNext()) {
            builder.append(delimiter).append(it.next());
        }
        return builder.toString();
    }
}
