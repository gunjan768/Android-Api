package com.example.ml_kits.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ml_kits.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class TextRecognitionActivity extends AppCompatActivity
{
    private static final String TAG = "TextRecognitionActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView detectedText;
    private Bitmap imageBitmap;

    private void initWidget()
    {
        captureImageBtn = findViewById(R.id.button_capture_image);
        detectTextBtn = findViewById(R.id.button_detect_text_image);
        imageView = findViewById(R.id.imageView);
        detectedText = findViewById(R.id.text_detect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        initWidget();

        captureImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
                detectedText.setText("");
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                detectTextFromImage();
            }
        });
    }

    private void detectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
        {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText)
            {
                // If the text recognition operation succeeds, a FirebaseVisionText object will be passed to the success listener. A FirebaseVisionText object contains
                // the full text recognized in the image and zero or more TextBlock objects. Each TextBlock represents a rectangular block of text, which contains zero
                // or more Line objects. Each Line object contains zero or more Element objects, which represent words and word-like entities (dates, numbers, and so on).
                // For each TextBlock, Line, and Element object, you can get the text recognized in the region and the bounding coordinates of the region.
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() 
        {
            @Override
            public void onFailure(@NonNull Exception e) 
            {
                Toast.makeText(TextRecognitionActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        String resultText = firebaseVisionText.getText();
        StringBuilder stringBuilder = new StringBuilder();

        List<FirebaseVisionText.TextBlock> textBlocks = firebaseVisionText.getTextBlocks();

        if(textBlocks.size() == 0)
        {
            Toast.makeText(this, "No text has been found in the image, please try some other image with text",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
            {
                String blockText = block.getText();
                Float blockConfidence = block.getConfidence();

                Point[] blockCornerPoints = block.getCornerPoints();
                Rect blockFrame = block.getBoundingBox();

                for(FirebaseVisionText.Line line: block.getLines())
                {
                    String lineText = line.getText();
                    Float lineConfidence = line.getConfidence();

                    Point[] lineCornerPoints = line.getCornerPoints();
                    Rect lineFrame = line.getBoundingBox();

                    for(FirebaseVisionText.Element element: line.getElements())
                    {
                        String elementText = element.getText();
                        Float elementConfidence = element.getConfidence();

                        Point[] elementCornerPoints = element.getCornerPoints();
                        Rect elementFrame = element.getBoundingBox();

                        stringBuilder.append(elementText).append(" ");
                    }

                    stringBuilder.append("\n");
                }
            }

            detectedText.setText(stringBuilder);
        }
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();

            assert extras != null;
            imageBitmap = (Bitmap) extras.get("data");

            imageView.setImageBitmap(imageBitmap);
        }
    }
}