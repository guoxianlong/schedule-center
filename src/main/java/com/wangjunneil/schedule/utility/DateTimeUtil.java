package com.wangjunneil.schedule.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangjun on 8/2/16.
 */
public final class DateTimeUtil {
    private DateTimeUtil() {}

    public static String nowDateString(String format) {
        return dateFormat(new Date(), format);
    }

    public static String preDateString(String format, int days) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        now = calendar.getTime();
        return dateFormat(now, format);
    }

    public static String dateFormat(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date getExpireDate(long time, int expireIn) {
        Date date = new Date(time + expireIn * 1000);
        return date;
    }

    public static Date parseDateString(String dateTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date formatDateString(String dateTime, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
