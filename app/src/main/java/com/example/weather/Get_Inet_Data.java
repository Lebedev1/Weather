package com.example.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Get_Inet_Data{
            Document doc = null;

            public Get_Inet_Data(String... path) throws ParserConfigurationException {
            BufferedReader reader;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder buf = new StringBuilder();
            String line;
            try {
            URL url = new URL(path[0]);
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

try{ while ((line = reader.readLine()) != null) { buf.append(line); } } catch (IOException e) {}

            doc = builder.parse(new InputSource(new StringReader(buf.toString())));
            } catch (Exception e) {}
        }
    public boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo gANInfo = cm.getActiveNetworkInfo();

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobInfo != null && mobInfo.isConnected()) || (gANInfo != null && gANInfo.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}