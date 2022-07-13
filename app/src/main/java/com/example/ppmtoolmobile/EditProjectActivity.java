package com.example.ppmtoolmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class EditProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editProjectTitleEditText, editProjectDescriptionEditText, editProjectDueDateEditText, editProjectDueTimeEditText;
    private Button editProjectBtn;
    private CheckBox editProjectRemindMe2WeeksCheckbox, editProjectRemindMe1WeekCheckbox, editProjectRemindMe1DayCheckbox, editProjectRemindMe1HourCheckbox, editProjectRemindMe30MinutesCheckbox;
    private long projectId;
    private ImageView editProjectNavigationBack;
    private ProjectAndUserDAOImpl databaseHelper;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private RadioButton editProjectPriorityRadioBtn, projectPriorityHighRadioBtn, projectPriorityMediumRadioBtn, projectPriorityLowRadioBtn;
    private RadioGroup editProjectPriorityRadioGroup;
    private static String strSeparator = ", ";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);


        editProjectRemindMe2WeeksCheckbox = findViewById(R.id.editProjectRemindMe2WeeksCheckbox);
        editProjectRemindMe1WeekCheckbox = findViewById(R.id.editProjectRemindMe1WeekCheckbox);
        editProjectRemindMe1DayCheckbox = findViewById(R.id.editProjectRemindMe1DayCheckbox);
        editProjectRemindMe1HourCheckbox = findViewById(R.id.editProjectRemindMe1HourCheckbox);
        editProjectRemindMe30MinutesCheckbox = findViewById(R.id.editProjectRemindMe30MinutesCheckbox);

        editProjectTitleEditText = findViewById(R.id.editProjectTitleEditText);
        editProjectDescriptionEditText = findViewById(R.id.editProjectDescriptionEditText);
        editProjectDueDateEditText = findViewById(R.id.editProjectDueDateEditText);
        editProjectDueTimeEditText = findViewById(R.id.editProjectDueTimeEditText);
        editProjectBtn = findViewById(R.id.editProjectBtn);
        editProjectPriorityRadioGroup = findViewById(R.id.editProjectPriorityRadioGroup);

        projectPriorityHighRadioBtn = findViewById(R.id.projectPriorityHighRadioBtn);
        projectPriorityMediumRadioBtn = findViewById(R.id.projectPriorityMediumRadioBtn);
        projectPriorityLowRadioBtn = findViewById(R.id.projectPriorityLowRadioBtn);
        editProjectNavigationBack = findViewById(R.id.editProjectNavigationBack);

        databaseHelper = new ProjectAndUserDAOImpl(this);


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
        Project project = databaseHelper.getProjectById(projectId);

        // get full date and time as LocalDateTime
        LocalDateTime dueDateAndTime = project.getDateDue();
        // Get date only as String
        String date = dueDateAndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Get time only as String
        String time = dueDateAndTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        String[] remindMeValues = convertStringToArray(project.getRemindMeInterval());
        String priority = project.getPriority();

        int priority_high_id = 2_131_296_703;
        int priority_medium_id = 2_131_296_706;
        int priority_low_id = 2_131_296_705;


        if(priority.equalsIgnoreCase("High")) editProjectPriorityRadioGroup.check(priority_high_id);
        if(priority.equalsIgnoreCase("Medium")) editProjectPriorityRadioGroup.check(priority_medium_id);
        if(priority.equalsIgnoreCase("Low")) editProjectPriorityRadioGroup.check(priority_low_id);

        System.out.println("Project: " + project);

        editProjectTitleEditText.setText(project.getTitle());
        editProjectDescriptionEditText.setText(project.getDescription());
        editProjectDueDateEditText.setText(date);
        editProjectDueTimeEditText.setText(time);


        System.out.println("priority: " + priority);
        if(remindMeValues.length > 0) {
            if(!remindMeValues[0].equals("null")) editProjectRemindMe2WeeksCheckbox.setChecked(true);
            if(!remindMeValues[1].equals("null")) editProjectRemindMe1WeekCheckbox.setChecked(true);
            if(!remindMeValues[2].equals("null")) editProjectRemindMe1DayCheckbox.setChecked(true);
            if(!remindMeValues[3].equals("null")) editProjectRemindMe1HourCheckbox.setChecked(true);
            if(!remindMeValues[4].equals("null")) editProjectRemindMe30MinutesCheckbox.setChecked(true);
        }



    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateProject() {
        String title = editProjectTitleEditText.getText().toString().trim();
        String description = editProjectDescriptionEditText.getText().toString().trim();
        String dateDue = editProjectDueDateEditText.getText().toString().trim();
        String timeDue = editProjectDueTimeEditText.getText().toString().trim();
        String priority = getProjectPriorityValue();
        String remindMeInterval = getProjectRemindMeValues();
        String checkList;



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

            Project theProject = new Project(projectId, title, description, LocalDateTime.parse(dateTime, formatter), priority, remindMeInterval);


            System.out.println("UPDATED PROJECT: " + theProject);

            boolean result = databaseHelper.editProject(theProject);

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

        System.out.println("ratiod id: " + radioId + ", " + editProjectPriorityRadioBtn.getText());
        return editProjectPriorityRadioBtn.getText().toString();
    }

    public String getProjectRemindMeValues() {
        String[] msg = new String[5];
        if(editProjectRemindMe2WeeksCheckbox.isChecked())
            msg[0] = "2 weeks";
        if(editProjectRemindMe1WeekCheckbox.isChecked())
            msg[1] = "1 week";
        if(editProjectRemindMe1DayCheckbox.isChecked())
            msg[2] = "1 day";
        if(editProjectRemindMe1HourCheckbox.isChecked())
            msg[3] = "1 hour";
        if(editProjectRemindMe30MinutesCheckbox.isChecked())
            msg[4] = "30 minutes";

        return convertArrayToString(msg);
    }

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){

        if(str == null) {
            return new String[]{};
        }
        return str.split(strSeparator);

    }
}