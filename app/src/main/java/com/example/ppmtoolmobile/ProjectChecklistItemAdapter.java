package com.example.ppmtoolmobile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProjectChecklistItemAdapter extends BaseAdapter {

    private Context context;
    private List<String> checklist;
    private LayoutInflater inflater;

    public ProjectChecklistItemAdapter(Context context, List<String> checklist) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.checklist = checklist;
    }

    @Override
    public int getCount() {
        return checklist.size();
    }

    @Override
    public Object getItem(int position) {
        return checklist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup vg;

        if (convertView != null) {
            vg = (ViewGroup) convertView;
        } else {
            vg = (ViewGroup) inflater.inflate(R.layout.project_checklist_list_item, null);
        }

        String item = checklist.get(position);

        final TextView projectChecklistItemTextView = ((TextView) vg.findViewById(R.id.projectChecklistItemTextView));
        projectChecklistItemTextView.setText(item);

        return vg;
    }
}