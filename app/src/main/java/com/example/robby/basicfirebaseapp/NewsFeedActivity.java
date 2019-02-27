package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;

    //private ArrayList<Post> postList;
    private ArrayList<Map.Entry> postList;
    private ArrayList<String> followingUids;

//    private RecyclerView recyclerView;
    private ListView listView;
//    private RecyclerView.LayoutManager mLayoutManager;
//    private PostAdapter mAdapter;
    private FeedAdapter adapter;

    private ValueEventListener postListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        listView = findViewById(R.id.feedListView);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        followingUids = new ArrayList<>();

        adapter = new FeedAdapter(NewsFeedActivity.this, R.layout.newsfeed_view, postList);
        listView.setAdapter(adapter);

//        recyclerView = findViewById(R.id.newsFeedRecyclerView);
//        mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new PostAdapter(postList);
//        recyclerView.setAdapter(mAdapter);

//        // post click
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Map.Entry<String, Object> item = (Map.Entry) listView.getItemAtPosition(position);
//                String pid = item.getKey();
//                // start new PostActivity and pass pid with Intent
//                Intent intent = new Intent(NewsFeedActivity.this, PostActivity.class);
//                intent.putExtra("PID", pid);
//                intent.putExtra("UID", item.getKey());
//                startActivity(intent);
//            }
//        });


        mDatabase.child("following/" + authUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NewsFeedActivity", "following onDataChange");
                if(dataSnapshot.getValue() != null){
                    if(postListener != null){
                        mDatabase.child("posts").removeEventListener(postListener);
                    }

                    collectFollowing((Map<String, Object>) dataSnapshot.getValue());
                    Log.d("NewsFeedActivity", "following updated");

                    mDatabase.child("posts").addValueEventListener(postListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NewsFeedActivity", "post onDataChange");
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        // if childSnapshot is a follower
                        if(followingUids.contains(childSnapshot.getKey())){
                            // get all of childSnapshots posts
//                            for(DataSnapshot childChildSnapshot : childSnapshot.getChildren()){
//                                Post post = childChildSnapshot.getValue(Post.class);
//                                childSnapshot.getKey();
//                                postList.add(post);
//                            }
                            //get all of childSnapshots posts
                            collectPosts((Map<String,Object>) childSnapshot.getValue());

                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private void collectPosts(Map<String,Object> posts){
        if(posts != null) {
            postList.clear();
            for (Map.Entry<String, Object> item : posts.entrySet()) {
                postList.add(item);
            }
        }
    }

    private void collectFollowing(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            followingUids.clear();
            //Get user map
            String uid = item.getKey();
            //followerList.add(uid);
            followingUids.add(uid);
        }
    }

}