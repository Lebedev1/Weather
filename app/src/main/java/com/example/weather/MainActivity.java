package com.example.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;



public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    Document doc_weather_now_activity, doc_weather_5_day_activity;
    ProgressBar sun_progress;

    MyLocation location;

    String[] weather_data_now = new String[17];
    String[][][] weather_data_5_day = new String[40][12][4];
    String city_id;

    long progress;

    int day_value=0;
    int i = 0;


    ArrayList<String> city_list;
    TextView city_name;
    TextView temp_now;
    TextView humidity;
    TextView pressure;
    TextView feels_like;
    TextView wind_speed_value;
    TextView sun_rise;
    TextView sun_set;
    TextView tipe_weather;
    TextView local_time;
    TextView local_date;

    TextView second_day_date;
    TextView third_day_date;
    TextView fourth_day_date;
    TextView fifth_day_date;

    TextView first_day_value, first_day_min_max;
    TextView second_day_value, second_day_min_max;
    TextView third_day_value, third_day_min_max;
    TextView fourth_day_value, fourth_day_min_max;
    TextView fifth_day_value, fifth_day_min_max;

    RelativeLayout first_day;
    RelativeLayout second_day;
    RelativeLayout third_day;
    RelativeLayout fourth_day;
    RelativeLayout fifth_day;

    RelativeLayout tool_bar;

    View tomorrow_divider;

    ImageView weather_back;
    ImageView weather_front;
    ImageView weather_background_first;
    ImageView weather_background_second;
    ImageView wind_direction;

    Intent add_city_activity;
    Intent main_activity2;


    Animation animation;

    SharedPreferences sp;
    Get_Weather_Data get_weather_data;

    boolean show_activity=false;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sun_progress = findViewById(R.id.sun_progress);
        sun_progress.setMax(1000);

        location = new MyLocation();

        get_weather_data = new Get_Weather_Data();
        add_city_activity = new Intent(this, AddCityActivity.class);
        main_activity2 = new Intent(this, MainActivity2.class);

        city_name = findViewById(R.id.city_name);
        temp_now = findViewById(R.id.temp_now);
        humidity = findViewById(R.id.humidity_value);
        wind_speed_value = findViewById(R.id.wind_speed_value);
        pressure = findViewById(R.id.pressure_value);
        feels_like = findViewById(R.id.feels_like_value);
        sun_rise = findViewById(R.id.sun_rise);
        sun_set = findViewById(R.id.sun_set);
        tipe_weather = findViewById(R.id.tipe_weather);
        weather_back = findViewById(R.id.weaher_back);
        weather_front = findViewById(R.id.weaher_front);
        local_time = findViewById(R.id.local_time);
        local_date = findViewById(R.id.local_date);

        second_day_date = findViewById(R.id.second_day_date);
        third_day_date = findViewById(R.id.third_day_date);
        fourth_day_date = findViewById(R.id.fourth_day_date);
        fifth_day_date = findViewById(R.id.fifth_day_date);

        tool_bar = findViewById(R.id.tool_bar);

        first_day_value = findViewById(R.id.first_day_value);        first_day_min_max = findViewById(R.id.first_day_min_max);         first_day = findViewById(R.id.first_day);
        second_day_value = findViewById(R.id.second_day_value);      second_day_min_max = findViewById(R.id.second_day_min_max);       second_day = findViewById(R.id.second_day);
        third_day_value= findViewById(R.id.third_day_value);         third_day_min_max = findViewById(R.id.third_day_min_max);         third_day = findViewById(R.id.third_day);
        fourth_day_value = findViewById(R.id.fourth_day_value);      fourth_day_min_max = findViewById(R.id.fourth_day_min_max);       fourth_day = findViewById(R.id.fourth_day);
        fifth_day_value = findViewById(R.id.fifth_day_value);        fifth_day_min_max = findViewById(R.id.fifth_day_min_max);         fifth_day = findViewById(R.id.fifth_day);

        tomorrow_divider=findViewById(R.id.tomorrow_divider);

        wind_direction = findViewById(R.id.wind_direction);
        weather_background_first = findViewById(R.id.weather_background_first);
        weather_background_second = findViewById(R.id.weather_background_second);

        city_list = new ArrayList<>();
        mSwipeRefreshLayout = findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animat);
        if (android.os.Build.VERSION.SDK_INT > 21) Set_Margins();

        show_activity=true;
        try { if (getIntent().getExtras().getString("city_id") != null) city_id =getIntent().getExtras().getString("city_id"); else
        {
            formatLocation();
        }
        } catch (NullPointerException e) {formatLocation();}
    }

    public void onStart() {
        setTheme(R.style.AppTheme);
        super.onStart();
        get_weather_data = new Get_Weather_Data();

    }


    @Override
    protected void onPause() {
        super.onPause();
        location.stopUpdate();
    }



    @SuppressLint("StringFormatMatches")
    @Override
    public void onResume() {
        super.onResume();
        show_activity=true;







        try {
            Load_Weather_Data();
//   get_weather_data();
            if(weather_data_now[0]==null) get_weather_data(); else Set_Content(); Set_5_day_Content();



            if (new Get_Inet_Data().hasConnection(this)) {
                if (new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").before(new Format_Datetime().string_data_to_datetime(weather_data_now[11], 30 * 60, "dd.MM.yyyy HH:mm:ss"))) {
                    String period;
                    long last_updated_period = (new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").getTime()- new Format_Datetime().string_data_to_datetime(weather_data_now[11], 0, "dd.MM.yyyy HH:mm:ss").getTime())/1000;
                    if(last_updated_period<60) period= last_updated_period + " " + String.format(getResources().getString(R.string.second)); else period =last_updated_period/60+ " " + String.format(getResources().getString(R.string.minute)) + " " + last_updated_period%60 + " " + String.format(getResources().getString(R.string.second));
                    Toast.makeText(this, String.format(getResources().getString(R.string.weather_updated), period), Toast.LENGTH_LONG).show();
                }
            } else { Toast.makeText(this, R.string.not_connection, Toast.LENGTH_SHORT).show(); }
        } catch (ParseException | NullPointerException | NumberFormatException | ParserConfigurationException e) {}
    }


    @Override
    public void onRefresh() {
        try {
            if (new Get_Inet_Data().hasConnection(this)) {
                Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();

                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wind_direction.animate().rotation(360);
                        get_weather_data();
                    }
                }, 1000);
            } else {
                Toast.makeText(this, R.string.not_connection, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } catch ( ParserConfigurationException e) {}
    }









    private void formatLocation() {
        MyLocation location = new MyLocation();
        location.SetUpLocationListener(this);
        if (location == null)
             city_id = "Нью-Йорк";
        else
         city_id = "lat="+location.imHere.getLatitude()+"&lon="+location.imHere.getLongitude();
        city_name.setText("lat="+location.imHere.getLatitude()+"&lon="+location.imHere.getLongitude());
        try {
            Load_Weather_Data();
            get_weather_data();
            if(weather_data_now[0]==null) get_weather_data(); else Set_Content(); Set_5_day_Content();



            if (new Get_Inet_Data().hasConnection(this)) {
                if (new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").before(new Format_Datetime().string_data_to_datetime(weather_data_now[11], 30 * 60, "dd.MM.yyyy HH:mm:ss"))) {
                    String period = null;
                    long last_updated_period = (new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").getTime()- new Format_Datetime().string_data_to_datetime(weather_data_now[11], 0, "dd.MM.yyyy HH:mm:ss").getTime())/1000;
                    if(last_updated_period<60) period= last_updated_period + " " + String.format(getResources().getString(R.string.second)); else period =last_updated_period/60+ " " + String.format(getResources().getString(R.string.minute)) + " " + last_updated_period%60 + " " + String.format(getResources().getString(R.string.second));
                    Toast.makeText(this, String.format(getResources().getString(R.string.weather_updated), period), Toast.LENGTH_LONG).show();
                }
            } else { Toast.makeText(this, R.string.not_connection, Toast.LENGTH_SHORT).show(); }
        } catch (ParseException | NullPointerException | NumberFormatException | ParserConfigurationException e) {}
    }





    public String Get_Url(String city_id, String tipe_data) {
        return "https://api.openweathermap.org/data/2.5/" + tipe_data + "?" + city_id + "&lang=" + Locale.getDefault().getLanguage() + "&units=metric&mode=xml&appid=c840168bc8c473cf3dbfe8c1c2ddaf24";
    }

    @SuppressLint("SetTextI18n")
    public void Set_Content() throws ParseException {
        Set_weather_back();

        city_name.setText(weather_data_now[0] + " | " + new Moon(weather_data_now[9]).moonAge() + " | " + new Moon(weather_data_now[9]).moonRise());
        temp_now.setText(weather_data_now[1]);
        humidity.setText(weather_data_now[2]);
        pressure.setText(weather_data_now[3]);
        feels_like.setText(weather_data_now[4] + "°С");
        tipe_weather.setText(weather_data_now[8].substring(0, 1).toUpperCase() + weather_data_now[8].substring(1));
        wind_speed_value.setText(weather_data_now[7]+ " " +String.format(getResources().getString(R.string.wind_speed_unit)));
        sun_rise.setText(new Format_Datetime().get_Custom_Format(new Format_Datetime().string_data_to_datetime(weather_data_now[5], 0, "dd.MM.yyyy HH:mm"), "HH:mm"));
        sun_set.setText(new Format_Datetime().get_Custom_Format(new Format_Datetime().string_data_to_datetime(weather_data_now[6], 0, "dd.MM.yyyy HH:mm"), "HH:mm"));

        if (Integer.parseInt(weather_data_now[13]) != 0)
            wind_direction.animate().rotation(Integer.parseInt(weather_data_now[13]));
        else wind_direction.setVisibility(View.GONE);

        TimeHandler.sendEmptyMessageDelayed(0, 0L);
    }

    public void Set_5_day_Content() throws ParseException { String[] average_min_max_temperature = new String[3];
        second_day_date.setText(upper_case(new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9])+2*24*60*60,"dd.MM.yyyy HH:mm:ss"), "EE"), 0,1));
        third_day_date.setText(upper_case(new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9])+3*24*60*60,"dd.MM.yyyy HH:mm:ss"), "EE"), 0,1));
        fourth_day_date.setText(upper_case(new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9])+4*24*60*60,"dd.MM.yyyy HH:mm:ss"), "EE"),0, 1));
        fifth_day_date.setText(upper_case(new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9])+5*24*60*60,"dd.MM.yyyy HH:mm:ss"), "EE"), 0,1));

        average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day, 24*60*60);
        if(average_min_max_temperature[0]!="-") { if(first_day.getVisibility()==View.GONE) first_day.setVisibility(View.VISIBLE);
            first_day_value.setText(average_min_max_temperature[0] + "°С");
            first_day_min_max.setText(average_min_max_temperature[1] + "/" + average_min_max_temperature[2] + "°С");
        } else first_day.setVisibility(View.GONE);
