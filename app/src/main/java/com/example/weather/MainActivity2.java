package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.StringWriter;
import java.text.ParseException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView data1 = findViewById(R.id.data1), data2 = findViewById(R.id.data2);
        if(getIntent().getExtras()!= null)
        {
            String doc1 = getIntent().getExtras().getString("data1");
            String doc2 = getIntent().getExtras().getString("data2");
            data1.setText(doc1);
            data2.setText(doc2);
        }
    }
}