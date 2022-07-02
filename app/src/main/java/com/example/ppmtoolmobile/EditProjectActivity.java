package com.example.ppmtoolmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.example.ppmtoolmobile.model.Project;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class EditProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editProjectTitleEditText, editProjectDescriptionEditText, editProjectDueDateEditText, editProjectDueTimeEditText;
    private Button editProjectBtn;
    private long projectId;
    private ImageView editProjectNavigationBack;
    private DaoHelper daoHelper;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private RadioButton editProjectPriorityRadioBtn;
    private RadioGroup editProjectPriorityRadioGroup;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);



        editProjectTitleEditText = findViewById(R.id.editProjectTitleEditText);
        editProjectDescriptionEditText = findViewById(R.id.editProjectDescriptionEditText);
        editProjectDueDateEditText = findViewById(R.id.editProjectDueDateEditText);
        editProjectDueTimeEditText = findViewById(R.id.editProjectDueTimeEditText);
        editProjectBtn = findViewById(R.id.editProjectBtn);
        editProjectPriorityRadioGroup = findViewById(R.id.editProjectPriorityRadioGroup);
        editProjectNavigationBack = findViewById(R.id.editProjectNavigationBack);

        daoHelper = new DaoHelper(this);


        loadProjectData();

        dateSetListener = (datePicker, year, month, day) -> {
            editProjectDueDateEditText.setText(year + "-" + checkDigit(month + 1)  + "-" + checkDigit(day));
        };


        editProjectDueDateEditText.setOnClickListener(this);
        editProjectDueTimeEditText.setOnClickListener(this);
        editProjectBtn.setOnClickListener(this);
        editProjectNavigationBack.setOnClickListener(this);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == editProjectBtn) {
            updateProject();

        } else if(view == editProjectDueDateEditText) {
            Calendar mcurrentDate=Calendar.getInstance();
            int year = mcurrentDate.get(Calendar.YEAR);
            int month = mcurrentDate.get(Calendar.MONTH);
            int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(EditProjectActivity.this, android.R.style.Theme_Holo_Light_Dialog, dateSetListener, year, month, day);

            dialog.setTitle("Select project due date");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        else if(view == editProjectDueTimeEditText) {
            Calendar mcurrentTime = Calendar.getInstance();
            int currHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int currMinute = mcurrentTime.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog, (timePicker, selectedHour, selectedMinute) -> {
                editProjectDueTimeEditText.setText( "" + checkDigit(selectedHour) + ":" + checkDigit(selectedMinute));
                Toast.makeText(EditProjectActivity.this, "hour=" + selectedHour + " min=" + selectedMinute, Toast.LENGTH_SHORT).show();

            }, currHour,currMinute, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Select a Time");
            timePickerDialog.show();
        } else if(view == editProjectNavigationBack) {
            finish();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjectData() {
        projectId = getIntent().getLongExtra("projectId", 0);
        Project project = daoHelper.getProjectById(projectId);

        // get full date and time as LocalDateTime
        LocalDateTime dueDateAndTime = project.getDateDue();
        // Get date only as String
        String date = dueDateAndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Get time only as String
        String time = dueDateAndTime.format(DateTimeFormatter.ofPattern("HH:mm"));


        System.out.println("Full date: " + dueDateAndTime);
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        System.out.println("Project: " + project);

        editProjectTitleEditText.setText(project.getTitle());
        editProjectDescriptionEditText.setText(project.getDescription());
        editProjectDueDateEditText.setText(date);
        editProjectDueTimeEditText.setText(time);

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateProject() {
        String title = editProjectTitleEditText.getText().toString().trim();
        String description = editProjectDescriptionEditText.getText().toString().trim();
        String dateDue = editProjectDueDateEditText.getText().toString().trim();
        String timeDue = editProjectDueTimeEditText.getText().toString().trim();
        String priority = getProjectPriorityValue();



        if(TextUtils.isEmpty(dateDue)) {
            editProjectDueDateEditText.setError("Due date is required");
            return;
        }

        if(TextUtils.isEmpty(timeDue)) {
            editProjectDueTimeEditText.setError("Due time is required");
            return;
        }

        if(TextUtils.isEmpty(dateDue) && TextUtils.isEmpty(timeDue)) {
            editProjectDueDateEditText.setError("Due date is required");
            editProjectDueTimeEditText.setError("Due time is required");
        } else {
            String dateTime = dateDue + " " + timeDue;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Project theProject = new Project(projectId, title, description, LocalDateTime.parse(dateTime, formatter), priority);


            System.out.println(theProject);

            boolean result = daoHelper.editProject(theProject);

            if (result) {
                Toast.makeText(EditProjectActivity.this, "Project was edited sucessfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditProjectActivity.this, "Error editing project", Toast.LENGTH_SHORT).show();
            }

        }
    }



    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private String getProjectPriorityValue() {
        int radioId = editProjectPriorityRadioGroup.getCheckedRadioButtonId();
        editProjectPriorityRadioBtn = findViewById(radioId);
        return editProjectPriorityRadioBtn.getText().toString();
    }
}