package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private Button btnFollow;
    private Button btnUnFollow;
    private ListView postListView;

    private DatabaseReference mDatabase;
    private FirebaseUser authUser;
    private String authUid;
    private String uid;

    private EntryAdapter adapter;
    private ArrayList<Map.Entry> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        btnFollow = findViewById(R.id.btnFollow);  //TODO check if already following
        btnUnFollow = findViewById(R.id.btnUnfollow);
        postListView = findViewById(R.id.userPostListView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // get authUser
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        // get user info to display
        uid = getIntent().getStringExtra("UID");
        // get posts
        postList = new ArrayList<>();
        adapter = new EntryAdapter(UserActivity.this, R.layout.map_list_item, postList, "title");
        postListView.setAdapter(adapter);

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check if already following
                // add uid to authUid following
                mDatabase.child("following").child(authUid).child(uid).setValue(true);
                // add authUid to uid followers
                mDatabase.child("followers").child(uid).child(authUid).setValue(true);
            }
        });

        btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("following").child(authUid).child(uid).removeValue();
                mDatabase.child("followers").child(uid).child(authUid).removeValue();
            }
        });

        mDatabase.child("posts").child(uid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        collectPosts((Map<String, Object>) dataSnapshot.getValue());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // handle error
                    }
                }
        );

        // post click
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<String, Object> item = (Map.Entry) postListView.getItemAtPosition(position);
                String pid = item.getKey();
                // start new PostActivity and pass pid with Intent
                Intent intent = new Intent(UserActivity.this, PostActivity.class);
                intent.putExtra("PID", pid);
                intent.putExtra("UID", uid);
                startActivity(intent);
            }
        });
    }

    private void collectPosts(Map<String,Object> posts) {
        // TODO: posts out of order?
        if(posts != null){
            postList.clear();
            for (Map.Entry<String, Object> item : posts.entrySet()){
                postList.add(item);
            }
        }
    }


}
