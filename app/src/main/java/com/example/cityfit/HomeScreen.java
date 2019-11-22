package com.example.cityfit;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cityfit.data.model.Leaderboard;
import com.example.cityfit.data.model.Weather;
import com.example.cityfit.ui.history.HistoryFragment;
import com.example.cityfit.ui.home.HomeFragment;
import com.example.cityfit.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class HomeScreen extends AppCompatActivity {

    String TAG;
    private FirebaseAuth mAuth;
    private Button mLogout;
    private HomeViewModel homeViewModel;
    TextView timeAndCity;
    TextView temperature;
    TextView humidity;
    TextView clouds;
    TextView wind;
    TextView cityname;
    TextView description;
    TextView minutes;
    TextView goal;
    Long min = 0l;
    long value;
    DocumentReference leaderboardNoteRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String city;
    public String val;

//    @RequiresApi(api = Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing  permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        try {
//            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            double longitude = location.getLongitude();
//            double latitude = location.getLatitude();
//            city = City.getAddress(getApplicationContext(), latitude, longitude);
//            Toast.makeText(getApplicationContext(), city, Toast.LENGTH_LONG).show();
//            Log.d("MY CITY NAME", city);
//        } catch (NullPointerException e) {
//            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_LONG).show();
//        }

//        toolbar = findViewById(R.id.toolbar);
//
//
//        setSupportActionBar(toolbar);


        Toast.makeText(this,val,Toast.LENGTH_LONG).show();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        TAG = this.getClass().getName();

//        temperature = findViewById(R.id.textTemperature);
        callAsynchronousTask();

        // DocumentReference docRef = db.collection("cities").document("SF");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
//No one signed in
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        } else {
//User logged in
            // left to handle
        }


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.d(TAG, "CHECK IN");
                            JSONWeatherTask task = new JSONWeatherTask();
                            task.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 500); //execute in every 50000 ms
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getCity(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                 return "missing value";
            }

            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            Double longitude = 0.0;
            Double latitude = 0.0;
            for (String provider : providers) {
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

            city = City.getAddress(getApplicationContext(), latitude, longitude);
//            Toast.makeText(this,"Welcome"+ city+"person!",Toast.LENGTH_LONG).show();
            Log.d("MY CITY NAME", city);
        }
        catch (NullPointerException e){
            Log.d("NULL", "NULL");
        }

        return city;
    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            city = getCity(getApplicationContext());
            String data = ((new WeatherHttpClient()).getWeatherData(city));
            Log.d("entered do inback bef", "yes entered");
            if (data != null) {
                try {
                    wind = findViewById(R.id.wind);
                    clouds = findViewById(R.id.clouds);
                    humidity = findViewById(R.id.humidity);
                    cityname = findViewById(R.id.cityname);
                    description = findViewById(R.id.descriptionx);
                    temperature=findViewById(R.id.textTemperature);
                    Log.d("entered do inback", "yes entered");
                    weather = JSONWeatherParser.getWeather(data);
//                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    Date date = new Date();
//                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    leaderboardNoteRef = db.collection("leaderboard")
//                            .document(dateFormat.format(date)).collection("users").document(userId);

//                            temperature.setText(val);
//                    Leaderboard leaderboard = new Leaderboard();
//
//                    leaderboard.setUserId(userId);
//                    leaderboardNoteRef.set(leaderboard).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d("Created new note", "message");
//                            } else {
//                                Log.d("Created new note", "message");
//                            }
//                        }
//                    });

                } catch (JSONException e) {
//
                }
            }
            return weather;
        }


        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = new Date();
//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//            leaderboardNoteRef = db.collection("leaderboard")
//                    .document(dateFormat.format(date)).collection("users").document(userId);

//            final String val = String.valueOf(weather.temperature.getTemp());
//                            temperature.setText(val);

//            Leaderboard leaderboard = new Leaderboard();

//            leaderboard.setSeconds(0L);
//            leaderboard.setUserId(userId);
            Integer val = (int)(weather.temperature.getTemp() - 273.15);
            if (temperature!=null) {
                temperature.setText(String.valueOf(val));
            }
            if(wind!=null){
                wind.setText(String.valueOf(weather.wind.getSpeed()));
            }
            if(clouds!=null){
                clouds.setText(String.valueOf(weather.clouds.getPerc()));
            }
            if(humidity!=null){
                humidity.setText(String.valueOf(weather.currentCondition.getHumidity()));
            }
            if(cityname!=null){
                cityname.setText(String.valueOf(city));
            }
            if(description!=null){
                description.setText(String.valueOf(weather.currentCondition.getDescr()));
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference weatherRef = db.collection("weather")
                    .document(dateFormat.format(date)).collection("city").document(city).collection(userId).document();

            weatherRef.set(weather).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuLogout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));




                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = new MenuInflater(this);
        getMenuInflater();

        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }


}
