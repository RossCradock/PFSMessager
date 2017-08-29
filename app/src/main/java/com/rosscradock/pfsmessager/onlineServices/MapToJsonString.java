package com.rosscradock.pfsmessager.onlineServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MapToJsonString {

    public static String get(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static String getArray(String[] array) throws UnsupportedEncodingException{
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(String entry : array){
            if(first){
                first = false;
            }else{
                builder.append("+");
            }
            builder.append(URLEncoder.encode(entry, "UTF-8"));
        }
        return builder.toString();
    }
}
