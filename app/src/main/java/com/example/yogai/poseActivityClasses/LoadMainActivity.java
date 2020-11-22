package com.example.yogai.poseActivityClasses;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogai.MainActivity;

public class LoadMainActivity extends AppCompatActivity {
    public void clickAnywhere(View view) {
        String pose = getIntent().getStringExtra("POSE");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
