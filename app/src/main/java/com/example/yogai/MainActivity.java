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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.yogai.MLPoses.Triangle;
import com.example.yogai.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    // Get the progress bar
    private ProgressBar progressBar;

    // Get the pose from onCreate()
    private String poseFromIntent;

    // Get floating action button
    private FloatingActionButton cameraButton;

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
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
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
//                    for (PoseLandmark individualPose : allPoseLandmarks) {
//                        System.out.println(individualPose.getLandmarkType()); }
                    Angles angle = new Angles();
                    System.out.println(angle.leftElbowAngle(pose));
                    // Test pose
                    if (angle.leftElbowAngle(pose) > 10 && angle.leftElbowAngle(pose) < 20) {
                        progressBar.incrementProgressBy(10);
                    }
                    Triangle triangle = new Triangle();
                    if (triangle.isValidPose(pose)) {
                        progressBar.incrementProgressBy(20);
                    }
                    // Let's switch case each pose
//                    switch(poseFromIntent) {
//                        case "bridgePose":x`
//                            break;
//                        case "cobraPose":
//                            break;
//                        case "downwardDog":
//                            break;
//                        case "triangle":
//
//                            break;
//                    }

                    // Check if progress bar is 100
                    // If it is, change icon from camera to checkmark
                    if (progressBar.getProgress() == progressBar.getMax()) {
                        // Stop firing image stream
                        // Prompt that user successfully did it!
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Well done!")
                                .setMessage("You've successfully done " + poseFromIntent + " correctly!")
                                .setPositiveButton("Go back.", (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                })
                                .show();
                    }
                    if (progressBar.getProgress() != progressBar.getMax()) {
                        imageProxy.close();
                    }
                }).addOnFailureListener(e -> {
                    System.out.println(e.getMessage() + e.getCause());
                });
            }

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
        poseFromIntent = getIntent().getStringExtra("POSE");
        progressBar = findViewById(R.id.progressBar);
        cameraButton = findViewById(R.id.cameraButton);
        Log.d("POSE: ", poseFromIntent);

        // Set the initial prompt
        new MaterialAlertDialogBuilder(this)
                .setTitle("Start posing!")
                .setMessage("Position your camera so it can show your full body. When you've held that position correctly for a few seconds, you'll receive a checkmark for that pose!")
                .setPositiveButton("Accept", (dialog, which) -> {
                    dialog.dismiss();
                })
        .show();
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
