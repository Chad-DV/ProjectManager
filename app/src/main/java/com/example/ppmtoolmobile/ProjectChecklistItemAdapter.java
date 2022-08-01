package com.example.ppmtoolmobile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ProjectChecklistItemAdapter extends BaseAdapter {
    Context context;
    List<String> textArray;
    LayoutInflater inflater;

    public ProjectChecklistItemAdapter(Context context, List<String> textarray) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.textArray = textarray;

    }

    @Override
    public int getCount() {
        return textArray.size();
    }

    @Override
    public Object getItem(int position) {
        return textArray.get(position);
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

        String text = textArray.get(position);

        final TextView projectChecklistItemTextView = ((TextView) vg.findViewById(R.id.projectChecklistItemTextView));

        return vg;
    } }