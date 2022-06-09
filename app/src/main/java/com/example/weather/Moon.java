package com.example.weather;

import java.text.ParseException;
import java.util.Date;

public class Moon {
    private Date date;
    public Moon(String current_time)throws ParseException{
        this.date = new Format_Datetime().Local_city_date(Integer.parseInt(current_time), "dd.MM.yyyy HH:mm:ss:SSS");
    }
    public double moonAge(){
        double number_year, moon_age;
        double day_in_second;

        day_in_second = date.getHours()*60*60+date.getMinutes()*60+date.getSeconds()/(24*60*60/80000);
        day_in_second/=80000;

        for(number_year = (date.getYear()+1900)+1; number_year>19; number_year-=19);
        for(moon_age = (number_year*11)-14+date.getDate()+(date.getMonth()+1); moon_age>=30; moon_age-=30);
        return new MathOperation().round(moon_age + day_in_second, 2);
    }

    public double moonPhase(){
        double moonAge = moonAge();
        moonAge = Math.floor(moonAge);
        double L = ( 360 * moonAge ) / 29.53;
        return new MathOperation().round(Math.pow(Math.sin(Math.toRadians(L) / 2),  2), 4 );

    }

    public double moonRise(){
        return new MathOperation().round(moonAge() * 0.8 + 12 - 6, 2);
    }
}
