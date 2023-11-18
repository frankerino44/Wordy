package com.example.wordy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateWord extends AppCompatActivity {

    private EditText wordET;
    private TextView wordLabel;

    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);

        auth = FirebaseAuth.getInstance();

        wordET = findViewById(R.id.wordET);
        wordLabel = findViewById(R.id.wordLabel);
    }

    public void add(View view) {
        String word = wordET.getText().toString();

        if (word != null && word.length() == 5 && word.matches("[a-zA-Z]+")) {
            final String finalWord = word.toUpperCase();
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("Words");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                //@Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        ArrayList<String> dataList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String value = snapshot.getValue(String.class);
                            dataList.add(value);
                        }

                        if (dataList.contains(finalWord)) {
                            wordLabel.setTextColor(getColor(R.color.purple));
                            Toast.makeText(CreateWord.this, "Word already exists in the database.", Toast.LENGTH_SHORT).show();
                        } else {
                            reference.push().setValue(finalWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        wordLabel.setTextColor(getColor(R.color.black));
                                        wordET.setText("");
                                        Toast.makeText(CreateWord.this, "Word added!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CreateWord.this, "Write failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        reference.push().setValue(finalWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    wordLabel.setTextColor(getColor(R.color.black));
                                    wordET.setText("");
                                    Toast.makeText(CreateWord.this, "Word added!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateWord.this, "Write failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                //@Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CreateWord.this, "Error checking database.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            wordLabel.setTextColor(getColor(R.color.purple));
            Toast.makeText(CreateWord.this, "Enter a valid 5 letter word!", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearDatabase(View view) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Words");

        showAlertDialogClear();
    }

    private void showAlertDialogClear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Clear Database")
                .setMessage("Are you sure you want to clear your database?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reference.removeValue();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void viewDatabase(View view) {
        switchToViewDatabase();
    }

    public void cancel(View view) {
        switchToMainActivity();
    }

    private void switchToMainActivity() {
        Intent switchToMainActivity = new Intent(this, MainActivity.class);
        startActivity(switchToMainActivity);
    }

    private void switchToViewDatabase() {
        Intent switchToViewDatabase = new Intent(this, ViewDatabase.class);
        startActivity(switchToViewDatabase);
    }
}