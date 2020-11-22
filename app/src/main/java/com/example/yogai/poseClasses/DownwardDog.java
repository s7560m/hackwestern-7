package com.example.yogai.poseClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogai.MainActivity;
import com.example.yogai.R;

public class DownwardDog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downwarddog);
    }

    public void clickAnywhere(View view) {
        String pose = getIntent().getStringExtra("POSE");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("POSE", pose);
        startActivity(intent);
    }
}
