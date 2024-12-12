package com.example.heatdetection;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MyModel {
    private Interpreter tflite;

    public MyModel(Context context) throws IOException {
        // 加载 .tflite 模型
        tflite = new Interpreter(loadModelFile(context, "final_model.tflite"));
    }

    // 加载模型文件
    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelFileName);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }



    public float[] runModel(float scaledAge, float Humidity, float Temperature, double body_temp) {
        // Prepare input as a (1, 3) float array
        float[][] input = new float[1][4];
        //input[0][0] = scaledBMI;  // Scaled BMI
        input[0][0] = scaledAge;  // Scaled Age
        input[0][1] = Humidity;
        input[0][2] = Temperature;
        input[0][3] = (float)body_temp;

        // Define output array to receive model result
        float[][] output = new float[1][1];  // Adjust dimensions as per model's output shape
        // Run inference
        tflite.run(input, output);

        return output[0];  // Returns the output result
    }

    // 关闭模型
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}
