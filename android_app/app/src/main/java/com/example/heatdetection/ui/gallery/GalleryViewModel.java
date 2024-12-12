package com.example.heatdetection.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heatdetection.MainActivity;


public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Outdoor temperature:");
    }

    public LiveData<String> getText() {
        return mText;
    }


}