package com.example.weather;

import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import javax.xml.parsers.DocumentBuilderFactory;

public class Get_Weather_Data {
    String[] weather_data_now   = new String[17];
    String[][][] weather_data_5_day = new String[40][12][4];


    Format_Datetime format_datetime = new Format_Datetime();






    public String[] Weather_now(Document doc_now) throws ParseException { Date date_sun_rise, date_sun_set, lastupdate;
        NodeList timezone_elements = doc_now.getDocumentElement().getElementsByTagName("timezone");
        NodeList country_elements = doc_now.getDocumentElement().getElementsByTagName("country");
        // <timezone>10800</timezone>
        weather_data_now[9]=timezone_elements.item(0).getTextContent();

        NodeList city_name = doc_now.getDocumentElement().getElementsByTagName("city");
        // <city id="2643743" name="London">
        weather_data_now[12] = city_name.item(0).getAttributes().item(0).getNodeValue();
        weather_data_now[0]  = city_name.item(0).getAttributes().item(1).getNodeValue();
        if(country_elements.item(0).getTextContent()!="") weather_data_now[0]+=", "+country_elements.item(0).getTextContent();

        NodeList coord_elements = doc_now.getDocumentElement().getElementsByTagName("coord");
        // <coord lon="-74.01" lat="40.71"></coord>
        weather_data_now[16] = "lat="+coord_elements.item(0).getAttributes().item(1).getNodeValue();
        weather_data_now[16] += "&lon="+coord_elements.item(0).getAttributes().item(0).getNodeValue();

        NodeList temp_elements = doc_now.getDocumentElement().getElementsByTagName("temperature");
        //<temperature value="280.15" min="278.15" max="281.15" unit="kelvin"/>
        weather_data_now[1] = String.valueOf(Math.round(Double.parseDouble(temp_elements.item(0).getAttributes().item(0).getNodeValue())));

        NodeList humidity_list = doc_now.getDocumentElement().getElementsByTagName("humidity");
        // <humidity value="81" unit="%"/>
        weather_data_now[2] = humidity_list.item(0).getAttributes().item(0).getNodeValue();
        weather_data_now[2] += humidity_list.item(0).getAttributes().item(1).getNodeValue();


        NodeList pressure_list = doc_now.getDocumentElement().getElementsByTagName("pressure");
        // <pressure value="1012" unit="hPa"/>
        weather_data_now[3] = pressure_list.item(0).getAttributes().item(0).getNodeValue();
        weather_data_now[3] += " "+pressure_list.item(0).getAttributes().item(1).getNodeValue();

        NodeList feels_like_list = doc_now.getDocumentElement().getElementsByTagName("feels_like");
        //  <feels_like value="14.2" unit="celsius"></feels_like>
        weather_data_now[4] = feels_like_list.item(0).getAttributes().item(0).getNodeValue();

        NodeList sun_list = doc_now.getDocumentElement().getElementsByTagName("sun");
        // <sun rise="2020-08-25T03:06:22" set="2020-08-25T17:17:33"></sun>

        date_sun_rise = format_datetime.string_data_to_datetime(sun_list.item(0).getAttributes().item(0).getNodeValue().replace('T', '_'), Integer.parseInt(weather_data_now[9]), "yyyy-MM-dd_HH:mm:ss");
        date_sun_set = format_datetime.string_data_to_datetime(sun_list.item(0).getAttributes().item(1).getNodeValue().replace('T', '_'), Integer.parseInt(weather_data_now[9]), "yyyy-MM-dd_HH:mm:ss");
        weather_data_now[5] = format_datetime.get_DateTime(date_sun_rise);
        weather_data_now[6] = format_datetime.get_DateTime(date_sun_set);

        NodeList wind_list = doc_now.getDocumentElement().getElementsByTagName("speed");
        // <speed value="1" unit="m/s" name="Calm"></speed>
        weather_data_now[7] = wind_list.item(0).getAttributes().item(0).getNodeValue();

        NodeList weather_list = doc_now.getDocumentElement().getElementsByTagName("weather");
        //  <weather number="804" value="пасмурно" icon="04d"></weather>
        weather_data_now[8] = weather_list.item(0).getAttributes().item(1).getNodeValue();
        weather_data_now[10] = weather_list.item(0).getAttributes().item(0).getNodeValue();

        NodeList lastupdate_list = doc_now.getDocumentElement().getElementsByTagName("lastupdate");
        //  <lastupdate value="2020-08-25T05:26:32"></lastupdate>
        lastupdate = format_datetime.string_data_to_datetime(lastupdate_list.item(0).getAttributes().item(0).getNodeValue().replace('T', '_'), Integer.parseInt(weather_data_now[9]), "yyyy-MM-dd_HH:mm:ss");
        weather_data_now[11] = format_datetime.get_DateTime(lastupdate);

        NodeList wind_direction = doc_now.getDocumentElement().getElementsByTagName("direction");
        // <direction value="260" code="W" name="West"></direction>
        Element wind_direction_gold = (Element) wind_direction.item(0);
        NamedNodeMap wind_direction_gold_attr = wind_direction_gold.getAttributes();

        if(wind_direction_gold_attr.getLength()>0)
        {
            weather_data_now[13] = wind_direction.item(0).getAttributes().item(0).getNodeValue();
        } else weather_data_now[13] = toString().valueOf(0);

        return weather_data_now;
    }






