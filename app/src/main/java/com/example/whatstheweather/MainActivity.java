package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.editText);
        resultTextView=findViewById(R.id.resultTextView);

    }

    public void getWeather(View view){




        try {
            DownloadTask task=new DownloadTask();
            String encodedCityName= URLEncoder.encode(editText.getText().toString(),"UTF-8");//this will endoce the string in utf format so if there is any city with space in betn will be handled
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+editText.getText().toString()+"&appid=1cb1a355b0d181db26de6f563959e0f3");

            //this will hide the keyboard when user clicks the button so that it wont cover the result
            InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();

        }


    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try{

                url= new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while (data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }

                Log.i("Result",result);
                return result;


            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        //above code happends in background while the below one will affect the UI
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("JSON",s+"jello");
            try{
                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");

                Log.i("weather content",weatherInfo);

                JSONArray arr=new JSONArray(weatherInfo);//we get the data in array

                String message="";
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=arr.getJSONObject(i);//parse the array

                    //assign the part with tags main and desc to strings
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){ //if not empty assign to final message
                        message+=main+": "+description+"\r\n";

                    }
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));

                }
                if(!message.equals("")){
                    resultTextView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();

                }

            }
            catch (Exception e){

                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }
    }
}