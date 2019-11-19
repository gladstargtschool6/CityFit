package com.example.cityfit.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    ListView simpleList, mListView;
    private FirebaseListAdapter mAdapter;

    private FirebaseAuth mAuth;

    RecyclerView friendList;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Leaderboard, DashboardFragment.FriendsHolder> adapter;
    LinearLayoutManager linearLayoutManager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
//    Intent intent = new Intent(root.getContext(), ListItemAdapter.class);
//    startActivity(intent);
        friendList = root.findViewById(R.id.friend_list);
        db =  FirebaseFirestore.getInstance();
        linearLayoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
       getFriendList();
//        textName = root.findViewById(R.id.textView1);
        return root;
    }

    private void getFriendList(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Query query = db.collection("leaderboard")
                .document(dateFormat.format(date)).collection("users").orderBy("seconds", Query.Direction.DESCENDING);

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
                friendsHolder.setLeaderboardValues(i+1, leaderboard.getUserId(), leaderboard.getSeconds());
            }
        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);
    }


    public class FriendsHolder extends RecyclerView.ViewHolder {
        private View view;

        public FriendsHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setLeaderboardValues(int rank, String name,Long seconds) {
            TextView textView = view.findViewById(R.id.textView1);
            TextView textView1 = view.findViewById(R.id.textView2);
            TextView textView2 = view.findViewById(R.id.textView3);

            seconds = seconds/(60*60);
            textView.setText(String.valueOf(rank));
            textView1.setText(name);
            textView2.setText(String.valueOf(seconds));
        }
    }

//    public class FriendsHolder extends RecyclerView.ViewHolder {
//
//
//
//
//
//
//     TextView textName;
//
//        public FriendsHolder(View itemView) {
//            super(itemView);
//            textName = itemView.findViewById(R.id.textView1);
//
//
//        }
//    }

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