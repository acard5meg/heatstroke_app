package com.example.heatdetection;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heatdetection.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SharedViewModel sharedViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);


        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        //setContentView(R.layout.fragment_slideshow);



        // 初始化视图
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    public SharedViewModel getSharedViewModel() {
        return sharedViewModel;
    }




    // 显示温度和湿度数据
    /*private void showTemperatureAndHumidity(String temperature, String humidity) {
        Toast.makeText(this, "Temperature: " + temperature + ", Humidity: " + humidity, Toast.LENGTH_LONG).show();

    }*/

    /*private void checkStoragePermissionAndReadFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // 权限已授予，读取文件
            readWeatherData();
        } else {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，读取文件
                readWeatherData();
            } else {
                // 权限被拒绝
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void readWeatherData() {
        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String fileName = "weather_data.txt";
        ContentResolver resolver = getContentResolver();

        // 使用MediaStore API查询文件
        String[] projection = new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME};
        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // 文件存在于 MediaStore
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                Uri fileUri = Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, String.valueOf(id));
                readFileFromUri(fileUri);
                return;
            }
        }

        // 如果 MediaStore 没找到文件，尝试使用绝对路径
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/weather_data.txt");
        if (file.exists()) {
            Toast.makeText(this, "File found in Downloads", Toast.LENGTH_SHORT).show();
            readFileFromFile(file);
        } else {
            Toast.makeText(this, "File not found in Downloads", Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileFromUri(Uri fileUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String temperature = "";
            String humidity = "";
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Temperature:")) {
                    temperature = line.replace("Temperature:", "").trim();
                } else if (line.startsWith("Humidity:")) {
                    humidity = line.replace("Humidity:", "").trim();
                }
            }

            showTemperatureAndHumidity(temperature, humidity);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read weather_data.txt", Toast.LENGTH_SHORT).show();
        }
    }
    private void readFileFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String temperature = "";
            String humidity = "";
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Temperature:")) {
                    temperature = line.replace("Temperature:", "").trim();
                } else if (line.startsWith("Humidity:")) {
                    humidity = line.replace("Humidity:", "").trim();
                }
            }

            showTemperatureAndHumidity(temperature, humidity);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read weather_data.txt", Toast.LENGTH_SHORT).show();
        }
    }
    private void showTemperatureAndHumidity(String temperature, String humidity) {
        TextView tempView = findViewById(R.id.temperatureView);
        TextView humidityView = findViewById(R.id.humidityView);
        tempView.setText("Temperature: " + temperature);
        humidityView.setText("Humidity: " + humidity);
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}