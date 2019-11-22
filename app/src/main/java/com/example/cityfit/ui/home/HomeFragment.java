package com.example.cityfit.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cityfit.HomeScreen;
import com.example.cityfit.JSONWeatherParser;
import com.example.cityfit.LoginActivity;
import com.example.cityfit.R;
import com.example.cityfit.WeatherHttpClient;
import com.example.cityfit.data.model.Leaderboard;

import com.example.cityfit.data.model.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonIOException;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private Button mLogout;
    TextView timeAndCity;
    TextView temperature;
    TextView minutes;
    TextView goal;
    Long min = 0l;
    long value;
    TextView pressure;
    TextView humidity;

    DocumentReference leaderboardNoteRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        DocumentReference leaderboardNoteRef = db.collection("leaderboard")
                .document(dateFormat.format(date)).collection("users").document(userId);


        //setHasOptionsMenu(true);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        DecoView decoView = root.findViewById(R.id.dynamicArcView);

//        callAsynchronousTask();

        leaderboardNoteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        timeAndCity = root.findViewById(R.id.textDateAndCity);
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date date = new Date();
                        timeAndCity.setText(dateFormat.format(date));


                        minutes = root.findViewById(R.id.textMinutes);

                        if (doc.get("seconds") != null) {
                            min = ((Long) doc.get("seconds")) / 3600;
                        }
                        minutes.setText(String.valueOf(min));
                        value = 1440 - min;
                        String goalSet = (int) value + " Minutes to go";
                        goal = root.findViewById(R.id.textGoal);
                        goal.setText(goalSet);


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

//        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
//                .setRange(0, 50, 0)
//                .build();
//
//        int backIndex = decoView.addSeries(seriesItem);

        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 100, 0)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_OUTER, Color.parseColor("#22000000"), 0.4f))
                .setInset(new PointF(20f, 20f))
                .setShowPointWhenEmpty(true)
                .setInitialVisibility(false)
                .build();
        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#E12929"))
                .setRange(0, 1440, 0)
                .setInset(new PointF(20f, 20f))
                .build();

        int series2Index = decoView.addSeries(seriesItem1);
        int series1Index = decoView.addSeries(seriesItem2);


//        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
//        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
//            @Override
//            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
//                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
//            }

//            @Override
//            public void onSeriesItemDisplayProgress(float percentComplete) {
//
//            }
//        });
        decoView.addEvent(new DecoEvent.Builder(100)
                .setIndex(series2Index)
                .setDelay(500)
                .build());

        if (value <= 5) {
            decoView.addEvent(new DecoEvent.Builder(5f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 10) {
            decoView.addEvent(new DecoEvent.Builder(10f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 15) {
            decoView.addEvent(new DecoEvent.Builder(15f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 20) {
            decoView.addEvent(new DecoEvent.Builder(20f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 25) {
            decoView.addEvent(new DecoEvent.Builder(25f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 30) {
            decoView.addEvent(new DecoEvent.Builder(30f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 35) {
            decoView.addEvent(new DecoEvent.Builder(35f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 40) {
            decoView.addEvent(new DecoEvent.Builder(40f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 45) {
            decoView.addEvent(new DecoEvent.Builder(45f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 50) {
            decoView.addEvent(new DecoEvent.Builder(50f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 55) {
            decoView.addEvent(new DecoEvent.Builder(55f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 60) {
            decoView.addEvent(new DecoEvent.Builder(60f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 65) {
            decoView.addEvent(new DecoEvent.Builder(65f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 70) {
            decoView.addEvent(new DecoEvent.Builder(70f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 75) {
            decoView.addEvent(new DecoEvent.Builder(75f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 80) {
            decoView.addEvent(new DecoEvent.Builder(80f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 85) {
            decoView.addEvent(new DecoEvent.Builder(85f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 90) {
            decoView.addEvent(new DecoEvent.Builder(90f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 95) {
            decoView.addEvent(new DecoEvent.Builder(95f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        } else if (value <= 100) {
            decoView.addEvent(new DecoEvent.Builder(100f)
                    .setIndex(series1Index)
                    .setDelay(1000)
                    .build());
        }


        return root;


    }
}
//

//