    public String[][][] Weather_5_day(Document doc_5_day) throws ParseException {  int i=0;

        NodeList time_List = doc_5_day.getElementsByTagName("time");

        NodeList time_elements = doc_5_day.getDocumentElement().getElementsByTagName("time");                           // 1
        NodeList symbol_elements = doc_5_day.getDocumentElement().getElementsByTagName("symbol");                       // 2
        NodeList precipitation_elements = doc_5_day.getDocumentElement().getElementsByTagName("precipitation");         // 3
        NodeList windDirection_elements = doc_5_day.getDocumentElement().getElementsByTagName("windDirection");         // 4
        NodeList windSpeed_elements = doc_5_day.getDocumentElement().getElementsByTagName("windSpeed");                 // 5
        NodeList temperature_elements = doc_5_day.getDocumentElement().getElementsByTagName("temperature");             // 6
        NodeList feels_like_elements = doc_5_day.getDocumentElement().getElementsByTagName("feels_like");               // 7
        NodeList pressure_elements = doc_5_day.getDocumentElement().getElementsByTagName("pressure");                   // 8
        NodeList humidity_elements = doc_5_day.getDocumentElement().getElementsByTagName("humidity");                   // 9
        NodeList clouds_elements = doc_5_day.getDocumentElement().getElementsByTagName("clouds");                       // 10
        NodeList visibility_elements = doc_5_day.getDocumentElement().getElementsByTagName("visibility");               // 11
        NodeList tiomezone_elements = doc_5_day.getDocumentElement().getElementsByTagName("timezone");                  // 12

        weather_data_5_day[0][0][0]=tiomezone_elements.item(0).getTextContent();

        for (i = 0; i < time_List.getLength(); i++)
        {
            //    <time from="2020-08-03T12:00:00" to="2020-08-03T15:00:00">
            weather_data_5_day[i][1][1] = format_datetime.get_DateTime(format_datetime.string_data_to_datetime(time_elements.item(i).getAttributes().item(0).getNodeValue().replace('T', '_'), Integer.parseInt(weather_data_5_day[0][0][0]), "yyyy-MM-dd_HH:mm:ss"));
            weather_data_5_day[i][1][2] = format_datetime.get_DateTime(format_datetime.string_data_to_datetime(time_elements.item(i).getAttributes().item(1).getNodeValue().replace('T', '_'), Integer.parseInt(weather_data_5_day[0][0][0]), "yyyy-MM-dd_HH:mm:ss"));
            //    <symbol number="803" name="облачно с прояснениями" var="04d"></symbol>
            weather_data_5_day[i][2][1] = symbol_elements.item(i).getAttributes().item(1).getNodeValue();
            weather_data_5_day[i][2][2] = symbol_elements.item(i).getAttributes().item(2).getNodeValue();
            //    <precipitation probability="0.04"></precipitation>
            weather_data_5_day[i][3][1] = precipitation_elements.item(i).getAttributes().item(0).getNodeValue();
            //    <windDirection deg="185" code="S" name="South"></windDirection>
            weather_data_5_day[i][4][1] = windDirection_elements.item(i).getAttributes().item(0).getNodeValue();
            //    <windSpeed mps="4.74" unit="m/s" name="Gentle Breeze"></windSpeed>
            weather_data_5_day[i][5][1] = windSpeed_elements.item(i).getAttributes().item(0).getNodeValue();
            //    <temperature unit="celsius" value="23.11" min="22.18" max="23.11"></temperature>
            weather_data_5_day[i][6][1] = String.valueOf(Math.round(Double.parseDouble(temperature_elements.item(i).getAttributes().item(1).getNodeValue())));
            //    <feels_like value="20.91" unit="celsius"></feels_like>
            weather_data_5_day[i][7][1] = feels_like_elements.item(i).getAttributes().item(0).getNodeValue();
            //    <pressure unit="hPa" value="1013"></pressure>
            weather_data_5_day[i][8][1] = pressure_elements.item(i).getAttributes().item(1).getNodeValue();
            weather_data_5_day[i][8][1]+= pressure_elements.item(i).getAttributes().item(0).getNodeValue();
            //    <humidity value="55" unit="%"></humidity>
            weather_data_5_day[i][9][1] = humidity_elements.item(i).getAttributes().item(0).getNodeValue();
            weather_data_5_day[i][9][2] = humidity_elements.item(i).getAttributes().item(1).getNodeValue();
            //    <clouds value="облачно с прояснениями" all="60" unit="%"></clouds>
            weather_data_5_day[i][10][1] = clouds_elements.item(i).getAttributes().item(0).getNodeValue();
            weather_data_5_day[i][10][2] = clouds_elements.item(i).getAttributes().item(1).getNodeValue();
            weather_data_5_day[i][10][3] = clouds_elements.item(i).getAttributes().item(2).getNodeValue();
            //    <visibility value="10000"></visibility>
            weather_data_5_day[i][11][1] = visibility_elements.item(i).getAttributes().item(0).getNodeValue();
        }
        return weather_data_5_day;
    }



