package com.smartshopper.model;

import android.graphics.RectF;

/**
 * Represents a single detection result from the model
 */
public class DetectionResult {
    private String label;
    private float confidence;
    private RectF boundingBox;

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

    @Override
    public String toString() {
        return String.format("%s (%.2f%%)", label, confidence * 100);
    }
}
