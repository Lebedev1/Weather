package com.example.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import org.w3c.dom.Document;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import static android.view.KeyEvent.KEYCODE_ENTER;










public class AddCityActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout add_city_layout;
    RelativeLayout location_city;
    Intent mainActivity;
    LinearLayout new_city;
    EditText name_new_city;
    ListView city_list_view;
    SharedPreferences sp;
    List<String> simple_city_list;
    String[] weather_data_now = new String[17];
    String[][][] weather_data_5_day = new String[40][12][4];
    String last_open_city;

    SwipeRefreshLayout mSwipeRefresh;
    Document doc_weather_now_activity, doc_weather_5_day_activity;

    TextView location_city_name;
    TextView location_weather_tipe;
    TextView location_now_temp;

    ImageView ic_location_city;

    boolean start_activity=false;






    public static final List<City> city_list = new ArrayList<>();

    public static class City {
        public final String city_id;
        public final String city_name;
        public final String now_temp;
        public final String weather_tipe;
        public final String min_temp;
        public final String max_temp;

        public City(String city_id, String city_name, String now_temp, String weather_tipe, String min_temp, String max_temp) {

            this.city_id = city_id;
            this.city_name = city_name;
            this.now_temp = now_temp;
            this.weather_tipe = weather_tipe;
            this.min_temp = min_temp;
            this.max_temp = max_temp;
        }
    }

    public class CityAdapter extends ArrayAdapter<City> {

        public CityAdapter(Context context) {
            super(context, R.layout.city_list_item, city_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            City city = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_list_item, null);
            }
            ((TextView) convertView.findViewById(R.id.city_name)).setText(city.city_name);
            ((TextView) convertView.findViewById(R.id.now_temp)).setText(city.now_temp);
            ((TextView) convertView.findViewById(R.id.weather_tipe)).setText(city.weather_tipe);
            ((TextView) convertView.findViewById(R.id.min_temp)).setText(city.min_temp+"/");
            ((TextView) convertView.findViewById(R.id.max_temp)).setText(city.max_temp+"°С");
            return convertView;
        }
        public String getCity_Id(int position)
        {
            return getItem(position).city_id;
        }
    }


















    @SuppressLint("ClickableViewAccessibility")
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city_main);

        mainActivity = new Intent(this, MainActivity.class);
        new_city= findViewById(R.id.new_city);
        add_city_layout = findViewById(R.id.add_city_layout);
        name_new_city = findViewById(R.id.name_new_city);
        city_list_view = findViewById(R.id.city_list);
        location_city = findViewById(R.id.location_city);
        simple_city_list = new ArrayList<String>();
        try { Load_City_list_weather(); } catch (ParseException e) { }
        city_list_view.setAdapter(new CityAdapter(this));
        mSwipeRefresh = findViewById(R.id.refresh_city_list_data);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.blue);

        location_city_name = findViewById(R.id.location_city_name);
        location_weather_tipe = findViewById(R.id.location_weather_tipe);
        location_now_temp = findViewById(R.id.location_now_temp);
        ic_location_city = findViewById(R.id.ic_location_city);


        getMyLocation();

        updateData();


    //  if(getIntent().getExtras()!= null)
   //     {
          //  weather_data_now = getIntent().getExtras().getStringArray("data");
             //    }
           if(android.os.Build.VERSION.SDK_INT>21)Set_Margins();


           city_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   mainActivity.putExtra("city_id", "id="+new CityAdapter(AddCityActivity.this).getCity_Id(position));
                   start_main_activity();


               }
           });
         city_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   delete_city(position);
                   return true;
               }
           });
           city_list_view.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromWindow(name_new_city.getWindowToken(), 0);
                   return false;
               }
           });
           name_new_city.setOnEditorActionListener( new TextView.OnEditorActionListener() {
               public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                   if( event != null && event.getKeyCode() == KEYCODE_ENTER){
                       add_city(name_new_city.getText().toString());
                       return true;
                   }
                   return false;
               }
           });

    }



    public void onRefresh() {
       updateData();
       getMyLocation();
    }

public void updateData(){
    try {
        if (new Get_Inet_Data().hasConnection(this)) {

            Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
            mSwipeRefresh.setRefreshing(true);
            mSwipeRefresh.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for(int i =0; i <simple_city_list.size(); i++) get_weather_data(simple_city_list.get(i));

                    Toast.makeText(getApplicationContext(), R.string.data_loaded, Toast.LENGTH_SHORT).show();
                }

            }, 1000);
        } else {
            Toast.makeText(this, R.string.not_connection, Toast.LENGTH_SHORT).show();}
    } catch ( ParserConfigurationException e) {}

    mSwipeRefresh.setRefreshing(false);
}

    public  void getMyLocation(){
        MyLocation location = new MyLocation();
        location.SetUpLocationListener(this);
        new WeatherDataNow_1().execute(Get_Url("lat="+location.imHere.getLatitude()+"&lon="+location.imHere.getLongitude(), "weather"));

    }

    public void add(View view)
    {
      add_city(toString().valueOf(name_new_city.getText()));
    }

    public void add_city(String name_city)
    {    start_activity=true;
        if(!city_list.contains(name_city) & name_city!=null) {
            get_weather_data("q="+name_city);
         //   mainActivity.putExtra("city_id", "id="+weather_data_now[12]);
         //   start_main_activity();
        }
    }

