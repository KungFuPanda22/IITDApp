package com.example.ankurshaswat.iitdapp;

/**
 * Created by ankurshaswat on 23/1/18.
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ankurshaswat.iitdapp.DisplayClasses.BlogPost;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class BlogFragment extends Fragment{

    private ArrayList<BlogPost> blogItems = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private BlogAdapter blogAdapter;

    public BlogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addListenerForSingleValueEvent(postListener);

        blogAdapter = new BlogAdapter(blogItems);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        RecyclerView blogPostList = getView().findViewById(R.id.blogList);

        blogPostList.setLayoutManager(mLayoutManager);
        blogPostList.setAdapter(blogAdapter);

        blogPostList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        swipeRefreshLayout = getView().findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        super.onViewCreated(view, savedInstanceState);
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("blogs");
            myRef.addListenerForSingleValueEvent(postListener);
        }
    };

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            blogItems.clear();
            swipeRefreshLayout.setRefreshing(false);
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                BlogPost blogPost= postSnapshot.getValue(BlogPost.class);
                blogItems.add(blogPost);
                Log.d(TAG, "onDataChange: blogpost fetched"+blogPost.getTitle());
            }
            blogAdapter.notifyDataSetChanged();

            // This method is called once with the initial value and again
            // whenever data at this location is updated.
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
            swipeRefreshLayout.setRefreshing(false);

        }
    };

}