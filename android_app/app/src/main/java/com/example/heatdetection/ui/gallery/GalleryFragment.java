package com.example.heatdetection.ui.gallery;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heatdetection.R;
import com.example.heatdetection.SharedViewModel;
import com.example.heatdetection.databinding.FragmentGalleryBinding;
import com.example.heatdetection.ui.home.HomeFragment;

import android.content.ContentResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private TextView tempView;
    private TextView humidityView;
    String temperature = "";
    String humidity = "";
    float tem = 0;
    float hum = 0;
    private SharedViewModel sharedViewModel;
    private ActivityResultLauncher<Intent> openDocumentLauncher;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        tempView = view.findViewById(R.id.temperatureView);
        humidityView = view.findViewById(R.id.humidityView);

        openDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                readFileFromUri(uri);
                            }
                        }
                    }
                });

        openWeatherDataFile();

    }

    private void openWeatherDataFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        openDocumentLauncher.launch(intent);
    }

    private void readFileFromUri(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;


            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Temperature:")) {
                    temperature = line.replace("Temperature:", "").trim();
                } else if (line.startsWith("Humidity:")) {
                    humidity = line.replace("Humidity:", "").trim();
                }
            }
            tempView.setText(temperature);
            humidityView.setText(humidity);

            if (humidity.isEmpty() || temperature.isEmpty()) {
                hum = 0;
                tem = 0;
            }else{

                hum = Float.parseFloat(humidity.replace("%", "")) / 100;
                tem = Float.parseFloat(temperature);

                // transfer data to homeFragment
                sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                Log.d("GalleryFragment", "Temperature set to" + tem);
                sharedViewModel.setTemperature(tem);
                Log.d("GalleryFragment", "Humidity set to" + hum);
                sharedViewModel.setHumidity(hum);
            }


        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Failed to read weather_data.txt", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}