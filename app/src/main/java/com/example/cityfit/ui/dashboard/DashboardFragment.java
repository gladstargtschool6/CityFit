package com.example.cityfit.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cityfit.City;
import com.example.cityfit.HomeScreen;
import com.example.cityfit.R;
import com.example.cityfit.data.model.Leaderboard;
import com.example.cityfit.data.model.Leaderboard;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    ListView simpleList, mListView;
    private FirebaseListAdapter mAdapter;

    private FirebaseAuth mAuth;

    RecyclerView friendList;
    String city;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Leaderboard, DashboardFragment.FriendsHolder> adapter;
    LinearLayoutManager linearLayoutManager;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
//    Intent intent = new Intent(root.getContext(), ListItemAdapter.class);
//    startActivity(intent);
        friendList = root.findViewById(R.id.friend_list);
        db =  FirebaseFirestore.getInstance();
        linearLayoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
       getFriendList(root);
//        textName = root.findViewById(R.id.textView1);
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getFriendList(View context){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        HomeScreen homeScreen = new HomeScreen();
        String city = getCity(getContext());
        Query query;
        if (city != null) {
            Log.d("MyCityNameNotification", city);
           query= db.collection("leaderboard").document(city)
                    .collection(dateFormat.format(date)).orderBy("seconds", Query.Direction.DESCENDING);
        } else {
            query = db.collection("leaderboard").document("unknown")
                    .collection(dateFormat.format(date)).orderBy("seconds", Query.Direction.DESCENDING);
        }
//        Query query = db.collection("leaderboard")
//                .document(dateFormat.format(date)).collection("users").orderBy("seconds", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Leaderboard> response = new FirestoreRecyclerOptions.Builder<Leaderboard>()
                .setQuery(query, Leaderboard.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Leaderboard, DashboardFragment.FriendsHolder>(response) {
//            @Override
//            public void onBindViewHolder(ListItemAdapter.FriendsHolder holder, int position, Leaderboard model) {
//
//                holder.textName.setText(model.getName());
//                // holder.textEmail.setText(model.getEmail());
//
//
////                holder.itemView.setOnClickListener(v -> {
////                    Snackbar.make(friendList, model.getName()+", "+model.getTitle()+" at "+model.getCompany(), Snackbar.LENGTH_LONG)
////                            .setAction("Action", null).show();
////                });
//            }

            @NonNull
            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.listview_leaderboard, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }

            @Override
            protected void onBindViewHolder(@NonNull FriendsHolder friendsHolder, int i, @NonNull Leaderboard leaderboard) {
                if (leaderboard.getSeconds()!=null) {
                    friendsHolder.setLeaderboardValues(i + 1, leaderboard.getUserId(), leaderboard.getSeconds());
                }
                else{
                    friendsHolder.setLeaderboardValues(i + 1, leaderboard.getUserId(), 0);
                }
            }
        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String getCity(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            city = City.getAddress(getContext(), latitude, longitude);
//            Toast.makeText(this,"Welcome"+ city+"person!",Toast.LENGTH_LONG).show();
            Log.d("MY CITY NAME", city);
        }
        catch (NullPointerException e){
            Log.d("NULL", "NULL");
        }

        return city;
    }


    public class FriendsHolder extends RecyclerView.ViewHolder {
        private View view;

        public FriendsHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setLeaderboardValues(int rank, String name,long seconds) {
            TextView textView = view.findViewById(R.id.textView1);
            TextView textView1 = view.findViewById(R.id.textView2);
            TextView textView2 = view.findViewById(R.id.textView3);

            seconds = seconds/(60*60);
            textView.setText(String.valueOf(rank));
            textView1.setText(name);
            textView2.setText(String.valueOf(seconds));
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

}