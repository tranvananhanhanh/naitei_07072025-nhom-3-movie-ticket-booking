package com.naitei.group3.movie_ticket_booking_system.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPayUtils {

    public static String getPaymentUrl(Map<String, String> params, String hashSecret, String baseUrl) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        try {
            for (int i = 0; i < fieldNames.size(); i++) {
                String name = fieldNames.get(i);
                String value = params.get(name);
                if ((value != null) && (value.length() > 0)) {
                    hashData.append(name).append('=')
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                    query.append(URLEncoder.encode(name, StandardCharsets.UTF_8.toString())).append('=')
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                    if (i != fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        return baseUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    public static String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean verifyPayment(Map<String, String> params, String secureHash, String hashSecret) {
        // Tạo một bản sao để không ảnh hưởng dữ liệu gốc
        Map<String, String> fields = new HashMap<>(params);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // Xây lại chuỗi dữ liệu đã ký
        String signData = buildData(fields);
        String generatedHash = hmacSHA512(hashSecret, signData);
        System.out.println("Generated vnp_SecureHash: " + generatedHash);
        return generatedHash.equalsIgnoreCase(secureHash);
    }

    private static String buildData(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Mã hóa URL cho giá trị
                    sb.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (i != fieldNames.size() - 1) {
                        sb.append('&');
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
