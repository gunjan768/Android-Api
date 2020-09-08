package com.example.ml_kits.face_detection.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;

// If you extends View instead of GraphicOverlay.Graphic instead then there will be two methods invalidate() and postInvalidate().
// Each class which is derived from the View class has the invalidate and the postInvalidate method. If invalidate gets called it tells the system that
// the current view has changed and it should be redrawn as soon as possible. As this method can only be called from your UIThread another method is
// needed for when you are not in the UIThread and still want to notify the system that your View has been changed. The postInvalidate method notifies
// the system from a non-UIThread and the View gets redrawn in the next event loop on the UIThread as soon as possible.

public class EarLineOverlay extends GraphicOverlay.Graphic
{
    private static final String TAG = "Helper";
    Paint paint = new Paint();
    FirebaseVisionPoint leftEarPos = null, rightEarPos = null;

    public EarLineOverlay(GraphicOverlay graphicOverlay, FirebaseVisionPoint leftEarPos, FirebaseVisionPoint rightEarPos)
    {
        super(graphicOverlay);

        this.leftEarPos = leftEarPos;
        this.rightEarPos = rightEarPos; drawShapeOverEars();
    }

    public void drawShapeOverEars()
    {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);

        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas)
    {
        // Log.d("Helper", "onDraw: " + leftEarPos + " " + rightEarPos);

        if(leftEarPos != null)
        {
            canvas.drawLine(leftEarPos.getX() + 1, leftEarPos.getY() + 50,
                    leftEarPos.getX() - 1, leftEarPos.getY() - 50, paint
            );
        }

        if(rightEarPos != null)
        {
            canvas.drawLine(rightEarPos.getX() + 1, rightEarPos.getY() + 50,
                    rightEarPos.getX() - 1, rightEarPos.getY() - 50, paint
            );
        }
    }
}