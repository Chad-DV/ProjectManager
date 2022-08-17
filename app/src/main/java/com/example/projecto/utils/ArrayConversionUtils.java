package com.example.projecto.utils;

public class ArrayConversionUtils {

    private final static String strSeparator = ", ";

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){
        if(str == null) {
            return null;
        }
        return str.split(strSeparator);
    }
}
