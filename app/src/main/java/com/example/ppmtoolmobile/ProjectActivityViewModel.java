package com.example.ppmtoolmobile;

import android.text.Editable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProjectActivityViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();

    public void setQuery(String query) {
        this.query.setValue(query);
    }

    public LiveData<String> getQuery() {
        return query;
    }


}
