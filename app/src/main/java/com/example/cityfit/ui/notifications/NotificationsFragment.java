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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.cityfit.BackgroundDetector;
import com.example.cityfit.BuildConfig;
import com.example.cityfit.City;
import com.example.cityfit.Constants;
import com.example.cityfit.HomeScreen;
import com.example.cityfit.R;
import com.example.cityfit.data.model.HumanActivity;
import com.example.cityfit.data.model.Leaderboard;
import com.example.cityfit.ui.history.HistoryActivity;
import com.example.cityfit.ui.history.HistoryFragment;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class NotificationsFragment extends Fragment {
    Integer VALUE;
    private String label;
    private int icon;
    private Integer confidence;
    private Long seconds;
    private Handler mHandler;
    Boolean flagm;
    String city;
    BroadcastReceiver broadcastReceiver;
    private String TAG = NotificationActivity.class.getSimpleName();
    private TextView txtActivity, txtConfidence;
    private ImageView imgActivity;
    private Button btnStartTrcking, btnStopTracking;
    private static boolean flag = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference leaderboardNoteRef;

    private NotificationsViewModel notificationsViewModel;
    private Button mHistory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);

        String city = "Dublin,IE";
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        txtActivity = root.findViewById(R.id.txt_activity);
        txtConfidence = root.findViewById(R.id.txt_confidence);
        imgActivity = root.findViewById(R.id.img_activity);
        btnStartTrcking = root.findViewById(R.id.btn_start_tracking);
        btnStopTracking = root.findViewById(R.id.btn_stop_tracking);
//        mInitFlag = false;
        mHandler = new Handler();

