package com.example.ml_kits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ml_kits.face_detection.FaceDetectionActivity;
import com.example.ml_kits.text_recognition.TextRecognitionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button faceActivity = findViewById(R.id.face_detection_activity);
        Button textActivity = findViewById(R.id.text_recognition_activity);

        faceActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FaceDetectionActivity.class));
            }
        });

        textActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TextRecognitionActivity.class));
            }
        });
    }
}