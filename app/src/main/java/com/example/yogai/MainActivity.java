package com.example.yogai;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraView;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;

import com.example.yogai.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Define cameraprovider and previewview fields
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // Get the image capture
    private ImageCapture imageCapture;

    // Get the main thread
    ExecutorService thread = Executors.newFixedThreadPool(10);

    // Method to bind camera to preview view
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView) {
        Size resolution = new Size(360, 480);
        // Generate preview with target resolution
        Preview preview = new Preview.Builder()
                .setTargetResolution(resolution)
                .build();

        // Build the camera and select the back lens
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Generate the image analysis
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(resolution)
                // This sets how it will handle the data pipeline.
                // Only deliver the latest image to the analyzer, dropping images as they arrive.
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        // AsyncTask.THREAD_POOL_EXECUTOR was previous executor
        // let's try
        imageAnalysis.setAnalyzer(getMainExecutor(), imageProxy -> {
            // Do stuff with the image proxy here
            @SuppressLint("UnsafeExperimentalUsageError")
            Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                PoseClassification poseClassification = new PoseClassification();


                poseClassification.getPose(image).addOnSuccessListener(pose -> {
                    List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();
                    for (PoseLandmark individualPose : allPoseLandmarks) {
                        System.out.println(individualPose.getLandmarkType());
                    }
//                    System.out.println("test");
                    imageProxy.close();
                }).addOnFailureListener(e -> {
                    System.out.println("FUCK" + e.getMessage() + e.getCause());
                });
            }
//            imageProxy.close();

        });


        // Set up image capture methods
        this.imageCapture =
                new ImageCapture.Builder()
                        .build();

        // Create the lifecycle, bind camera preview to previewView
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, imageAnalysis, preview);
        preview.setSurfaceProvider(previewView.createSurfaceProvider());

    }

    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define the processCameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        // When the camera provider resolves, it will bind the hardware camera to the preview
        cameraProviderFuture.addListener(() -> {
            try {
                // If the listener resolves, define the cameraProvider and bind the cameraProvider to the previewView
                PreviewView previewView = (PreviewView) findViewById(R.id.previewView);
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, previewView);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

}
