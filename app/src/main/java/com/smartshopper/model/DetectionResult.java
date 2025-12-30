package com.smartshopper.model;

import android.graphics.RectF;

import androidx.annotation.NonNull;

/**
 * Represents a single detection result from the model
 */
public class DetectionResult {
    private final String label;
    private final float confidence;
    private final RectF boundingBox;

    public DetectionResult(String label, float confidence, RectF boundingBox) {
        this.label = label;
        this.confidence = confidence;
        this.boundingBox = boundingBox;
    }

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }

    public RectF getBoundingBox() {
        return boundingBox;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%.2f%%)", label, confidence * 100);
    }
}
