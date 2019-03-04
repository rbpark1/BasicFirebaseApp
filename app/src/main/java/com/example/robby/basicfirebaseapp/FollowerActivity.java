package com.example.robby.basicfirebaseapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.robby.basicfirebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FollowerActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;
    private List<String> followerList;
    private ArrayAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        listView = findViewById(R.id.follower_list);

        followerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(FollowerActivity.this, R.layout.map_list_item, followerList);
        listView.setAdapter(adapter);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("followers/" + authUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    collectFollowers((Map<String, Object>) dataSnapshot.getValue());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void collectFollowers(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            //Get user map
            String uid = item.getKey();
            //followerList.add(uid);
            addUserToList(uid);
        }
    }

    private void addUserToList(String uid){
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                followerList.add(user.getUsername());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
