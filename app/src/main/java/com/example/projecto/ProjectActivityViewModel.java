package com.example.projecto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProjectActivityViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final MutableLiveData<Integer> projectCount = new MutableLiveData<>();

    public LiveData<String> getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query.setValue(query);
    }

    public LiveData<Integer> getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount.setValue(projectCount);
    }

}
