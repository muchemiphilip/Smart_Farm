package com.muchemi.smartfarm.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.muchemi.smartfarm.models.User;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    private static final String TAG = "MessagesActivity";

    //widgets
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;

    private ArrayList<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRecyclerView = findViewById(R.id.recycler_view);
        mSearchView = findViewById(R.id.action_search);
    }
}