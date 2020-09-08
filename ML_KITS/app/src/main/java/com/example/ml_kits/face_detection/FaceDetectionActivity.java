package com.example.ml_kits.face_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ml_kits.R;
import com.example.ml_kits.face_detection.helper.GraphicOverlay;
import com.example.ml_kits.face_detection.helper.EarLineOverlay;
import com.example.ml_kits.face_detection.helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class FaceDetectionActivity extends AppCompatActivity
{
    private static final String TAG = "FaceDetectionActivity";
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    private AlertDialog alertDialog;
    Button faceDetectButton, cameraFrontBack, autoFlashButton, manualFlashButton;
    private String cameraState = "free", cameraFace = "back", flashState = "off", autoFlashState = "off";

    private void initCameraButton()
    {
        faceDetectButton = findViewById(R.id.button_detect_face);
        cameraFrontBack = findViewById(R.id.camera_front_back);
        autoFlashButton = findViewById(R.id.button_auto_flash);
        manualFlashButton = findViewById(R.id.button_flash);

        faceDetectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                graphicOverlay.clear();

                if(cameraState.equals("capture"))
                {
                    cameraView.start();
                    cameraState = "free";
                    faceDetectButton.setText(R.string.detect_face);
                }
                else
                {
                    cameraView.captureImage();
                    faceDetectButton.setEnabled(false);
                }
            }
        });

        cameraFrontBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(cameraFace.equals("back"))
                {
                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                    cameraFrontBack.setText(R.string.back_camera);
                    cameraFace = "front";
                }
                else
                {
                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                    cameraFrontBack.setText(R.string.front_camera);
                    cameraFace = "back";
                }
            }
        });

        manualFlashButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(flashState.equals("off"))
                {
                    flashState = "on";
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    manualFlashButton.setText("Flash Off");
                    autoFlashButton.setEnabled(false);
                }
                else
                {
                    flashState = "off";
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    manualFlashButton.setText("Flash On");
                    autoFlashButton.setEnabled(true);
                }
            }
        });

        autoFlashButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cameraView.setFlash(CameraKit.Constants.FLASH_OFF);

                if(autoFlashState.equals("off"))
                {
                    cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
                    autoFlashState = "auto";
                    autoFlashButton.setText("Auto Flash Off");

                    manualFlashButton.setEnabled(false);
                }
                else
                {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    autoFlashState = "off";

                    autoFlashButton.setText("Auto Flash On");
                    manualFlashButton.setEnabled(true);
                }
            }
        });
    }

    private void setCameraProperties()
    {
        cameraView = findViewById(R.id.camera_view);
        // cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);

        // cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        // cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);

        // cameraView.setMethod(CameraKit.Constants.METHOD_STANDARD);
        // cameraView.setMethod(CameraKit.Constants.METHOD_STILL);

        // cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_STRICT);
        // cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_LAZY);
        // cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);

        // cameraView.setJpegQuality(100);
        // cameraView.setCropOutput(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        graphicOverlay = findViewById(R.id.graphic_overlay);

        setCameraProperties();
        initCameraButton();

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please wait, processing...")
                .setCancelable(false)
                .build();

        cameraView.addCameraKitListener(new CameraKitEventListenerAdapter()
        {
            @Override
            public void onEvent(CameraKitEvent event) {
                super.onEvent(event);
            }

            @Override
            public void onError(CameraKitError error) {
                super.onError(error);
            }

            @Override
            public void onImage(CameraKitImage image)
            {
                super.onImage(image);

                alertDialog.show();

                Bitmap bitmap = image.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                processFaceDetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo video) {
                super.onVideo(video);
            }
        });
    }

    private void processFaceDetection(Bitmap bitmap)
    {
        // setClassificationMode() : Indicates whether to run additional classifiers for characterizing attributes such as "smiling" and "eyes open".
        // enableTracking() : Enables face tracking, which will maintain a consistent ID for each face when processing consecutive frames. Tracking should be
        // disabled for handling a series of non-consecutive still images.
        // setContourMode() : Sets whether to detect no contours or all contours. Processing time increases as the number of contours to search for increases,
        // so detecting all contours will increase the overall detection time. Note that it would return up to 5 faces contours.
        // setPerformanceMode() : Extended option for controlling additional accuracy / speed trade-offs in performing face detection. In general, choosing the
        // more accurate mode will generally result in longer runtime, whereas choosing the faster mode will generally result in detecting fewer faces.

        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setMinFaceSize(0.1f)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                // .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)     // By default it is NO_CONTOURS.
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)     // By default it is FAST.
                .enableTracking()
                .build();

        // getVisionFaceDetector() can have no argument if you don't want ot set any FirebaseVisionFaceDetectorOptions.
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);

        // Represents an image object that can be used for both on-device and cloud API detectors.
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        Task<List<FirebaseVisionFace>> task = firebaseVisionFaceDetector.detectInImage(firebaseVisionImage);

        task.addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>()
        {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces)
            {
                getFaceResult(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(FaceDetectionActivity.this, "Error while detecting image", Toast.LENGTH_SHORT).show();

                resetWidget();
            }   
        });
    }

    private void resetWidget()
    {
        cameraState = "capture";
        alertDialog.dismiss();
        faceDetectButton.setEnabled(true);
        faceDetectButton.setText(R.string.clear_reset);
    }

    private void getFaceResult(List<FirebaseVisionFace> firebaseVisionFaces)
    {
        if(firebaseVisionFaces.size() == 0)
        {
            Toast.makeText(this, "No face(s) has been found. PLease try image with at least oen face", Toast.LENGTH_LONG).show();
            resetWidget();

            return;
        }

        for(FirebaseVisionFace firebaseVisionFace : firebaseVisionFaces)
        {
            // Rect holds four integer coordinates for a rectangle. The rectangle is represented by the coordinates of its 4 edges (left, top, right bottom).
            // These fields can be accessed directly. Use width() and height() to retrieve the rectangle's width and height. Here Rect is used to draw the
            // rectangle over the faces. getBoundingBox() returns the axis-aligned bounding rectangle of the detected face.
            Rect rect = firebaseVisionFace.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);

            graphicOverlay.add(rectOverlay);

            // float rotY = firebaseVisionFace.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            // float rotZ = firebaseVisionFace.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees


            // ************************************************** If landmark enabled ****************************************************************


            // LEFT_EAR will give the midpoint of the subject's left ear tip and left ear lobe and similarly for right ear.
            FirebaseVisionFaceLandmark leftEar = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
            FirebaseVisionFaceLandmark rightEar = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);

            someMoreLandmark(firebaseVisionFace);

            if(leftEar != null && rightEar != null)
            {
                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                FirebaseVisionPoint rightEarPos = rightEar.getPosition();

                // Log.d(TAG, "getFaceResult : " + leftEarPos + " " + rightEarPos);

                EarLineOverlay earLineOverlay = new EarLineOverlay(graphicOverlay, leftEarPos, rightEarPos);
                graphicOverlay.add(earLineOverlay);
            }


            // **************************************************  landmark finishes ****************************************************************



            // ************************************************** If contour enabled ****************************************************************


            // List<FirebaseVisionPoint> leftEyeContour = firebaseVisionFace.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
            // List<FirebaseVisionPoint> upperLipBottomContour = firebaseVisionFace.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();


            // **************************************************  contour finishes *******************************************************************



            // ************************************************** If classification enabled ***********************************************************


            StringBuilder stringBuilder = new StringBuilder();

            if(firebaseVisionFace.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float smilingProbability = firebaseVisionFace.getSmilingProbability();

                stringBuilder.append("Smiling Probability : ").append(smilingProbability).append("\n");
            }

            if(firebaseVisionFace.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float rightEyeOpenProbability = firebaseVisionFace.getRightEyeOpenProbability();

                stringBuilder.append("Right Eye Open Probability : ").append(rightEyeOpenProbability).append("\n");
            }

            if(firebaseVisionFace.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
            {
                float leftEyeOpenProbability = firebaseVisionFace.getRightEyeOpenProbability();

                stringBuilder.append("Left Eye Open Probability : ").append(leftEyeOpenProbability).append("\n");
            }


            // ************************************************  classification finishes ***********************************************************


            // ************************************************ If tracking enabled ****************************************************************


            if(firebaseVisionFace.getTrackingId() != FirebaseVisionFace.INVALID_ID)
            {
                int id = firebaseVisionFace.getTrackingId();
            }


            // *************************************************** tracking finishes ******************************************************************


            showToastMessage(stringBuilder);
        }

        resetWidget();
    }

    private void someMoreLandmark(FirebaseVisionFace firebaseVisionFace)
    {
        // You can also detect all these using the same process as we did for ears and face.
        FirebaseVisionFaceLandmark leftEye = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
        FirebaseVisionFaceLandmark rightEye = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
        FirebaseVisionFaceLandmark nose = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);
        FirebaseVisionFaceLandmark leftMouth = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT);
        FirebaseVisionFaceLandmark bottomMouth = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);
        FirebaseVisionFaceLandmark rightMouth = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT);
    }

    // There is no way to directly change the duration for which the toast is shown using the show() method without reimplementing the whole Toast
    // class in your application.
    private void showToastMessage(StringBuilder stringBuilder)
    {
        // Set the toast and duration.
        final Toast mToastToShow = Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG);

        mToastToShow.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        cameraView.stop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        cameraView.start();
    }
}