//        JSONWeatherTask task = new JSONWeatherTask();
//        task.execute();
        mHistory = root.findViewById(R.id.buttonHistory);
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getContext(), HistoryActivity.class));
            }
        });


        btnStartTrcking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mToastRunnable.run();

            }
        });

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                try {
                    stopTracking();
                } catch (IOException e) {
                    Log.e(TAG, "IO ");
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


//        Intent intent1 = new Intent(NotificationActivity.this, BackgroundDetector.class);
//        startService(intent1);

        return root;
    }


    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getContext(), "This is a delayed toast", Toast.LENGTH_SHORT).show();
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
                label = "In Vehicle";
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
    public void export(String label, String conf) {

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
        try {
            FileOutputStream out = getActivity().openFileOutput("data.csv", getContext().MODE_APPEND);

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


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void csvToFirestore() throws IOException {


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HumanActivity humanActivity = new HumanActivity();
        DocumentReference humanActivityNoteRef;

        try {

            FileInputStream fileInputStream = getActivity().openFileInput("data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = "";
            ArrayList<Integer> arr = new ArrayList<>();
            HashMap<String, Long> map
                    = new HashMap<>();
            map.put("still", 0l);
            map.put("tilting", 0l);
            map.put("onFoot", 0l);
            map.put("walking", 0l);
            map.put("running", 0l);
            map.put("cycling", 0l);
            map.put("driving", 0l);
            map.put("unknown", 0l);

            while ((line = reader.readLine()) != null) {

                Log.e("ERROR", line);
                humanActivityNoteRef = db.collection("notes")
                        .document();
                // use comma as separator

                String[] values = line.split(",");

                humanActivity.setLabel(values[0]);
                humanActivity.setConfidence(Integer.parseInt(values[1]));
                humanActivity.setUserId(userId);

                if (values[0].equals("Still")) {


                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);

                        long sec = Integer.parseInt(values[1]) + (hour * 60 * 60) + (minute * 60) + map.get("still");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("still", sec);
                        }


                    } catch (Exception e) {
                        //
                    }


                } else if (values[0].equals("Tilting")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("tilting") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("tilting", sec);
                        }


                    } catch (Exception e) {
                        //
                    }


                } else if (values[0].equals("In Vehicle")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("driving") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("driving", sec);
                        }


                    } catch (Exception e) {
                        //
                    }

                } else if (values[0].equals("On Bicycle")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("cycling") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("cycling", sec);
                        }


                    } catch (Exception e) {
                        //
                    }
                } else if (values[0].equals("On Foot")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("onFoot") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("onFoot", sec);
                        }


                    } catch (Exception e) {
                        //
                    }

                } else if (values[0].equals("Running")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("running") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("running", sec);
                        }


                    } catch (Exception e) {
                        //
                    }

                } else if (values[0].equals("Walking")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("walking") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("walking", sec);
                        }


                    } catch (Exception e) {
                        //
                    }

                } else if (values[0].equals("Unknown")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = sdf.parse(values[2]);

                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        long sec = Integer.parseInt(values[1]) + map.get("unknown") + (hour * 60 * 60) + (minute * 60);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            map.replace("unknown", sec);
                        }


                    } catch (Exception e) {
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
//             seconds = 0;
//            for (Integer num : arr) {
//                if(arr.size()<=1) {
//                seconds = 3600; //make this seconds it is kept for
//                }
//                else {
//                    seconds = num - seconds;
//                }
//            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            HomeScreen homeScreen = new HomeScreen();
            String city = getCity(getContext());
            if (city != null) {
                Log.d("MyCityNameNotification", city);
                leaderboardNoteRef = db.collection("leaderboard").document(city)
                        .collection(dateFormat.format(date)).document(userId);
            } else {
                leaderboardNoteRef = db.collection("leaderboard").document("unknown")
                        .collection(dateFormat.format(date)).document(userId);
                Log.d("MyCityNameNotification", "unknown");
            }


            //store hash map into the value.

            leaderboardNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null) {
                            //get value of each and then add.
                            for (Map.Entry<String, Long> entry : map.entrySet()) {
                                Long val = 0l;
                                if (doc.get(entry.getKey()) != null) {
                                    val = entry.getValue() + (Long) doc.get(entry.getKey());
                                } else {
                                    val = Long.valueOf(entry.getValue());
                                }
                                map.replace(entry.getKey(), val);


                            }

                            Leaderboard leaderboard = new Leaderboard();
                            try {
                                seconds = 0l; // else i get error (attempt to invoke virtual method on null object
                                seconds = map.get("walking") + map.get("cycling") + map.get("running") + map.get("on Foot");
                            } catch (NullPointerException e) {
                                Log.e("Notification Actiivity", e.getLocalizedMessage());
                            }


                            leaderboard.setUserId(userId);
                            leaderboard.setCycling(map.get("cycling"));
                            leaderboard.setRunning(map.get("running"));
                            leaderboard.setDriving(map.get("driving"));
                            leaderboard.setOnFoot(map.get("onFoot"));
                            leaderboard.setStill(map.get("still"));
                            leaderboard.setTilting(map.get("tilting"));
                            leaderboard.setUnknown(map.get("unknown"));
                            leaderboard.setWalking(map.get("walking"));
                            leaderboard.setSeconds(seconds);
                            if (seconds != null) {
                                Toast.makeText(getContext(), seconds.toString(), Toast.LENGTH_LONG).show();
                            }

                            leaderboardNoteRef.set(leaderboard).addOnCompleteListener(new OnCompleteListener<Void>() {
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


            getActivity().deleteFile("data.csv");


        } catch (FileNotFoundException e) {
            Log.e("NOTACT", e.getMessage());
        }
    }

    //    private void setValue(Long val,String userId){
//
//
//
//  }
//
//    public Integer getSeconds(){
//        return seconds;
//    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getCity(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {


            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            Double longitude = 0.0;
            Double latitude = 0.0;
            for (String provider : providers) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return "unknown";
                }
                Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
                longitude = l.getLongitude();
                latitude = l.getLatitude();
            }
        }

        city = City.getAddress(getContext(), latitude, longitude);
//            Toast.makeText(this,"Welcome"+ city+"person!",Toast.LENGTH_LONG).show();
        Log.d("MY CITY NAME", city);
    }
    catch (NullPointerException e){
        Log.d("NULL", "NULL");
    }

    return city;
}


    private void fileExport() {
        try {
            //exporting


            Context context = getContext();
            File filelocation = new File(getActivity().getFilesDir(), "data.csv");
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
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {

        if (flag == false) {
            Intent intent1 = new Intent(getContext(), BackgroundDetector.class);
            getContext().startService(intent1);

            //fileExport();
            flag = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stopTracking() throws IOException {
        if (flag == true) {
            txtActivity.setVisibility(View.VISIBLE);
            txtConfidence.setVisibility(View.VISIBLE);
            imgActivity.setVisibility(View.VISIBLE);
            fileExport();
            csvToFirestore();

            Intent intent = new Intent(getContext(), BackgroundDetector.class);
            getContext().stopService(intent);
            flag = false;
        }
    }
}