package com.example.cityfit.ui.notifications;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.cityfit.BackgroundDetector;
import com.example.cityfit.BuildConfig;
import com.example.cityfit.City;
import com.example.cityfit.Constants;
import com.example.cityfit.JSONWeatherParser;
import com.example.cityfit.R;
import com.example.cityfit.WeatherHttpClient;
import com.example.cityfit.data.model.HumanActivity;
import com.example.cityfit.data.model.Leaderboard;
import com.example.cityfit.data.model.Weather;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


//import android.support.v7.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    Integer VALUE;
    private String label;
    private int icon;
    private Integer confidence;
    private Integer seconds;
    private Handler mHandler;
    Boolean flagm;
    BroadcastReceiver broadcastReceiver;
    private String TAG = NotificationActivity.class.getSimpleName();
    private TextView txtActivity, txtConfidence;
    private ImageView imgActivity;
    private Button btnStartTrcking, btnStopTracking;
    private static boolean flag = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean mInitFlag;
    DocumentReference leaderboardNoteRef;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String city = City.getAddress(getApplicationContext(),latitude,longitude);
        Toast.makeText(getApplicationContext(), city,Toast.LENGTH_LONG).show();
//        String city = "Dublin,IE";
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        txtActivity = findViewById(R.id.txt_activity);
        txtConfidence = findViewById(R.id.txt_confidence);
        imgActivity = findViewById(R.id.img_activity);
        btnStartTrcking = findViewById(R.id.btn_start_tracking);
        btnStopTracking = findViewById(R.id.btn_stop_tracking);
        mInitFlag = false;
        mHandler = new Handler();

//        JSONWeatherTask task = new JSONWeatherTask();
//        task.execute();
        btnStartTrcking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mToastRunnable.run();

            }
        });

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    stopTracking();
                }
                catch (IOException e){
                    Log.e(TAG,"IO ");
                }

                mHandler.removeCallbacks(mToastRunnable);
            }
        });





        broadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    confidence = intent.getIntExtra("confidence", 0);

                    handleUserActivity(type, confidence);
                }
            }
        };