;
        average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day, 2*24*60*60);
        if(average_min_max_temperature[0]!="-") {
            if(second_day.getVisibility()==View.GONE) { second_day.setVisibility(View.VISIBLE); tomorrow_divider.setVisibility(View.VISIBLE); }
            second_day_value.setText(average_min_max_temperature[0]+"°С");
            second_day_min_max.setText(average_min_max_temperature[1]+"/"+average_min_max_temperature[2]+"°С");
        } else { second_day.setVisibility(View.GONE); tomorrow_divider.setVisibility(View.GONE); }

        average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day, 3*24*60*60);
        if(average_min_max_temperature[0]!="-") {
            if(third_day.getVisibility()==View.GONE) third_day.setVisibility(View.VISIBLE);
            third_day_value.setText(average_min_max_temperature[0]+"°С");
            third_day_min_max.setText(average_min_max_temperature[1]+"/"+average_min_max_temperature[2]+"°С");
        } else third_day.setVisibility(View.GONE);

        average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day, 4*24*60*60);
        if(average_min_max_temperature[0]!="-") {
            if(fourth_day.getVisibility()==View.GONE) fourth_day.setVisibility(View.VISIBLE);
            fourth_day_value.setText(average_min_max_temperature[0]+"°С");
            fourth_day_min_max.setText(average_min_max_temperature[1]+"/"+average_min_max_temperature[2]+"°С");
        } else fourth_day.setVisibility(View.GONE);

        average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day, 5*24*60*60);
        if(average_min_max_temperature[0]!="-") {
            if(fifth_day.getVisibility()==View.GONE) fifth_day.setVisibility(View.VISIBLE);
            fifth_day_value.setText(average_min_max_temperature[0]+"°С");
            fifth_day_min_max.setText(average_min_max_temperature[1]+"/"+average_min_max_temperature[2]+"°С");
        } else fifth_day.setVisibility(View.GONE);
    }




    public String upper_case(String text, int first_symbol_number, int last_symbol_number)
    {
        return text.substring(0,first_symbol_number) + text.substring(first_symbol_number, last_symbol_number).toUpperCase() + text.substring(last_symbol_number);
    }


    public void Output(Document doc) throws ParseException {
        weather_data_now = get_weather_data.Weather_now(doc);
        Save_City_list();
        Save_Weather_Data();
        Set_Content();
    }

    public void Output_5_day(Document doc_5_day) throws ParseException {
       weather_data_5_day = get_weather_data.Weather_5_day(doc_5_day);
       Save_Weather_5_Day_Data();
       Set_5_day_Content();
    }





    public void add_city(View view) {
        formatLocation();
        try {
            if (weather_data_now != null) add_city_activity.putExtra("data", weather_data_now);
        } catch (NullPointerException e) {}
        startActivity(add_city_activity);
        finish();
        overridePendingTransition(R.anim.anim_to_right_in_add_city, R.anim.anim_to_left_in_add_city);


    }

    public void data(View view) {
        if ( document_to_string(doc_weather_now_activity) != null) main_activity2.putExtra("data1",  document_to_string(doc_weather_now_activity));
        if ( document_to_string(doc_weather_5_day_activity) != null) main_activity2.putExtra("data2",  document_to_string(doc_weather_5_day_activity));

        startActivity(main_activity2);

    }
    public String document_to_string(Document doc)
    {
        StringWriter sw = new StringWriter();
        try {

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
        } catch (Exception ex) {}
        return sw.toString();
    }

    public void stAnimation() {
        weather_back.startAnimation(animation);
        weather_background_second.startAnimation(animation);
        weather_front.startAnimation(animation);
    }

    public boolean Save_Weather_Data() {

        sp = getSharedPreferences("City_weather", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("id="+weather_data_now[12] + "_size", weather_data_now.length);
        for (int i = 0; i < weather_data_now.length; i++) {
            mEdit1.remove("id="+weather_data_now[12] + "Weather_Data_" + i);
            mEdit1.putString("id="+weather_data_now[12] + "Weather_Data_" + i, weather_data_now[i]);
        }

        return mEdit1.commit();
    }

        public boolean Save_Weather_5_Day_Data() {

            sp = getSharedPreferences("City_weather_5_day", Context.MODE_PRIVATE);
            SharedPreferences.Editor mEdit5 = sp.edit();
            mEdit5.putInt("id="+weather_data_now[12] + "_size_1", weather_data_5_day.length);
            mEdit5.putInt("id="+weather_data_now[12] + "_size_2", weather_data_5_day[0].length);
            mEdit5.putInt("id="+weather_data_now[12] + "_size_3", weather_data_5_day[0][0].length);

            for (int i = 0; i < weather_data_5_day.length; i++) {
                for (int g = 0; g < weather_data_5_day[0].length; g++) {
                    for (int k = 0; k < weather_data_5_day[0][0].length; k++) {
                        mEdit5.remove("id=" + weather_data_now[12] + "Weather_Data_5_Day_" + i + "_" + g + "_" + k);
                        mEdit5.putString("id=" + weather_data_now[12] + "Weather_Data_5_Day_" + i + "_" + g + "_" + k, weather_data_5_day[i][g][k]);
                    }
                }
            }
            return mEdit5.commit();
    }
    public void Load_Weather_Data() {
        SharedPreferences mSharedPreference1 = getSharedPreferences("City_weather", Context.MODE_PRIVATE);
        int size = mSharedPreference1.getInt(city_id + "_size", 0);
        for (int i = 0; i < size; i++) {
            weather_data_now[i] = mSharedPreference1.getString(city_id + "Weather_Data_" + i, null);
        }

        SharedPreferences mSharedPreference5 = getSharedPreferences("City_weather_5_day", Context.MODE_PRIVATE);
        int size1 = mSharedPreference5.getInt(city_id + "_size_1", 0);
        int size2 = mSharedPreference5.getInt(city_id + "_size_2", 0);
        int size3 = mSharedPreference5.getInt(city_id + "_size_3", 0);
        for (int i = 0; i < size1; i++) {
            for (int g = 0; g < size2; g++) {
                for (int k = 0; k < size3; k++) {
                    weather_data_5_day[i][g][k] = mSharedPreference5.getString(city_id + "Weather_Data_5_Day_" + i + "_" + g + "_" + k, null);
                }
            }
        }
    }

    public void Load_City_list() {
        SharedPreferences mSharedPreference1 = getSharedPreferences("City_list", Context.MODE_PRIVATE);
        city_list.clear();
        int size = mSharedPreference1.getInt("City_list", 0);

        for (int i = 0; i < size; i++) {
            city_list.add(mSharedPreference1.getString("City_" + i, null));
        }
    }


    public boolean Save_City_list() {
        Load_City_list();
        sp = getSharedPreferences("City_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        return mEdit1.commit();
    }


    public void Set_weather_back() throws ParseException {
        weather_background_first.setImageDrawable(weather_background_second.getDrawable());
        day_value = new Get_Background().Set_weather_background(weather_data_now);

        if (Integer.parseInt(weather_data_now[10]) >= 200 & Integer.parseInt(weather_data_now[10]) <= 232) {
            weather_back.setImageResource(R.drawable.rain);
            weather_front.setImageResource(R.drawable.lightning);
        }
        if (Integer.parseInt(weather_data_now[10]) >= 300 & Integer.parseInt(weather_data_now[10]) <= 321) {
            weather_back.setImageResource(R.drawable.rain);
            weather_front.setImageResource(R.color.transparent);
        }
        if (Integer.parseInt(weather_data_now[10]) >= 500 & Integer.parseInt(weather_data_now[10]) <= 531) {
            weather_back.setImageResource(R.drawable.rain);
            weather_front.setImageResource(R.color.transparent);
        }
        if (Integer.parseInt(weather_data_now[10]) >= 600 & Integer.parseInt(weather_data_now[10]) <= 622) {
            weather_back.setImageResource(R.drawable.snow);
            weather_front.setImageResource(R.color.transparent);
        }
        if (Integer.parseInt(weather_data_now[10]) >= 700 & Integer.parseInt(weather_data_now[10]) <= 781) {
            weather_back.setImageResource(R.drawable.fog);
            weather_front.setImageResource(R.color.transparent);
        }
        if (Integer.parseInt(weather_data_now[10]) == 800) {
            weather_back.setImageResource(R.color.transparent);
            weather_front.setImageResource(R.color.transparent);
        }

        if (Integer.parseInt(weather_data_now[10]) >= 801 & Integer.parseInt(weather_data_now[10]) <= 804) {
            weather_back.setImageResource(R.drawable.cloud);
            weather_front.setImageResource(R.color.transparent);
        }

        if(new Get_Background().Set_tipe_weather_background(weather_data_now))
        {
            switch(day_value) {
                case 1: bg_night_clear(); break;
                case 2: bg_moning_evening_clear(); break;
                case 3: bg_day_clear(); break;
            }
        } else
        {
            switch(day_value) {
                case 1: bg_night_rain(); break;
                case 2: bg_moning_evening_rain(); break;
                case 3: bg_day_rain(); break;
            }
        }
        stAnimation();
    }


    public void bg_moning_evening_clear() { weather_background_second.setImageResource(R.drawable.bg_clear_moning_evening); }
    public void bg_day_clear() { weather_background_second.setImageResource(R.drawable.bg_clear_day); }
    public void bg_night_clear() { weather_background_second.setImageResource(R.drawable.bg_clear_night); }
    public void bg_moning_evening_rain() { weather_background_second.setImageResource(R.drawable.bg_rain_moning_evening); }
    public void bg_day_rain() { weather_background_second.setImageResource(R.drawable.bg_rain_day); }
    public void bg_night_rain() { weather_background_second.setImageResource(R.drawable.bg_rain_night); }


    public void Set_Margins() {
        ViewGroup.MarginLayoutParams tool_bar_view_LayoutParams = (ViewGroup.MarginLayoutParams) tool_bar.getLayoutParams();
        tool_bar_view_LayoutParams.setMargins(0, getStatusBarHeight(), 0, 0);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }




    @SuppressLint("StaticFieldLeak")
    class WeatherDataNow extends AsyncTask<String, Void, Document>  {

        @Override
        protected Document doInBackground(String... path) {
            try {
                doc_weather_now_activity = new Get_Inet_Data(path[0]).doc;
            } catch ( ParserConfigurationException e) {
            }
            return doc_weather_now_activity;
        }
            @Override
        protected void onPostExecute(Document doc_weather_now) {

            if (doc_weather_now != null) {
                Toast.makeText(getApplicationContext(), R.string.data_loaded, Toast.LENGTH_SHORT).show();

try { Output(doc_weather_now); } catch (ParseException e) {}

            } else {
                Toast.makeText(getApplicationContext(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            }
        }
    }





    @SuppressLint("StaticFieldLeak")
    class WeatherData5Day extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... path) {
            try {
                doc_weather_5_day_activity= new Get_Inet_Data(path[0]).doc;
            } catch (ParserConfigurationException e) {}
            return doc_weather_5_day_activity;
        }

        @Override
        protected void onPostExecute(Document doc_weather_5_day) {
            if (doc_weather_5_day != null) {
                try { Output_5_day(doc_weather_5_day); } catch (ParseException e) {}

            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void get_weather_data() {

        new WeatherDataNow().execute(Get_Url(city_id, "weather"));

        new WeatherData5Day().execute(Get_Url(city_id, "forecast"));
    }


    @SuppressLint("HandlerLeak")
    public Handler TimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            String l_time;
            String l_date;
            try {
                if (new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").after(new Format_Datetime().string_data_to_datetime(weather_data_now[11], 30 * 60, "dd.MM.yyyy HH:mm:ss")))
                {
                    if (i==0 & new Get_Inet_Data().hasConnection(MainActivity.this)) { get_weather_data(); }
                    if(i<100) i+=1; else i=0;
                }
                if (new Get_Background().Set_weather_background(weather_data_now) != day_value)  Set_weather_back();

                l_time = new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss"), "H:mm:ss");
                l_date = new Format_Datetime().get_Custom_Format(new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss"), "EE, d MMMM");
                long loc_date = new Format_Datetime().Local_city_date(Integer.parseInt(weather_data_now[9]),"dd.MM.yyyy HH:mm:ss").getTime();
                long sun_rise = new Format_Datetime().string_data_to_datetime(weather_data_now[5], 0, "dd.MM.yyyy HH:mm:ss").getTime();
                long sun_set = new Format_Datetime().string_data_to_datetime(weather_data_now[6], 0, "dd.MM.yyyy HH:mm:ss").getTime();
                progress = (loc_date - sun_rise) / ((sun_set - sun_rise) / 1000);
                local_date.setText(l_date.substring(0, 1).toUpperCase() + l_date.substring(1));
                local_time.setText(l_time.substring(0, 1).toUpperCase() + l_time.substring(1));
                sun_progress.setProgress((int) progress);

                city_name.setText(weather_data_now[0] + " | " + new Moon(weather_data_now[9]).moonAge() + " | " + new Moon(weather_data_now[9]).moonRise());

            } catch (ParserConfigurationException | ParseException e) {}
           if(show_activity) TimeHandler.sendEmptyMessageDelayed(0, 100L);
        }
    };

    @Override
    public void onStop() { super.onStop(); show_activity=false;    }

}