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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.ProjectAndUserDAOImpl;
import com.example.ppmtoolmobile.model.Project;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addProjectBtn;
    private TextView re333r;
    private EditText addProjectTitleEditText, addProjectDescriptionEditText, addProjectDueDateEditText, addProjectTimeEditText,addProjectChecklistEditText;
    private RadioGroup addProjectPriorityRadioGroup;
    private ImageView addProjectNavigationBack;
    private CheckBox addProjectRemindMe2WeeksCheckbox, addProjectRemindMe1WeekCheckbox, addProjectRemindMe1DayCheckbox, addProjectRemindMe1HourCheckbox, addProjectRemindMe30MinutesCheckbox;
    private ImageButton generateAddProjectChecklistEditText;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private ProjectAndUserDAOImpl databaseHelper;
    private RadioButton projectPriorityRadioBtn, projectPriorityHighRadioBtn, projectPriorityMediumRadioBtn, projectPriorityLowRadioBtn, projectPriorityNoneRadioBtn;
    private static String strSeparator = "__,__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        databaseHelper = new ProjectAndUserDAOImpl(this);

        addProjectRemindMe2WeeksCheckbox = findViewById(R.id.addProjectRemindMe2WeeksCheckbox);
        addProjectRemindMe1WeekCheckbox = findViewById(R.id.addProjectRemindMe1WeekCheckbox);
        addProjectRemindMe1DayCheckbox = findViewById(R.id.addProjectRemindMe1DayCheckbox);
        addProjectRemindMe1HourCheckbox = findViewById(R.id.addProjectRemindMe1HourCheckbox);
        addProjectRemindMe30MinutesCheckbox = findViewById(R.id.addProjectRemindMe30MinutesCheckbox);

        addProjectBtn = (Button) findViewById(R.id.addProjectBtn);
        addProjectTitleEditText = (EditText) findViewById(R.id.addProjectTitleEditText);
        addProjectDescriptionEditText = (EditText) findViewById(R.id.addProjectDescriptionEditText);
        addProjectDueDateEditText = (EditText) findViewById(R.id.addProjectDueDateEditText);
        addProjectTimeEditText = (EditText) findViewById(R.id.addProjectTimeEditText);
        addProjectPriorityRadioGroup = (RadioGroup) findViewById(R.id.addProjectPriorityRadioGroup);
        addProjectNavigationBack = findViewById(R.id.addProjectNavigationBack);
        addProjectChecklistEditText = findViewById(R.id.addProjectChecklistEditText);
        generateAddProjectChecklistEditText = findViewById(R.id.generateAddProjectChecklistEditText);



        dateSetListener = (datePicker, year, month, day) -> {
            addProjectDueDateEditText.setText(year + "-" + checkDigit(month + 1)  + "-" + checkDigit(day));
        };



        addProjectBtn.setOnClickListener(this);
        addProjectDueDateEditText.setOnClickListener(this);
        addProjectTimeEditText.setOnClickListener(this);
        generateAddProjectChecklistEditText.setOnClickListener(this);
        addProjectNavigationBack.setOnClickListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == addProjectBtn) {
            addProject();
        } else if(view == addProjectDueDateEditText) {
            Calendar mcurrentDate=Calendar.getInstance();
            int year = mcurrentDate.get(Calendar.YEAR);
            int month = mcurrentDate.get(Calendar.MONTH);
            int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(AddProjectActivity.this, android.R.style.Theme_Holo_Light_Dialog, dateSetListener, year, month, day);

            dialog.setTitle("Select project due date");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
         else if(view == addProjectTimeEditText) {
            Calendar mcurrentTime = Calendar.getInstance();
            int currHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int currMinute = mcurrentTime.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog, (timePicker, selectedHour, selectedMinute) -> {
                addProjectTimeEditText.setText( "" + checkDigit(selectedHour) + ":" + checkDigit(selectedMinute));
                Toast.makeText(AddProjectActivity.this, "hour=" + selectedHour + " min=" + selectedMinute, Toast.LENGTH_SHORT).show();

            }, currHour,currMinute, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Select a Time");
            timePickerDialog.show();
        } else if(view == generateAddProjectChecklistEditText) {
            LinearLayout ll = (LinearLayout)findViewById(R.id.addChecklistLinearLayout);

            EditText et = new EditText(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,20);

            et.setLayoutParams(params);
            et.setBackground(getResources().getDrawable(R.drawable.input_default));
            et.setHint("Add a item...");
            et.setTypeface(getResources().getFont(R.font.lato));
            et.setTextSize(15);
            ll.addView(et);

            Toast.makeText(AddProjectActivity.this, et.getText(), Toast.LENGTH_SHORT).show();

        } else if(view == addProjectNavigationBack) {
             finish();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addProject() {
        String title = addProjectTitleEditText.getText().toString().trim();
        String description = addProjectDescriptionEditText.getText().toString().trim();
        String dateDue = addProjectDueDateEditText.getText().toString().trim();
        String timeDue = addProjectTimeEditText.getText().toString().trim();
        String priority = getProjectPriorityValue();
        String authenticatedUser = getIntent().getStringExtra("authenticatedUser");






//
//        if(TextUtils.isEmpty(dateDue)) {
//            addProjectDueDateEditText.setError("Due date is required");
//            return;
//        }
//
//        if(TextUtils.isEmpty(timeDue)) {
//            addProjectTimeEditText.setError("Due time is required");
//            return;
//        }
//
//        if(TextUtils.isEmpty(dateDue) && TextUtils.isEmpty(timeDue)) {
//            addProjectDueDateEditText.setError("Due date is required");
//            addProjectTimeEditText.setError("Due time is required");
//        } else {
//            String dateTime = dateDue + " " + timeDue;
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

//            Project theProject = new Project(title, description, LocalDateTime.parse(dateTime, formatter), priority);
//
//            boolean result = databaseHelper.addProject(theProject,authenticatedUser);



            getProjectRemindMeValues();

//
//            if(result) {
//                Toast.makeText(AddProjectActivity.this, "Project was added sucessfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(AddProjectActivity.this, "Error adding project", Toast.LENGTH_SHORT).show();
//            }


            clearInput();

        }




//



    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void clearInput() {
        addProjectTitleEditText.setText("");
        addProjectDescriptionEditText.setText("");
        addProjectDueDateEditText.setText("");
        addProjectTimeEditText.setText("");
        addProjectPriorityRadioGroup.setSelected(false);
    }

    public void getProjectRemindMeValues() {
        String[] msg = new String[5];
        if(addProjectRemindMe2WeeksCheckbox.isChecked())
            msg[0] = "2 weeks";
        if(addProjectRemindMe1WeekCheckbox.isChecked())
            msg[1] = "1 week";
        if(addProjectRemindMe1DayCheckbox.isChecked())
            msg[2] = "1 day";
        if(addProjectRemindMe1HourCheckbox.isChecked())
            msg[3] = "1 hour";
        if(addProjectRemindMe30MinutesCheckbox.isChecked())
            msg[4] = "30 minutes";


        String strArr = convertArrayToString(msg);

        System.out.println("STRING VALUE: " + strArr);
        System.out.println("ARRAY VALUE: " + Arrays.toString(convertStringToArray(strArr)));




    }


    private String getProjectPriorityValue() {
        int radioId = addProjectPriorityRadioGroup.getCheckedRadioButtonId();
        projectPriorityRadioBtn = findViewById(radioId);
        return projectPriorityRadioBtn.getText().toString();
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
        return str.split(strSeparator);

    }
}