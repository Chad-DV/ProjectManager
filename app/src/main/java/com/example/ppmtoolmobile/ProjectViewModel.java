package com.example.ppmtoolmobile;

import android.text.Editable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProjectViewModel extends ViewModel {

    private MutableLiveData<CharSequence> searchText = new MutableLiveData<>();

    public ProjectViewModel() {
        Log.i("ProjectViewModel", "ViewModel created");
    }

    public void setText(CharSequence input)
    {
        searchText.setValue(input);
    }

    public LiveData<CharSequence> getText(){
        return searchText;

    }


}
