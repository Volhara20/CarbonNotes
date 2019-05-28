package com.example.testcarbonnotes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class MyNotesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView itemlist;
    ArrayList<Item> items = new ArrayList();
    ItemAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Notes");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog commentDialog = new Dialog(MyNotesActivity.this);
                commentDialog.setContentView(R.layout.addnotedialog);
                Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
                okBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        EditText text = commentDialog.findViewById(R.id.body);
                        String textf = text.getText().toString();
                        if (!textf.isEmpty()) {
                            commentDialog.dismiss();
                            new CreateNote().execute(textf);
                        } else {
                            text.setHint("Invalid Note name");
                            text.setText("");
                        }
                    }
                });
                Button cancelBtn = commentDialog.findViewById(R.id.cancel);
                cancelBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        commentDialog.dismiss();
                    }
                });
                commentDialog.setCancelable(false);
                commentDialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu=navigationView.getMenu();


        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.Name);
        TextView email = header.findViewById(R.id.Email);
        GetAndSetUserInfo(name, email,menu);
        itemlist = findViewById(R.id.list);
        adapter = new ItemAdapter(this, R.layout.item, items, this);
        itemlist.setAdapter(adapter);
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Item selectedState = (Item) parent.getItemAtPosition(position);
                Intent intent = new Intent(MyNotesActivity.this, com.example.testcarbonnotes.EditText.class);
                intent.putExtra("title", selectedState.getName());
                intent.putExtra("text", selectedState.getText());
                startActivityForResult(intent, 1);
                //
                //new GetDiskState().execute();
            }
        };
        itemlist.setOnItemClickListener(itemListener);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        new GetDiskState().execute();
    }

    private void GetAndSetUserInfo(TextView name, TextView email,Menu menu) {
        UserInfoModel info = BD.GetUserInfo(MyNotesActivity.this);
        name.setText(info.Name);
        email.setText(info.Email);
        menu.findItem(R.id.text_account).setTitle(info.Email);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                new GetDiskState().execute();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new DeleteAllNotes().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.notes) {

        } else if (id == R.id.account) {
            DeleteAccountClick();
        } else if (id == R.id.logout) {
            LogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogOut() {
        BD.DeleteToken(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void edit(final String name) {
        final Dialog commentDialog = new Dialog(MyNotesActivity.this);
        commentDialog.setContentView(R.layout.addnotedialog);
        Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
        okBtn.setText("Rename");
        final EditText text = commentDialog.findViewById(R.id.body);
        text.setText(name);
        text.selectAll();
        text.setFocusable(true);
        text.requestFocus();
        commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String textf = text.getText().toString();
                if (!textf.isEmpty()) {
                    commentDialog.dismiss();
                    new EditTitle().execute(name, textf);
                } else {
                    text.setHint("Invalid Note name");
                    text.setText("");
                }
            }
        });
        Button cancelBtn = commentDialog.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                commentDialog.dismiss();
            }
        });
        commentDialog.setCancelable(false);
        commentDialog.show();
    }

    AlertDialog.Builder builder;

    public void delete(final String noteName) {
        builder = new AlertDialog.Builder(MyNotesActivity.this);
        builder.setTitle("Confirm");  // заголовок
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                new Delete().execute(noteName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private final String MainUrl = "https://carbonnotes.azurewebsites.net/";

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

    public void DeleteAccountClick() {
        final Dialog commentDialog = new Dialog(MyNotesActivity.this);

        commentDialog.setContentView(R.layout.addnotedialog);
        Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
        okBtn.setText("Delete");
        final EditText text = commentDialog.findViewById(R.id.body);
        text.requestFocus();
        text.setHint("Enter your password");
        commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = commentDialog.findViewById(R.id.body);
                String textf = text.getText().toString();
                if (!textf.isEmpty()) {
                    commentDialog.dismiss();
                    new DeleteAccount().execute(textf);
                } else {
                    text.setHint("Invalid folder name");
                    text.setText("");
                }
            }
        });
        Button cancelBtn = commentDialog.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                commentDialog.dismiss();
            }
        });
        commentDialog.setCancelable(false);
        commentDialog.show();

    }
    public class DeleteAllNotes extends AsyncTask<String, Integer, String> {
        String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/values/DeleteAll");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("confirm", "false");
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
                responseCode=responseCode+1;
            } catch (Exception e) {
                result=e.getMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
                new GetDiskState().execute();
            } else {
                Toast.makeText(MyNotesActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class DeleteAccount extends AsyncTask<String, Integer, String> {
        String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/Account/Delete");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("userPass", data[0]);
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
                LogOut();
            } else {
                Toast.makeText(MyNotesActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public class EditTitle extends AsyncTask<String, Integer, String> {
        String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/values/Rename");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("oldtitle", data[0]);
                postDataParams.put("newtitle", data[1]);
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
                new GetDiskState().execute();
            } else {
                Toast.makeText(MyNotesActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }
    private class Delete extends AsyncTask<String,Void,Integer>
    {
        @Override
        protected Integer doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            try {
                url = new URL(MainUrl + "api/values?noteName="+strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                responseCode = urlConnection.getResponseCode();
                return responseCode;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
        @Override
        protected void onPostExecute(Integer result)
        {
            new GetDiskState().execute();
            Toast.makeText(MyNotesActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
        }
    }
    public class CreateNote extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            StringBuffer response = new StringBuffer();
            String result="Ok";
            try {
                url = new URL(MainUrl + "api/values/CreateNote?NoteName=" + strings[0] + "&NoteText=");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                responseCode = urlConnection.getResponseCode();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                result=e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                result+=e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                result+=e.getMessage();
            }
            return result;
        }
        protected void onPostExecute(String result) {
            if(result.equals("Ok"))
            {
                new GetDiskState().execute();
            }else
            {
                Toast.makeText(MyNotesActivity.this,result,Toast.LENGTH_LONG).show();
            }
        }
    }
    public class GetDiskState extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                url = new URL(MainUrl + "api/values");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyNotesActivity.this));
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            NoteModel msg = new Gson().fromJson(result, NoteModel.class);
            items.clear();
            for(int i=0;i<msg.NoteName.length;i++)
            {
                items.add(new Item(msg.NoteName[i],msg.NoteText[i]));
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
}
