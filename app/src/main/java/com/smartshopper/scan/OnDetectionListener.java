package com.smartshopper.scan;

import java.util.List;

public interface OnDetectionListener {
    void onDetections(List<DetectionResult> results);
}
