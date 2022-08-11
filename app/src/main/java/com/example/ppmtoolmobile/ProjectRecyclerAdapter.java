package com.example.ppmtoolmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ppmtoolmobile.model.Project;

import java.util.List;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.MyViewHolder>  {

    private List<Project> projectList;
    private OnProjectClickListener onProjectClickListener;
    private Context context;

    public ProjectRecyclerAdapter(Context context, List<Project> projectList, OnProjectClickListener onProjectClickListener) {
        this.projectList = projectList;
        this.context = context;
        this.onProjectClickListener = onProjectClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_item_card, parent, false);
        return new MyViewHolder(view, onProjectClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Project project = projectList.get(position);
        holder.projectPriorityInfoBtn.setText(project.getPriority() + " priority");





        if(project.getPriority().equals("High") || project.getPriority().equals("None")) {
            holder.projectPriorityInfoBtn.setTextColor(R.color.dark_red);
            holder.projectPriorityInfoBtn.setBackgroundResource(R.drawable.btn_prj_high_priority);
        } else if(project.getPriority().equals("Medium")) {
            holder.projectPriorityInfoBtn.setTextColor(R.color.dark_orange);
            holder.projectPriorityInfoBtn.setBackgroundResource(R.drawable.btn_prj_medium_priority);

        } else if(project.getPriority().equals("Low")) {
            holder.projectPriorityInfoBtn.setTextColor(R.color.white);
            holder.projectPriorityInfoBtn.setBackgroundResource(R.drawable.btn_prj_low_priority);
        }

        holder.projectListTitleTextView.setText(project.getTitle());
        holder.projectListDescriptionTextView.setText(project.getDescription());

//        String dateDue = due.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        boolean isExpired = project.isProjectExpired();

        if(isExpired) {
            holder.projectDueStatusInfoBtn.setText("Expired");
            holder.projectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_expired);
        } else {
            holder.projectDueStatusInfoBtn.setText("Not Expired");
            holder.projectDueStatusInfoBtn.setBackgroundResource(R.drawable.btn_prj_status_not_expired);
        }




    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void refreshList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView projectListTitleTextView,projectListDescriptionTextView;
        Button projectPriorityInfoBtn,projectDueStatusInfoBtn;
        OnProjectClickListener onProjectClickListener;
//        ImageView scheduleThumbnailImageView;

        public MyViewHolder(@NonNull View itemView, OnProjectClickListener onProjectClickListener) {
            super(itemView);

            projectListTitleTextView = itemView.findViewById(R.id.projectListTitleTextView);
            projectListDescriptionTextView = itemView.findViewById(R.id.projectListDescriptionTextView);
            projectPriorityInfoBtn = itemView.findViewById(R.id.projectPriorityInfoBtn);
            projectDueStatusInfoBtn = itemView.findViewById(R.id.projectDueStatusInfoBtn);
//            scheduleThumbnailImageView = itemView.findViewById(R.id.scheduleThumbnailImageView);

            this.onProjectClickListener = onProjectClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onProjectClickListener.onProjectClick(view, getLayoutPosition());
        }


        @Override
        public boolean onLongClick(View view) {
            onProjectClickListener.onProjectLongClick(view, getAdapterPosition());
            return true;
        }
    }


    public interface OnProjectClickListener {
        void onProjectClick(View view, int position);
        void onProjectLongClick(View view, int position);
    }
}