     public String[] Average_Min_Max_temperature(String[][][] weather_data_5_day, int current_time) throws ParseException { int temperature=0; int dimensions = 0; String[] average_min_max_temperature = new String[3];

         average_min_max_temperature[0]="0"; average_min_max_temperature[1]="1000"; average_min_max_temperature[2]="-1000";

         long now_date=new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_5_day[0][0][0])+current_time,"dd.MM.yyyy HH:mm:ss").getTime();
         long now_Hours=new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_5_day[0][0][0])+current_time,"dd.MM.yyyy HH:mm:ss").getHours();
         long now_Minutes=new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_5_day[0][0][0])+current_time,"dd.MM.yyyy HH:mm:ss").getMinutes();
         long now_Seconds=new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_5_day[0][0][0])+current_time,"dd.MM.yyyy HH:mm:ss").getSeconds();

         long now_Date=now_date-now_Hours*60*60*1000-now_Minutes*60*1000-now_Seconds*1000;

     for(int i=0; i < weather_data_5_day.length; i++)
     {
         if(format_datetime.Between_date(
                 format_datetime.string_data_to_datetime(weather_data_5_day[i][1][1], 0, "dd.MM.yyyy").getTime(),
                 new Date(now_Date).getTime(),
                 format_datetime.string_data_to_datetime(weather_data_5_day[i][1][2], 0, "dd.MM.yyyy HH:mm:ss").getTime()))
         {
             dimensions++;
             temperature+=Integer.parseInt(weather_data_5_day[i][6][1]);
             if(Integer.parseInt(weather_data_5_day[i][6][1])<Integer.parseInt(average_min_max_temperature[1])) average_min_max_temperature[1]=weather_data_5_day[i][6][1];
             if(Integer.parseInt(weather_data_5_day[i][6][1])>Integer.parseInt(average_min_max_temperature[2])) average_min_max_temperature[2]=weather_data_5_day[i][6][1];
         }
     }
     if(dimensions!=0)average_min_max_temperature[0] = String.valueOf(temperature/dimensions); else for(int i=0; i<3; i++) average_min_max_temperature[i] = "-";
     return average_min_max_temperature;
    }
}