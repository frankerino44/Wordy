package com.example.wordy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int row;
    String word;
    boolean done;
    boolean won;
    boolean hard;

    EditText[][] ETArray;
    TextView modeLabel;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<Character> prevChars;
    ArrayList<Character> currentChars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeLabel = findViewById(R.id.modeLabel);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Words");

        row = 1;
        done = false;
        won = false;
        hard = false;
        prevChars = new ArrayList<Character>();
        currentChars = new ArrayList<Character>();

        ETArray = initET();
        for (int i = 2; i < 7; i++) {
            disableRow(i);
        }

        setRandomWord();
    }

    public void restart(View view) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                ETArray[i][j].setText("");
                ETArray[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            }
        }

        disableRow(row);

        row = 1;
        done = false;
        won = false;
        hard = false;

        enableRow(row);

        setRandomWord();
    }

    public void clear(View view) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                ETArray[i][j].setText("");
                ETArray[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            }
        }

        disableRow(row);

        row = 1;
        done = false;
        won = false;

        enableRow(row);
    }

    public void submit(View view) {
        if (done) {
            Toast.makeText(MainActivity.this, "Try again or pick a new word.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentChars.clear();

        for (int i = 0; i < 5; i++) {
            if (ETArray[row-1][i].getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "Fill all boxes in row " + row +"!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (hard) {
                    currentChars.add(ETArray[row-1][i].getText().toString().charAt(0));
                }
            }
        }

        if (hard && row > 1) {
            for (int i = 0; i < 5; i++) {
                Character tempChar = currentChars.get(i);
                if (prevChars.contains(tempChar)) {
                    prevChars.remove(tempChar);
                }
            }

            if (!prevChars.isEmpty()) {
                Toast.makeText(MainActivity.this, "Include all correct letters from last guess!", Toast.LENGTH_SHORT).show();
                return;
            }

            prevChars.clear();
        }

        String[] color = new String[5];
        for (int i = 0; i < 5; i++) {
            color[i] = "GRAY";
        }

        won = true;

        for (int i = 0; i < 5; i++) {
            if (ETArray[row-1][i].getText().toString().charAt(0) == word.charAt(i)) {
                color[i] = "GREEN";

                if (hard){
                    prevChars.add(ETArray[row-1][i].getText().toString().charAt(0));
                }
            } else {
                won = false;
                for (int j = 0; j < 5; j++) {
                    if (word.charAt(i) == ETArray[row-1][j].getText().toString().charAt(0) && color[j] == "GRAY") {
                        color[j] = "YELLOW";

                        if (hard) {
                            prevChars.add(ETArray[row-1][j].getText().toString().charAt(0));
                        }

                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            ETArray[row-1][i].setBackgroundColor(Color.parseColor(color[i]));
        }

        disableRow(row);

        if (won) {
            Toast.makeText(MainActivity.this, "Congratulations, you got it!", Toast.LENGTH_SHORT).show();
            done = true;
            return;
        }

        if (row == 6) {
            Toast.makeText(MainActivity.this, "Tough luck! Try again or pick a new word.", Toast.LENGTH_SHORT).show();
            done = true;
            return;
        }

        disableRow(row);
        enableRow(row+1);

        row++;
    }

    private EditText[][] initET() {
        EditText[][] ETArray = new EditText[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                String editTextID = "row" + (i+1) + "col" + (j+1) + "ET";
                int RID = getResources().getIdentifier(editTextID, "id", getPackageName());
                ETArray[i][j] = findViewById(RID);
            }
        }
        return ETArray;
    }

    private void setRandomWord() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> dataList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        dataList.add(value);
                    }

                    word = getRandomValue(dataList);

                    showAlertDialogMode();
                } else {
                    showAlertDialogEmpty();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error fetching data", databaseError.toException());
            }
        });
    }

    private String getRandomValue(ArrayList<String> dataList) {
        if (dataList != null && dataList.size() > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(dataList.size());
            return dataList.get(randomIndex);
        }
        return null;
    }

    private void enableRow(int row) {
        for (int i = 0; i < 5; i++) {
            ETArray[row-1][i].setFocusable(true);
            ETArray[row-1][i].setFocusableInTouchMode(true);
        }
    }

    private void disableRow(int row) {
        for (int i = 0; i < 5; i++) {
            ETArray[row-1][i].setFocusable(false);
            ETArray[row-1][i].setFocusableInTouchMode(false);
        }
    }

    private void showAlertDialogEmpty() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Database Empty")
                .setMessage("Please add a word to play Wordy.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                switchToCreateWord();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showAlertDialogMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Wordy")
                .setMessage("Would you like to play on easy or hard mode?");

        builder.setPositiveButton("EASY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hard = false;
                modeLabel.setText("Easy Mode");
                modeLabel.setTextColor(Color.GREEN);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("HARD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                hard = true;
                modeLabel.setText("Hard Mode");
                modeLabel.setTextColor(Color.RED);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void addWord(View view) {
        switchToCreateWord();
    }

    private void switchToCreateWord() {
        Intent switchToCreateWordIntent = new Intent(this, CreateWord.class);
        startActivity(switchToCreateWordIntent);
    }
}