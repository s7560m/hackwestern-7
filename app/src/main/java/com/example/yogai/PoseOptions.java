package com.example.yogai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogai.poseClasses.BridgePose;
import com.example.yogai.poseClasses.CobraPose;
import com.example.yogai.poseClasses.DownwardDog;
import com.example.yogai.poseClasses.Triangle;

public class PoseOptions extends AppCompatActivity {
    public void bridgePose(View view) {
        loadPose("bridgePose", BridgePose.class);
    }
    public void cobraPose(View view) {
        loadPose("cobraPose", CobraPose.class);
    }
    public void downwardDog(View view) {
        loadPose("downwardDog", DownwardDog.class);
    }
    public void triangle(View view) {
        loadPose("triangle", Triangle.class);
    }
    public <T> void loadPose(String pose, Class<T> tClass) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra("POSE", pose);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.poseoptions);
    }
}
