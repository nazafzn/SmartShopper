package com.smartshopper.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.smartshopper.model.DetectionResult;

import java.util.List;
import java.util.Locale;

/**
 * Custom view to overlay bounding boxes and labels on camera preview
 */
public class OverlayView extends View {
    private Paint boxPaint;
    private Paint textPaint;
    private Paint textBackgroundPaint;
    private List<DetectionResult> detections;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        // Paint for bounding boxes
        boxPaint = new Paint();
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);
        boxPaint.setAntiAlias(true);

        // Paint for text labels
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        // Paint for text background
        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(Color.GREEN);
        textBackgroundPaint.setStyle(Paint.Style.FILL);
        textBackgroundPaint.setAntiAlias(true);
    }

    /**
     * Update detections and redraw
     */
    public void setDetections(List<DetectionResult> detections) {
        this.detections = detections;
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (detections == null || detections.isEmpty()) {
            return;
        }

        for (DetectionResult detection : detections) {
            RectF box = detection.getBoundingBox();
            
            // Draw bounding box
            canvas.drawRect(box, boxPaint);

            // Prepare label text
            String label = String.format(Locale.US, "%s %.0f%%",
                    detection.getLabel(),
                    detection.getConfidence() * 100);

            // Measure text dimensions
            float textWidth = textPaint.measureText(label);
            float textHeight = textPaint.getTextSize();

            // Draw text background
            RectF textBackground = new RectF(
                    box.left,
                    box.top - textHeight - 10,
                    box.left + textWidth + 10,
                    box.top
            );
            canvas.drawRect(textBackground, textBackgroundPaint);

            // Draw label text
            canvas.drawText(label, box.left + 5, box.top - 5, textPaint);
        }
    }
}
