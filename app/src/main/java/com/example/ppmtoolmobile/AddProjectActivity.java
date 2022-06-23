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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmtoolmobile.dao.DaoHelper;
import com.example.ppmtoolmobile.model.Project;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AddProjectActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addProjectBtn;
    private TextView re333r;
    private EditText addProjectTitleEditText, addProjectDescriptionEditText, addProjectDueDateEditText, addProjectTimeEditText;
    private RadioGroup addProjectPriorityRadioGroup;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private DaoHelper daoHelper;
    private RadioButton projectPriorityRadioBtn, projectPriorityHighRadioBtn, projectPriorityMediumRadioBtn, projectPriorityLowRadioBtn, projectPriorityNoneRadioBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        daoHelper = new DaoHelper(this);

        addProjectBtn = (Button) findViewById(R.id.addProjectBtn);
        addProjectTitleEditText = (EditText) findViewById(R.id.addProjectTitleEditText);
        addProjectDescriptionEditText = (EditText) findViewById(R.id.addProjectDescriptionEditText);
        addProjectDueDateEditText = (EditText) findViewById(R.id.addProjectDueDateEditText);
        addProjectTimeEditText = (EditText) findViewById(R.id.addProjectTimeEditText);
        addProjectPriorityRadioGroup = (RadioGroup) findViewById(R.id.addProjectPriorityRadioGroup);



        dateSetListener = (datePicker, year, month, day) -> {
            addProjectDueDateEditText.setText(year + "-" + checkDigit(month + 1)  + "-" + checkDigit(day));
        };

        addProjectBtn.setOnClickListener(this);
        addProjectDueDateEditText.setOnClickListener(this);
        addProjectTimeEditText.setOnClickListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if(view == addProjectBtn) {
            System.out.println("test");
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addProject() {
        String title = addProjectTitleEditText.getText().toString().trim();
        String description = addProjectDescriptionEditText.getText().toString().trim();
        String dateDue = addProjectDueDateEditText.getText().toString().trim();
        String timeDue = addProjectTimeEditText.getText().toString().trim();
        String priority = getProjectPriorityValue();



        if(TextUtils.isEmpty(dateDue)) {
            addProjectDueDateEditText.setError("Due date is required");
            return;
        }

        if(TextUtils.isEmpty(timeDue)) {
            addProjectTimeEditText.setError("Due time is required");
            return;
        }

        if(TextUtils.isEmpty(dateDue) && TextUtils.isEmpty(timeDue)) {
            addProjectDueDateEditText.setError("Due date is required");
            addProjectTimeEditText.setError("Due time is required");
        } else {
            String dateTime = dateDue + " " + timeDue;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Project theProject = new Project(title, description, LocalDateTime.parse(dateTime, formatter), priority, 12);

            boolean result = daoHelper.addProject(theProject);

            if(result) {
                Toast.makeText(AddProjectActivity.this, "Project was added sucessfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddProjectActivity.this, "Error adding project", Toast.LENGTH_SHORT).show();
            }


            clearInput();

        }




//


    }

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


    private String getProjectPriorityValue() {
        int radioId = addProjectPriorityRadioGroup.getCheckedRadioButtonId();
        projectPriorityRadioBtn = findViewById(radioId);
        return projectPriorityRadioBtn.getText().toString();
    }
}