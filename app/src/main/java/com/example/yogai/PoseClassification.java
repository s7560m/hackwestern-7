package com.example.yogai;

import android.media.Image;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class PoseClassification {
    private PoseDetectorOptions options;
    private PoseDetector poseDetector;
    // constructor for pose detection
    PoseClassification() {
        // Accurate pose detector on static images, when depending on the pose-detection-accurate sdk
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        poseDetector = PoseDetection.getClient(options);
    }

    // Return the task of getting the pose from the posedetector
    public Task<Pose> getPose(InputImage inputImage) {
        return poseDetector.process(inputImage);
    }

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
}
