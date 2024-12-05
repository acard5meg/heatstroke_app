package com.example.heatdetection.ui.slideshow;

import static com.example.heatdetection.BodyTempCalc.core_body_temp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.heatdetection.MyModel;
import com.example.heatdetection.R;
import com.example.heatdetection.SharedViewModel;
import com.example.heatdetection.databinding.FragmentSlideshowBinding;

import java.io.IOException;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    int[] intArray = {73, 100, 120, 136, 152, 162, 169, 177, 180, 180, 185, 190,
            200, 201, 201, 201, 201, 201, 201, 201, 201, 201};
    int currentIndex = 0;
    TextView HeartRate;
    TextView coreBody;
    TextView resultBox1;
    Handler handler = new Handler();
    float result1 = 0;
    float humidity = 0;
    float temperature = 0;
    float coreBodyTemp = 0;
    float selectedAge = 0;
    private Runnable refreshRunnable;
    private SharedViewModel sharedViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HeartRate = view.findViewById(R.id.HeartRate);
        coreBody = view.findViewById(R.id.coreBody);
        resultBox1 = view.findViewById(R.id.resultBox1);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        startUpdatingTextView();
    }

    private void startUpdatingTextView() {

        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {

                HeartRate.setText(String.valueOf(intArray[currentIndex]));
                double coreBodyT = core_body_temp(intArray[currentIndex], 37.1);
                coreBodyTemp = (float)coreBodyT;
                //sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                //sharedViewModel.setCoreBodyTemp((float)coreBodyTemp);

                coreBody.setText("" + String.format("%.2f", coreBodyTemp));
                //resultBox1.setText("Likelihood:" + String.format("%.2f", result1));

                currentIndex = (currentIndex + 1) % intArray.length;


                handler.postDelayed(this, 3000); // 3000ms = 3s
            }
        };


        handler.post(updateRunnable);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 监听温度数据
        sharedViewModel.getTemperature().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float tem) {
                Log.d("HomeFragment", "Received temperature: " + tem);
                temperature = tem;
            }
        });

        // 监听湿度数据
        sharedViewModel.getHumidity().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float hum) {
                Log.d("HomeFragment", "Received humidity: " + hum);
                humidity = hum;
            }
        });

        sharedViewModel.getSelectedAge().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float age) {
                Log.d("HomeFragment", "Received age: " + age);
                selectedAge = age;
            }
        });


        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                //BMI = (float) selectedWeight * 703 / (selectedHeight * selectedHeight);
                setModel();
                handler.postDelayed(this, 2000); // 2s later run again
            }
        };

        // run refreshRunnable
        handler.post(refreshRunnable);
    }

    public void setModel(){

        try {

            MyModel myModel = new MyModel(requireContext());
            //float scaledBMI = BMI;
            float scaledAge = selectedAge;
            //int scaledGender = selectedGender;
            float scaledHumidity = humidity;
            float scaledTemperature = temperature;
            float scaledBodyTemp = coreBodyTemp;

            View view = getView();
            //Toast.makeText(requireContext(), String.valueOf(coreBodyTemp), Toast.LENGTH_SHORT).show();
            float[] result = myModel.runModel(scaledAge, scaledHumidity, scaledTemperature, scaledBodyTemp);

            // transfer data to homeFragment
            //sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            //sharedViewModel.setResult1(result[0]);

            //resultBox = view.findViewById(R.id.resultBox);

            resultBox1.setText("Likelihood:" + String.format("%.2f", (result[0])*100) + "%");
            //res = result[0];
            //resultBox.setText("output:" + Arrays.toString(result));
            if(result[0] > 0.8){

                LayoutInflater inflater = LayoutInflater.from(requireContext());
                View layout = inflater.inflate(R.layout.layout, null);

                TextView text = layout.findViewById(R.id.custom_toast_text);
                text.setText("You are in danger of heat stroke");
                Toast toast = new Toast(requireContext());
                //toast.setText("You are in danger of heat stroke");
                toast.setView(layout);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0); // Center the Toast
                toast.show();
            }
                //Toast.makeText(requireContext(), "You are in danger of heat stroke", Toast.LENGTH_SHORT).setGravity(Gravity.FILL, 0, 0);
                //Toast myToast = Toast.makeText(requireContext(), "You are in danger of heat stroke", Toast.LENGTH_SHORT);
                //myToast.setGravity(Gravity.CENTER, 0, 0);
            //Log.d("Model Output", "Prediction result: " + result[0]);
            //myModel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}