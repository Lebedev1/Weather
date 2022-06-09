package com.example.weather;
import java.text.ParseException;
import java.util.Date;

public class Get_Background {
    public int Set_weather_background(String[] weather_data_now) throws ParseException { int day_value=0;
        Format_Datetime format_datetime = new Format_Datetime();


        if(format_datetime.Between_date(
                format_datetime.string_data_to_datetime(weather_data_now[5], -60*60, "dd.MM.yyyy HH:mm:ss").getTime(),
                format_datetime.Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:s").getTime(),
                format_datetime.string_data_to_datetime(weather_data_now[5], 2*60*60, "dd.MM.yyyy HH:mm:ss").getTime()))
        {
            day_value=2;
        } else
        if(format_datetime.Between_date(
                format_datetime.string_data_to_datetime(weather_data_now[5], 2*60*60, "dd.MM.yyyy HH:mm:ss").getTime(),
                format_datetime.Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:s").getTime(),
                format_datetime.string_data_to_datetime(weather_data_now[6], -60*60, "dd.MM.yyyy HH:mm:ss").getTime()))
        {
            day_value=3;
        } else
        if(format_datetime.Between_date(
                format_datetime.string_data_to_datetime(weather_data_now[6], -60*60, "dd.MM.yyyy HH:mm:ss").getTime(),
                format_datetime.Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:s").getTime(),
                format_datetime.string_data_to_datetime(weather_data_now[6], 45*60, "dd.MM.yyyy HH:mm:ss").getTime()))
        {
            day_value=2;
        } else day_value=1;
        return  day_value;
    }
    public boolean Set_tipe_weather_background(String[] weather_data_now) {

        if ((Integer.parseInt(weather_data_now[10])%100==0) ||(Integer.parseInt(weather_data_now[10])>800 & (Integer.parseInt(weather_data_now[10])%100==1 || Integer.parseInt(weather_data_now[10])%100==2))) {
               return true; }
        else { return  false; }
    }
}
