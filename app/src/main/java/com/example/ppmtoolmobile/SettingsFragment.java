package com.example.ppmtoolmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class SettingsFragment extends Fragment implements View.OnClickListener {
    private CardView settingsProfileCardView, settingsMyProjectsCardView, settingsNotificationsCardView, settingsLogoutCardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, null);

        settingsProfileCardView = v.findViewById(R.id.settingsProfileCardView);
        settingsMyProjectsCardView = v.findViewById(R.id.settingsMyProjectsCardView);
        settingsNotificationsCardView = v.findViewById(R.id.settingsNotificationsCardView);
        settingsLogoutCardView = v.findViewById(R.id.settingsLogoutCardView);


        settingsProfileCardView.setOnClickListener(this);
        settingsMyProjectsCardView.setOnClickListener(this);
        settingsNotificationsCardView.setOnClickListener(this);
        settingsLogoutCardView.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        if(view == settingsProfileCardView) {
            getFragmentManager().beginTransaction().replace(R.id.navframeLayout, new ProfileFragment()).commit();
        } else if(view == settingsMyProjectsCardView) {
            getFragmentManager().beginTransaction().replace(R.id.navframeLayout, new ProjectFragment()).commit();
        } else if(view == settingsNotificationsCardView) {
            startActivity(new Intent(SettingsFragment.this.getActivity(), ProjectFragment.class));
        } else if(view == settingsLogoutCardView) {
            // code to logout
        }
    }

//    new AlertDialog.Builder(ProjectFragment.this.getActivity())
//            .setTitle("Delete entry")
//                .setMessage("Are you sure you want to delete this entry?")
//                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//        boolean res = daoHelper.deleteProjectById(projectList.get(position));
//
//        adapter.notifyDataSetChanged();
//        Toast.makeText(ProjectFragment.this.getActivity(), String.valueOf(res), Toast.LENGTH_SHORT).show();
//    })
//            .setNegativeButton(android.R.string.paste_as_plain_text, (dialog, which) -> {
//        Toast.makeText(ProjectFragment.this.getActivity(), "Not deleted", Toast.LENGTH_SHORT).show();
//    })
//            .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
}