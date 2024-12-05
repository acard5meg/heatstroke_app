package com.example.heatdetection.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.heatdetection.databinding.FragmentHomeBinding;
import com.example.heatdetection.ui.gallery.GalleryFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int selectedWeight;
    private int selectedHeight;
    private int selectedAge;
    private int selectedGender;
    private float BMI;
    //private float res;
    TextView resultBox;

    private Runnable refreshRunnable;
    private Handler handler = new Handler();
    private SharedViewModel sharedViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner ageSpinner = view.findViewById(R.id.ageSpinner);
        Spinner GenderSpinner = view.findViewById(R.id.GenderSpinner);
        Spinner WeightSpinner = view.findViewById(R.id.WeightSpinner);
        Spinner HeightSpinner = view.findViewById(R.id.HeightSpinner);

        //Current Timer
        /*long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date(currentTimeMillis));
        Toast.makeText(getContext(), "time"+ formattedDate, Toast.LENGTH_SHORT).show();*/



        Integer[] ages = new Integer[98];
        for (int i = 0; i < 98; i++) {
            ages[i] = 1 + i;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);

        String[] gender = new String[2];
        gender[0] = "Male";
        gender[1] = "Female";
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, gender);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GenderSpinner.setAdapter(adapter1);

        Integer[] Weight = new Integer[351];
        for (int i = 0; i < 350; i++) {
            Weight[i] = 50 + i;
        }
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Weight);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        WeightSpinner.setAdapter(adapter2);

        Integer[] Height = new Integer[62];
        for (int i = 0; i < 61; i++) {
            Height[i] = 36 + i;
        }
        ArrayAdapter<Integer> adapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Height);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        HeightSpinner.setAdapter(adapter3);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAge = (int) parent.getItemAtPosition(position);
                //Toast.makeText(getContext(), "Selected Age: " + selectedAge, Toast.LENGTH_SHORT).show();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setSelectedAge((float)selectedAge);
        GenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Male")){
                    selectedGender = 1;
                }else{
                    selectedGender = 0;
                }
                //Toast.makeText(getContext(), "Selected Gender: " + selectedGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWeight = (int) parent.getItemAtPosition(position);
                //Toast.makeText(getContext(), "Selected Weight: " + selectedWeight, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选中时执行的操作
            }
        });

        HeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  selectedHeight = (int) parent.getItemAtPosition(position);
                  //Toast.makeText(getContext(), "Selected Height: " + selectedHeight, Toast.LENGTH_SHORT).show();
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {
                 // 当没有选中时执行的操作
            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacks(refreshRunnable);
    }
}