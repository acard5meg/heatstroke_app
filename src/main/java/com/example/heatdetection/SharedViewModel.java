package com.example.heatdetection;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel{
    private final MutableLiveData<Float> temperature = new MutableLiveData<>();
    private final MutableLiveData<Float> humidity = new MutableLiveData<>();
    private final MutableLiveData<Float> result1 = new MutableLiveData<>();
    private final MutableLiveData<Float> coreBodyTemp = new MutableLiveData<>();
    private final MutableLiveData<Float> selectedAge = new MutableLiveData<>();
    public LiveData<Float> getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temp) {
        Log.d("SharedViewModel", "Setting temperature: " + temp);
        temperature.setValue(temp);
    }

    public LiveData<Float> getHumidity() {
        return humidity;
    }

    public void setHumidity(Float hum) {
        Log.d("SharedViewModel", "Setting humidity: " + hum);
        humidity.setValue(hum);
    }
    public LiveData<Float> getResult1() {
        return result1;
    }
    public void setResult1(Float res) {
        result1.setValue(res);
    }
    public LiveData<Float> getCoreBodyTemp() {
        return coreBodyTemp;
    }
    public void setCoreBodyTemp(Float temp) {
        coreBodyTemp.setValue(temp);
    }
    public LiveData<Float> getSelectedAge() {
        return selectedAge;
    }
    public void setSelectedAge(Float age) {
        selectedAge.setValue(age);
    }
}
