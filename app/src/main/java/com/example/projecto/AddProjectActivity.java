package com.example.projecto;

import static com.example.projecto.utils.DBUtils.USER_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.projecto.dao.ProjectDAOImpl;
import com.example.projecto.model.Project;
import com.example.projecto.utils.ArrayConversionUtils;
import com.example.projecto.utils.DBUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddProjectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener{

    private Button addProjectBtn;
    private EditText addProjectTitleEditText, addProjectDescriptionEditText, addProjectDueDateEditText, addProjectTimeEditText,addProjectChecklistEditText;
    private RadioGroup addProjectPriorityRadioGroup;
    private ImageView addProjectNavigationBack;
    private CheckBox addProjectRemindMe2WeeksCheckbox, addProjectRemindMe1WeekCheckbox, addProjectRemindMe1DayCheckbox, addProjectRemindMe1HourCheckbox, addProjectRemindMe30MinutesCheckbox;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private ProjectDAOImpl projectHelper;
    private long userId;
    private RadioButton projectPriorityRadioBtn;
    private Dialog dialog;



    private ImageButton addProjectChecklistBtn;
    private ListView addProjectChecklistListView;
    private List<String> checklistItemList;
    private ProjectChecklistItemAdapter checklistItemAdapter;
    private String authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        projectHelper = new ProjectDAOImpl(this);
        dialog = new Dialog(this);


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
        addProjectChecklistBtn = findViewById(R.id.addProjectChecklistBtn);
        addProjectChecklistListView = findViewById(R.id.addProjectChecklistListView);
        checklistItemList = new ArrayList<>();

        authenticatedUser =  getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);
        userId = getIntent().getLongExtra(USER_ID, -999);


        dateSetListener = (datePicker, year, month, day) -> {
            addProjectDueDateEditText.setText(year + "-" + checkDigit(month + 1)  + "-" + checkDigit(day));
        };



        addProjectChecklistListView.setOnItemLongClickListener(this);
        addProjectBtn.setOnClickListener(this);
        addProjectDueDateEditText.setOnClickListener(this);
        addProjectTimeEditText.setOnClickListener(this);
        addProjectChecklistBtn.setOnClickListener(this);
        addProjectNavigationBack.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
        startActivity(intent);

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

            }, currHour,currMinute, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Select a Time");
            timePickerDialog.show();
        } else if(view == addProjectChecklistBtn) {

            String add_item = addProjectChecklistEditText.getText().toString();

            if(TextUtils.isEmpty(add_item)) {
                addProjectChecklistEditText.setError("Please enter a value");
            } else if (checklistItemList.contains(add_item)) {
                Toast.makeText(getBaseContext(), "Item Already Exists", Toast.LENGTH_LONG).show();
            } else {
                checklistItemList.add(add_item);
                checklistItemAdapter = new ProjectChecklistItemAdapter(getApplicationContext(), checklistItemList);
                addProjectChecklistListView.setAdapter(checklistItemAdapter);
                ListViewHelper.getListViewSize(addProjectChecklistListView);
                System.out.println("CHECKLIST AFTER ADDING: " + checklistItemList );
                addProjectChecklistEditText.setText("");
            }

        } else if(view == addProjectNavigationBack) {
             finish();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final int itemToRemove =position;
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProjectActivity.this); // Ask the user to get the confirmation before deleting an item from the listView
        builder.setMessage("Do you want to delete").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checklistItemList.remove(itemToRemove);
                checklistItemAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Item Deleted", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("Cancel", null).show();



        return true;
    }



    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addProject() {


        String title = addProjectTitleEditText.getText().toString().trim();
        String description = addProjectDescriptionEditText.getText().toString().trim();
        String dateDue = addProjectDueDateEditText.getText().toString().trim();
        String timeDue = addProjectTimeEditText.getText().toString().trim();
        String priority = getProjectPriorityValue();
        String remindMeInterval = getProjectRemindMeValues();
        String checkList = ArrayConversionUtils.convertArrayToString(checklistItemList.toArray(new String[checklistItemList.size()]));

        boolean success = true;


        if (TextUtils.isEmpty(title)) {
            addProjectTitleEditText.setError("Title is required");
            success = false;
        }

//        if (title.length() < 15) {
//            addProjectTitleEditText.setError("Minimum of 15 characters required");
//            success = false;
//        }
//
//        if (TextUtils.isEmpty(description)) {
//            addProjectDescriptionEditText.setError("Description is required");
//            success = false;
//        }
//
//        if (description.length() < 25) {
//            addProjectDescriptionEditText.setError("Minimum of 25 characters required");
//            success = false;
//        }

        if (TextUtils.isEmpty(dateDue)) {
            addProjectDueDateEditText.setError("Due date is required");
            success = false;

        }

        if (TextUtils.isEmpty(timeDue)) {
            addProjectTimeEditText.setError("Due time is required");
            success = false;

        }

        if (TextUtils.isEmpty(dateDue) && TextUtils.isEmpty(timeDue)) {
            addProjectDueDateEditText.setError("Due date is required");
            addProjectTimeEditText.setError("Due time is required");
            success = false;

        }

        if(success) {
            System.out.println("dateDue: " + dateDue);
            System.out.println("timeDue: " + timeDue);
            String dateTime = dateDue + " " + timeDue;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            System.out.println(LocalDateTime.parse(dateTime, formatter));


            Project theProject = new Project(title, description, LocalDateTime.parse(dateTime, formatter), priority, remindMeInterval, checkList, userId);


            System.out.println(theProject);
            boolean result = projectHelper.addProject(theProject, authenticatedUser);
            if(result) {
                clearInput();
                displayDialog(R.layout.post_created_success_dialog);
                Button Okay = dialog.findViewById(R.id.btn_okay);

                Okay.setOnClickListener(view -> {
                    dialog.dismiss();
                        Intent goToProjectActivityIntent = new Intent(getApplicationContext(), ProjectActivity.class);
                        goToProjectActivityIntent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
                        startActivity(goToProjectActivityIntent);
                });

                dialog.show();


            }
        }


    }


    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void clearInput() {
        addProjectTitleEditText.setText("");
        addProjectDescriptionEditText.setText("");
        addProjectDueDateEditText.setText("");
        addProjectTimeEditText.setText("");
        addProjectRemindMe2WeeksCheckbox.setChecked(false);
        addProjectRemindMe1WeekCheckbox.setChecked(false);
        addProjectRemindMe1DayCheckbox.setChecked(false);
        addProjectRemindMe1HourCheckbox.setChecked(false);
        addProjectRemindMe30MinutesCheckbox.setChecked(false);
        addProjectPriorityRadioGroup.setSelected(false);
        checklistItemList.clear();

        addProjectChecklistListView.setAdapter(checklistItemAdapter);
    }

    public String getProjectRemindMeValues() {
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


        return ArrayConversionUtils.convertArrayToString(msg);
    }


    private String getProjectPriorityValue() {
        int radioId = addProjectPriorityRadioGroup.getCheckedRadioButtonId();
        projectPriorityRadioBtn = findViewById(radioId);
        return projectPriorityRadioBtn.getText().toString();
    }

    private void displayDialog(int layoutView) {
        dialog.setContentView(layoutView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

    }
}