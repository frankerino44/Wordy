package com.example.wordy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDatabase extends AppCompatActivity {

    private ListView databaseListView;
    private ArrayList<String> dataArrayList;
    private ArrayAdapter<String> arrayAdapter;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);

        databaseListView = findViewById(R.id.databaseListView);
        dataArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataArrayList);

        databaseListView.setAdapter(arrayAdapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Words");

        reference.addValueEventListener(new ValueEventListener() {
            //@Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String data = snapshot.getValue(String.class);
                    dataArrayList.add(data);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            //@Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewDatabase.this, "Error checking database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void play(View view) {
        switchToMainActivity();
    }

    public void addWord(View view) {
        switchToAddWord();
    }

    private void switchToMainActivity() {
        Intent switchToMainActivity = new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    private void switchToAddWord() {
        Intent switchToAddWord = new Intent(this, CreateWord.class);
        startActivity(switchToAddWord);
    }
}