public void Set_Background_Location_City(String[] weather_data_now) throws ParseException {
    if(new Get_Background().Set_tipe_weather_background(weather_data_now))
    {
        switch(new Get_Background().Set_weather_background(weather_data_now)) {
            case 1: bg_night_clear(); break;
            case 2: bg_moning_evening_clear(); break;
            case 3: bg_day_clear(); break;
    }
    } else
    {
            switch(new Get_Background().Set_weather_background(weather_data_now)) {
                case 1: bg_night_rain(); break;
                case 2: bg_moning_evening_rain(); break;
                case 3: bg_day_rain(); break;
            }
        }

    location_city_name.setText(weather_data_now[0]);
    location_weather_tipe.setText(weather_data_now[8]);
    location_now_temp.setText(weather_data_now[1] + "°С");
    ic_location_city.setImageResource(R.drawable.my_location);


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

    public boolean Save_City_list() {
        sp = getSharedPreferences("City_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();

            mEdit1.putInt("City_list", simple_city_list.size());
            for (int i = 0; i < simple_city_list.size(); i++) {
                mEdit1.remove("City_" + i);
                mEdit1.putString("City_" + i, simple_city_list.get(i));
        }
            return mEdit1.commit();
    }



    public  void Load_City_list_weather() throws ParseException {
        String city_id;
        String city_name;
        String weathe_now_temp;
        String weather_tipe;
        String[] average_min_max_temperature = new String[3];
        SharedPreferences city    = getSharedPreferences("City_list", Context.MODE_PRIVATE);
        SharedPreferences weather = getSharedPreferences("City_weather", Context.MODE_PRIVATE);

        city_list.clear();
        simple_city_list.clear();
        int size = city.getInt("City_list", 0);
        SharedPreferences weather_5_day = getSharedPreferences("City_weather_5_day", Context.MODE_PRIVATE);


        for(int i=0;i<size;i++)
        {
            city_id = weather.getString(city.getString("City_" + i, null)+"Weather_Data_" + 12, null);
           // int size1 = weather_5_day.getInt(city_id + "_size_1", 0);
            int size1 = weather_5_day.getInt("id="+city_id + "_size_1", 0);
            int size2 = weather_5_day.getInt("id="+city_id + "_size_2", 0);
            int size3 = weather_5_day.getInt("id="+city_id + "_size_3", 0);

            weather_data_5_day[0][0][0] = weather_5_day.getString("id="+city_id + "Weather_Data_5_Day_" + 0 + "_" + 0 + "_" + 0, null);

            for (int k = 0; k < size1; k++) {
                weather_data_5_day[k][1][1] = weather_5_day.getString("id="+city_id + "Weather_Data_5_Day_" + k + "_" + 1 + "_" + 1, null);
                weather_data_5_day[k][1][2] = weather_5_day.getString("id="+city_id + "Weather_Data_5_Day_" + k + "_" + 1 + "_" + 2, null);
                weather_data_5_day[k][6][1] = weather_5_day.getString("id="+city_id + "Weather_Data_5_Day_" + k + "_" + 6 + "_" + 1, null);
            }
//            for (int l = 0; l < size1; l++) {
//                for (int g = 0; g < size2; g++) {
//                    for (int k = 0; k < size3; k++) {
//                        weather_data_5_day[l][g][k] = weather_5_day.getString("id="+city_id + "Weather_Data_5_Day_" + l + "_" + g + "_" + k, null);
//                    }
//                }
//            }
            average_min_max_temperature = new Get_Weather_Data().Average_Min_Max_temperature(weather_data_5_day,0);
            city_name = weather.getString(city.getString("City_" + i, null)+"Weather_Data_" + 0, null);
            weathe_now_temp = weather.getString(city.getString("City_" + i, null)+"Weather_Data_" + 1, null)+"°С";
            weather_tipe = weather.getString(city.getString("City_" + i, null)+"Weather_Data_" + 8, null);

            city_list.add( new City(city_id, city_name, weathe_now_temp,  weather_tipe,  average_min_max_temperature[1],  average_min_max_temperature[2] ));
            simple_city_list.add("id="+city_id);
        }
    }
    public void bg_day_clear() { add_city_layout.setBackgroundResource(R.drawable.bg_clear_day); }
    public void bg_day_rain(){ add_city_layout.setBackgroundResource(R.drawable.bg_rain_day); }
    public void bg_moning_evening_clear(){add_city_layout.setBackgroundResource(R.drawable.bg_clear_moning_evening);}
    public void bg_moning_evening_rain() {add_city_layout.setBackgroundResource(R.drawable.bg_rain_moning_evening);}
    public void bg_night_clear() {add_city_layout.setBackgroundResource(R.drawable.bg_clear_night);}
    public void bg_night_rain() { add_city_layout.setBackgroundResource(R.drawable.bg_rain_night); }

    private void update_city_list() throws ParseException
    {
        if (!simple_city_list.contains("id="+weather_data_now[12]))
        simple_city_list.add("id="+weather_data_now[12]);
        Save_City_list();
        Load_City_list_weather();
        city_list_view.setAdapter(new CityAdapter(this));
    }




    @Override public void onStop() {super.onStop(); finish();}
    @Override
    public void onBackPressed() {
         super.onBackPressed();
        mainActivity.putExtra("city_id", "id="+last_open_city);
        start_main_activity();
       }


    public void delete_city(final int position)
    {
        SharedPreferences.Editor mEdit1 = getSharedPreferences("City_list", Context.MODE_PRIVATE).edit();
        city_list.remove(position);
        simple_city_list.remove(position);
        Save_City_list();
        city_list_view.setAdapter(new CityAdapter(this));
        mEdit1.remove("City_" + position);
        last_open_city= new CityAdapter(AddCityActivity.this).getCity_Id(0);
    }
        public void Set_Margins()
        {
            ViewGroup.MarginLayoutParams name_new_city_LayoutParams = (ViewGroup.MarginLayoutParams) new_city.getLayoutParams();
            name_new_city_LayoutParams.setMargins(10, 20, 10, getNavigationBarHeight()+15);

            ViewGroup.MarginLayoutParams location_city_view_LayoutParams = (ViewGroup.MarginLayoutParams) location_city.getLayoutParams();
            location_city_view_LayoutParams.setMargins(10, getStatusBarHeight(), 10, 7);
        }






    @SuppressLint("StaticFieldLeak")
    class WeatherDataNow extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... path) {
            try { doc_weather_now_activity = new Get_Inet_Data(path[0]).doc;} catch ( ParserConfigurationException e) {}
            return doc_weather_now_activity;
        }
        @Override
        protected void onPostExecute(Document doc_weather_now) {
            if (doc_weather_now != null)
            {
                try { weather_data_now = new Get_Weather_Data().Weather_now(doc_weather_now); } catch (ParseException e) { e.printStackTrace(); }
            }
        }
    }





    @SuppressLint("StaticFieldLeak")
    class WeatherData5Day extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... path) {
            doc_weather_5_day_activity=null;
              try {
                  doc_weather_5_day_activity = new Get_Inet_Data(path[0]).doc;
            } catch ( ParserConfigurationException e) {}
              return doc_weather_5_day_activity;
        }

        @Override
        protected void onPostExecute(Document doc_weather_5_day) {
            if (doc_weather_5_day != null)
            {

try { weather_data_5_day = new Get_Weather_Data().Weather_5_day(doc_weather_5_day); } catch (ParseException e) {}

                Save_Weather_Data();
                Save_Weather_5_Day_Data();
                try { update_city_list(); } catch (ParseException e) { }

                if(start_activity)
                {
                    mainActivity.putExtra("city_id", "id="+weather_data_now[12]);
                    start_main_activity();
                }
            } else start_activity=false;
        }
    }


    public void start_main_activity()
    {
        startActivity(mainActivity);
        finish();
        overridePendingTransition(R.anim.anim_to_right, R.anim.anim_to_left);
    }


    public String Get_Url(String city_id, String tipe_data) { String lang = "&lang=" + Locale.getDefault().getLanguage();
        return  "https://api.openweathermap.org/data/2.5/" + tipe_data + "?" + city_id + lang + "&units=metric&mode=xml&appid=c840168bc8c473cf3dbfe8c1c2ddaf24";
       }

    public void get_weather_data(String city_id) {
        new AddCityActivity.WeatherDataNow().execute(Get_Url(city_id, "weather"));
        new AddCityActivity.WeatherData5Day().execute(Get_Url(city_id, "forecast"));
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
        int resourceId=getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    class WeatherDataNow_1 extends AsyncTask<String, Void, Document>  {

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
                try {
                    weather_data_now = new Get_Weather_Data().Weather_now(doc_weather_now);
                    last_open_city = weather_data_now[12];
                    Set_Background_Location_City(weather_data_now); } catch (NullPointerException | ParseException ex) { }

                } else {
                Toast.makeText(getApplicationContext(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
            }
        }
    }
}