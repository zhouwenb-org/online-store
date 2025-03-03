package com.example.onlinestore.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private  static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDateStr(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }
}
