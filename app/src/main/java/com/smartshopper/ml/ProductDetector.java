package com.smartshopper.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.smartshopper.model.DetectionResult;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ProductDetector {
    private static final String TAG = "ProductDetector";
    private static final String MODEL_PATH = "models/product_detection.tflite";

    // YOLOv8 Configuration
    private static final int INPUT_SIZE = 640;
    private static final float CONFIDENCE_THRESHOLD = 0.50f; // for YOLO

    // 21 total rows = 4 box coordinates (cx, cy, w, h) + 17 classes
    private static final int NUM_ELEMENTS = 21;
    private static final int NUM_BOXES = 8400;

    private Interpreter interpreter;
    private List<String> labels;

    public interface DetectionCallback {
        void onDetectionResult(List<DetectionResult> results);
    }

    public ProductDetector(Context context) throws IOException {
        MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH);
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        interpreter = new Interpreter(modelBuffer, options);

        try {
            labels = FileUtil.loadLabels(context, "models/labels.txt");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load labels", e);
            labels = new ArrayList<>();
        }
    }

    public void detect(Bitmap bitmap, DetectionCallback callback) {
        if (interpreter == null) return;

        try {
            // 1. Preprocess: Resize to 1280x1280
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0.0f, 255.0f))
                    .build();

            TensorImage tensorImage = new TensorImage();
            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);

            // 2. Prepare Output Buffer: [1][21][8400]
            float[][][] output = new float[1][NUM_ELEMENTS][NUM_BOXES];

            // 3. Run Inference
            interpreter.run(tensorImage.getBuffer(), output);

            // 4. Parse YOLO Results
            List<DetectionResult> results = parseYoloResults(
                    output[0],
                    bitmap.getWidth(),
                    bitmap.getHeight()
            );

            callback.onDetectionResult(results);

        } catch (Exception e) {
            Log.e(TAG, "Error during detection", e);
            callback.onDetectionResult(new ArrayList<>());
        }
    }

    private List<DetectionResult> parseYoloResults(float[][] output, int imgWidth, int imgHeight) {
        List<DetectionResult> results = new ArrayList<>();

        // YOLOv8 output is [21][8400]
        // Rows 0,1,2,3 are cx, cy, w, h
        // Rows 4-20 are class scores
        for (int i = 0; i < NUM_BOXES; i++) {
            // Find max class score
            float maxScore = 0f;
            int classIndex = -1;

            for (int c = 4; c < NUM_ELEMENTS; c++) {
                if (output[c][i] > maxScore) {
                    maxScore = output[c][i];
                    classIndex = c - 4;
                }
            }

            if (maxScore >= CONFIDENCE_THRESHOLD) {
                // YOLO coordinates are relative to the input size (1280)
                float cx = output[0][i];
                float cy = output[1][i];
                float w = output[2][i];
                float h = output[3][i];

                // Convert Center to Corners and scale to actual image pixels
                float xFactor = (float) imgWidth / INPUT_SIZE;
                float yFactor = (float) imgHeight / INPUT_SIZE;

                float left = (cx - w / 2f) * xFactor;
                float top = (cy - h / 2f) * yFactor;
                float right = (cx + w / 2f) * xFactor;
                float bottom = (cy + h / 2f) * yFactor;

                RectF rect = new RectF(left, top, right, bottom);
                String label = (classIndex < labels.size()) ? labels.get(classIndex) : "Product";

                results.add(new DetectionResult(label, maxScore, rect));
            }
        }

        // can add non-max suppression for avoiding overlapping boxes but i my brain melted already
        // include in report under future work :)
        return results;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
