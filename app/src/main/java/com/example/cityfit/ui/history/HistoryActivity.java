package com.example.cityfit.ui.history;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.cityfit.City;
import com.example.cityfit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    ProgressBar still;
    ProgressBar walking;
    ProgressBar running;
    ProgressBar onFoot;
    ProgressBar cycling;
    ProgressBar driving;
    ProgressBar tilting;
    ProgressBar unknown;
    String city;
    DocumentReference leaderboardNoteRef;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        city = getCity(getApplicationContext());
        if (city != null) {
            Log.d("MyCityNameNotification", city);
            leaderboardNoteRef = db.collection("leaderboard").document(city)
                    .collection(dateFormat.format(date)).document(userId);
        } else {
            leaderboardNoteRef = db.collection("leaderboard").document("unknown")
                    .collection(dateFormat.format(date)).document(userId);
            Log.d("MyCityNameNotification", "unknown");
        }


        leaderboardNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                           @RequiresApi(api = Build.VERSION_CODES.N)
                                                           @Override
                                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                               if (task.isSuccessful()) {
                                                                   DocumentSnapshot doc = task.getResult();
                                                                   if (doc != null) {

                                                                       still = findViewById(R.id.still);
                                                                       still.setMax(86400);
                                                                       still.setProgress(((Long)doc.get("still")).intValue(),true);
                                                                       walking = findViewById(R.id.walking);
                                                                       walking.setMax(86400);

                                                                       walking.setProgress(((Long)doc.get("walking")).intValue(),true);
                                                                       running = findViewById(R.id.running);
                                                                       running.setMax(86400);
                                                                       running.setProgress(((Long)doc.get("running")).intValue(),true);

                                                                       cycling = findViewById(R.id.cycling);
                                                                       cycling.setMax(86400);
                                                                       cycling.setProgress(((Long)doc.get("cycling")).intValue(),true);
                                                                       driving = findViewById(R.id.driving);
                                                                       driving.setMax(86400);
                                                                       driving.setProgress(((Long)doc.get("driving")).intValue(),true);
                                                                       tilting = findViewById(R.id.tilting);
                                                                       tilting.setMax(86400);
                                                                       tilting.setProgress(((Long)doc.get("tilting")).intValue(),true);
                                                                       unknown = findViewById(R.id.unknown);
                                                                       unknown.setMax(86400);
                                                                       unknown.setProgress(((Long)doc.get("unknown")).intValue(),true);

                                                                   }
                                                                   else{
                                                                       still = findViewById(R.id.still);
                                                                       still.setMax(86400);
                                                                       still.setProgress(0,true);
                                                                       walking = findViewById(R.id.walking);
                                                                       walking.setMax(86400);
                                                                       walking.setProgress(0,true);
                                                                       running = findViewById(R.id.running);
                                                                       running.setMax(86400);
                                                                       running.setProgress(0,true);

                                                                       cycling = findViewById(R.id.cycling);
                                                                       cycling.setMax(86400);
                                                                       cycling.setProgress(0,true);
                                                                       driving = findViewById(R.id.driving);
                                                                       driving.setMax(86400);
                                                                       driving.setProgress(0,true);
                                                                       tilting = findViewById(R.id.tilting);
                                                                       tilting.setMax(86400);
                                                                       tilting.setProgress(0,true);
                                                                       unknown = findViewById(R.id.unknown);
                                                                       unknown.setMax(86400);
                                                                       unknown.setProgress(0,true);

                                                                   }
                                                               }
                                                           }
                                                       });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getCity(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {


            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            Double longitude = 0.0;
            Double latitude = 0.0;
            for (String provider : providers) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            city = City.getAddress(getApplicationContext(), latitude, longitude);
//            Toast.makeText(this,"Welcome"+ city+"person!",Toast.LENGTH_LONG).show();
            Log.d("MY CITY NAME", city);
        }
        catch (NullPointerException e){
            Log.d("NULL", "NULL");
        }

        return city;
    }

}
