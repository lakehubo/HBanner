package com.lake.banner.uitls;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Util {
    /**
     * 加密一段文字
     */
    public static String md5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = digest.digest(content.getBytes("UTF-8"));
            StringBuilder builder = new StringBuilder();
            String hex = null;
            for (byte b : md5Bytes) {
                hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    builder.append("0").append(hex);
                } else {
                    builder.append(hex);
                }
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查文件的md5特征码
     */
    public static String encodeFile(String apkpath) {
        String md5 = null;
        try {
            File file = new File(apkpath);
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                String str = Integer.toHexString(b & 0xff);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            md5 = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }
}