//        startTracking();


        Intent intent1 = new Intent(NotificationActivity.this, BackgroundDetector.class);
        startService(intent1);
    }


    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "This is a delayed toast", Toast.LENGTH_SHORT).show();
            startTracking();
            mHandler.postDelayed(this, 60000);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleUserActivity(int type, int confidence) {

         label = getString(R.string.activity_unknown);
         icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On Bicycle";
                icon = R.drawable.ic_on_bicycle;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_on_foot);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting";
                icon = R.drawable.ic_tilting;
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);
        if (confidence > Constants.CONFIDENCE) {
            export(label, Integer.toString(confidence));
            txtActivity.setVisibility(View.GONE);
            txtConfidence.setVisibility(View.GONE);
            imgActivity.setVisibility(View.GONE);

            txtActivity.setText(label);
            txtConfidence.setText("Confidence: " + confidence);
            imgActivity.setImageResource(icon);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void export(String label, String conf)  {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();


//        if(!flag){
//            if(label.equals("Unknown") || label.equals("Tilting") || label.equals("Still") || label.equals("On Foot") || label.equals("In Vehicle")){
//                int hour = ZonedDateTime.now().getHour();
//                int mins = ZonedDateTime.now().getMinute();
//                int caltime = (hour * 60 * 60) + (mins * 60);
//                VALUE = VALUE - caltime;
//            }
//        }
//        if(label.equals("walking") || label.equals("Running") || label.equals("On Bicycle")){
//            int hour = ZonedDateTime.now().getHour();
//            int mins = ZonedDateTime.now().getMinute();
//            int caltime = (hour * 60 * 60) + (mins * 60);
//            VALUE = caltime;
//        }







//        System.out.println(dateFormat.format(date));
//        LocalTime time = LocalTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String data = label + "," + conf + "," + dateFormat.format(date) + "\n";
        // store timestamp of activity in second and label.
        // if next activity is not the proper ones then take timestamp. Calculate timestamp difference.
        // add to leaderboard value.




            //saving the file into device
            // FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);

            // append to file
        try{
            FileOutputStream out = openFileOutput("data.csv", getApplicationContext().MODE_APPEND);

            out.write(data.getBytes());
            out.close();




//            File file = null;
//
////            File folder = new File("/sdcard/demo");
////            folder.mkdirs();
//            file = new File(getFilesDir() + "data.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }







    }



    private void csvToFirestore() throws IOException {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HumanActivity humanActivity = new HumanActivity();
        DocumentReference humanActivityNoteRef;


        try {



            FileInputStream fileInputStream = openFileInput("data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line =  "";
            ArrayList<Integer> arr = new ArrayList<>();
            while ((line = reader.readLine()) != null) {

                Log.e("ERROR", line);
               humanActivityNoteRef = db.collection("notes")
                        .document();
                // use comma as separator

                String[] values = line.split(",");




                    humanActivity.setLabel(values[0]);
                    humanActivity.setConfidence(Integer.parseInt(values[1]));
                    humanActivity.setUserId(userId);

                if(values[0].equals("Still") || values[0].equals("Tilting")){


                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        arr.add((hour * 60 * 60) + (minute * 60));


                    }
                    catch(Exception e){
                        //
                    }



                }


                humanActivityNoteRef.set(humanActivity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Created new note", "message");
                        } else {
                            Log.d("Created new note", "message");
                        }
                    }
                });



            }
             seconds = 0;
            for (Integer num : arr) {
                if(arr.size()<=1) {
                seconds = 3600; //make this seconds it is kept for
                }
                else {
                    seconds = num - seconds;
                }
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();

            leaderboardNoteRef = db.collection("leaderboard")
                    .document(dateFormat.format(date)).collection("users").document(userId);





            leaderboardNoteRef.get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot> () {
                @Override
                public void onComplete(@NonNull Task < DocumentSnapshot > task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc!=null) {
                            Long oldValue = (Long) doc.get("seconds");
                            if(oldValue!=null) {
                                final Long val = oldValue + getSeconds();
                                setValue(val, userId);
                            }
                            else {
                                oldValue = 0l;
                                final Long val = oldValue + getSeconds();
                                setValue(val,userId);
                            }
                        }
                        else {
                            Long oldValue = 0l;
                            final Long val = oldValue + getSeconds();
                            setValue(val,userId);
                        }

                    }
                }


            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.getStackTraceString(e.fillInStackTrace());
                            Log.e("REACHEDHERE", "HAHAHA");
                        }
                    });






            Toast.makeText(this,seconds.toString(),Toast.LENGTH_LONG).show();
            deleteFile("data.csv");


        }
        catch (FileNotFoundException e) {
            Log.e("NOTACT", e.getMessage());
        }
    }

    private void setValue(Long val,String userId){

//        Weather weather = new Weather();
//        String data = ((new WeatherHttpClient()).getWeatherData());
//
//        if (data != null) {
//            try {
//                if(JSONWeatherParser.getWeather(data)!=null) {
//                    weather = JSONWeatherParser.getWeather(data);
//                }
//                // Let's retrieve the icon
////                weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Leaderboard leaderboard = new Leaderboard();
//        leaderboard.setUserId(userId);
//        leaderboard.setSeconds(val);
//        leaderboard.setWeather(weather);
//
//        leaderboardNoteRef.set(leaderboard).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d("Created new note", "message");
//                } else {
//                    Log.d("Created new note", "message");
//                }
//            }
//        });
  }

    public Integer getSeconds(){
        return seconds;
    }



    private void fileExport() {
        try {
            //exporting


            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", filelocation);
            Intent fileIntent = new Intent(android.content.Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
//            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
//            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "�C");
//            hum.setText("" + weather.currentCondition.getHumidity() + "%");
//            press.setText("" + weather.currentCondition.getPressure() + " hPa");
//            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
//            windDeg.setText("" + weather.wind.getDeg() + "�");

               // }

          //  }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {

        if (flag == false) {
            Intent intent1 = new Intent(NotificationActivity.this, BackgroundDetector.class);
            startService(intent1);

           //fileExport();
            flag=true;
        }
    }

    private void stopTracking() throws IOException{
        if(flag == true){
            txtActivity.setVisibility(View.VISIBLE);
            txtConfidence.setVisibility(View.VISIBLE);
            imgActivity.setVisibility(View.VISIBLE);
            fileExport();
            csvToFirestore();

            Intent intent = new Intent(NotificationActivity.this, BackgroundDetector.class);
            stopService(intent);
            flag = false;
        }
    }
}
