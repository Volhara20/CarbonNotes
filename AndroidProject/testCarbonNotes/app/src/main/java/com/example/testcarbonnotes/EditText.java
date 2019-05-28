package com.example.testcarbonnotes;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class EditText extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String text;
    String title;
    android.widget.EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title=getIntent().getStringExtra("title");
        text=getIntent().getStringExtra("text");
        setTitle(title);
        editText=findViewById(R.id.text_note);
        editText.setText(text);
    }
    private void SaveNote()
    {
        String nowtext=editText.getText().toString();
        if(text==null)
        {
            text="";
        }
        new EditNote().execute(title,nowtext,text);
    }
    @Override
    public void onBackPressed() {
        SaveNote();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id==android.R.id.home) {
            SaveNote();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private String MainUrl = "https://carbonnotes.azurewebsites.net/";
    private String CreateString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public class EditNote extends AsyncTask<String, Integer, String> {
String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/values/post");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(EditText.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("name", data[0]);
                postDataParams.put("newtext", data[1]);
                postDataParams.put("oldtext", data[2]);
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
            } catch (Exception e) {
                result=e.getMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(EditText.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
