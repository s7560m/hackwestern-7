package com.example.yogai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChoosePose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pose);
    }

    public void vrikshasana(View view) {
        startCameraActivity("vrikshasana");
    }
    public void warriorpose(View view) {
        startCameraActivity("warrior pose");
    }

    // Start the camera activity
    public void startCameraActivity(String pose) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("POSE: ", pose);
        startActivity(intent);
    }
}
