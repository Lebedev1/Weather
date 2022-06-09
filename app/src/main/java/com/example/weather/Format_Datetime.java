package com.example.weather;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Format_Datetime {
    public Date string_data_to_datetime(String string_data, int timezone, String pattern) throws ParseException {
        DateFormat format = new SimpleDateFormat(pattern); //2020-08-25T03:06:22
        Date datetime = format.parse(string_data);
        datetime.setTime(datetime.getTime()+timezone*1000);
        return datetime;
    }

    public String get_DateTime(Date data) {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        String date = df.format(data);
        return date;
    }
    public String get_Custom_Format(Date data, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        String date = df.format(data);
        return date;
    }
    public Date Local_city_date(int timezone, String pattern) throws ParseException {
       final Date currentTime = new Date();

        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return string_data_to_datetime(sdf.format(currentTime),timezone, pattern);
    }

    public boolean Between_date(long first_date, long now_date, long second_date) {

        if(now_date>=first_date & now_date<second_date)
            return true;
        else
            return false;
    }
}