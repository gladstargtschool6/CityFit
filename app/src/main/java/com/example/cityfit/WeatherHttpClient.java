package com.example.cityfit;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.RequiresApi;

public class WeatherHttpClient {


    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    // since APK 28, cleartext isn't supported hence have to add permission to manifest else this function will return null

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getWeatherData(String city) {


        String URL_main = BASE_URL + city +"&APPID=bc375e921d8d29a6a7679d3791091a08";
        HttpURLConnection con = null ;
        InputStream is = null;
        Log.d("Inside http ","TEST 1 ");
        try {
            Log.d("Inside http ","TEST 2 ");
            con = (HttpURLConnection) ( new URL(URL_main )).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            Log.e("Inside http ","TEST error ");
            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            Log.d("Inside http ","TEST 3 ");
            return buffer.toString();
        }
        catch(Exception e) {
            String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
            Log.e("Inside HTTP:",err);
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        Log.d("Inside http ","TEST 4");
        return null;


    }


}