package com.example.ml_kits.face_detection.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic
{
    private Paint rectPaint;
    private Rect rect;

    public RectOverlay(GraphicOverlay overlay, Rect rect)
    {
        super(overlay);

        rectPaint = new Paint();

        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        this.rect = rect;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas)
    {
        // RectF holds four float coordinates for a rectangle.
        RectF rectF = new RectF(rect);

        // left	The X coordinate of the left side of the rectangle and similarly for all.
        rectF.left = translateX(rectF.left+30f);
        rectF.right = translateX(rectF.right-60f);
        rectF.top = translateX(rectF.top-45f);
        rectF.bottom = translateX(rectF.bottom-25f);

        canvas.drawRect(rectF, rectPaint);
    }
}
