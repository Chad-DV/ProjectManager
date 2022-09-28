package com.example.projecto;

import static com.example.projecto.utils.DBUtils.PROJECT_ID;
import static com.example.projecto.utils.DBUtils.USER_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.example.projecto.dao.UserDAOImpl;
import com.example.projecto.model.Project;
import com.example.projecto.utils.ArrayConversionUtils;
import com.example.projecto.utils.DBUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class EditProjectActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private EditText editProjectTitleEditText, editProjectDescriptionEditText, editProjectDueDateEditText, editProjectDueTimeEditText, editProjectChecklistEditText;
    private Button editProjectBtn;
    private CheckBox editProjectRemindMe2WeeksCheckbox, editProjectRemindMe1WeekCheckbox, editProjectRemindMe1DayCheckbox, editProjectRemindMe1HourCheckbox, editProjectRemindMe30MinutesCheckbox;
    private long projectId;
    private ImageView editProjectNavigationBack;
    private ProjectDAOImpl projectHelper;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog timePickerDialog;
    private RadioButton editProjectPriorityRadioBtn;
    private RadioGroup editProjectPriorityRadioGroup;
    private UserDAOImpl userHelper;
    private ImageButton editProjectChecklistBtn;
    private ListView editProjectChecklistListView;
    private List<String> checklistItemList;
    private ProjectChecklistItemAdapter checklistItemAdapter;
    private long userId;
    private String authenticatedUser;
    private Dialog dialog;

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
        editProjectNavigationBack = findViewById(R.id.editProjectNavigationBack);


        editProjectChecklistEditText = findViewById(R.id.editProjectChecklistEditText);
        editProjectChecklistBtn = findViewById(R.id.editProjectChecklistBtn);
        editProjectChecklistListView = findViewById(R.id.editProjectChecklistListView);

        dialog = new Dialog(this);
        checklistItemList = new ArrayList<>();

        projectHelper = new ProjectDAOImpl(this);

        userId = getIntent().getLongExtra(USER_ID, 000);
        authenticatedUser = getIntent().getStringExtra(DBUtils.AUTHENTICATED_USER);


        loadProjectData();


        dateSetListener = (datePicker, year, month, day) -> {
            editProjectDueDateEditText.setText(year + "-" + checkDigit(month + 1)  + "-" + checkDigit(day));
        };

        editProjectChecklistListView.setOnItemLongClickListener(this);
        editProjectChecklistBtn.setOnClickListener(this);
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

            }, currHour,currMinute, true);

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.setTitle("Select a Time");
            timePickerDialog.show();
        } else if(view == editProjectChecklistBtn) {

            String add_item = editProjectChecklistEditText.getText().toString();
            if(TextUtils.isEmpty(add_item)) {
                editProjectChecklistEditText.setError("Please enter a value");
            } else if (checklistItemList.contains(add_item)) {
                Toast.makeText(getBaseContext(), "Item Already Exists", Toast.LENGTH_LONG).show();
            } // Enter the element if it does not exist
            else {
                checklistItemList.add(add_item);
                checklistItemAdapter = new ProjectChecklistItemAdapter(getApplicationContext(), checklistItemList);
                editProjectChecklistListView.setAdapter(checklistItemAdapter);
                ListViewHelper.getListViewSize(editProjectChecklistListView);
                editProjectChecklistEditText.setText("");

            }

        }

        else if(view == editProjectNavigationBack) {
            finish();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

        System.out.println("long clicking");
        final int itemToRemove =position;
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProjectActivity.this); // Ask the user to get the confirmation before deleting an item from the listView
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadProjectData() {
        projectId = getIntent().getLongExtra(PROJECT_ID, -999);
        Project project = projectHelper.getProjectById(projectId);

        // get full date and time as LocalDateTime
        LocalDateTime dueDateAndTime = project.getDateDue();
        // Get date only as String
        String date = dueDateAndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Get time only as String
        String time = dueDateAndTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        String[] remindMeValues = ArrayConversionUtils.convertStringToArray(project.getRemindMeInterval());
        String priority = project.getPriority();

        int selected = -1;

        if(priority.equalsIgnoreCase("High")) {
            selected = 0;
        } else if(priority.equalsIgnoreCase("Medium")) {
            selected = 1;
        } else {
            selected = 2;
        }

        switch (selected) {
            case 0:
                editProjectPriorityRadioGroup.check(R.id.projectPriorityHighRadioBtn);
                break;
            case 1:
                editProjectPriorityRadioGroup.check(R.id.projectPriorityMediumRadioBtn);
                break;
            case 2:
                editProjectPriorityRadioGroup.check(R.id.projectPriorityLowRadioBtn);
                break;
        }


        editProjectTitleEditText.setText(project.getTitle());
        editProjectDescriptionEditText.setText(project.getDescription());
        editProjectDueDateEditText.setText(date);
        editProjectDueTimeEditText.setText(time);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        checklistItemList = gson.fromJson(project.getChecklist(), type);

        checklistItemAdapter = new ProjectChecklistItemAdapter(getApplicationContext(), checklistItemList);
        editProjectChecklistListView.setAdapter(checklistItemAdapter);
        ListViewHelper.getListViewSize(editProjectChecklistListView);



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
        boolean success = true;



        if (TextUtils.isEmpty(title)) {
            editProjectTitleEditText.setError("Title is required");
            success = false;
        }

        if (title.length() < 15) {
            editProjectTitleEditText.setError("Minimum of 15 characters required");
            success = false;
        }

        if (TextUtils.isEmpty(description)) {
            editProjectDescriptionEditText.setError("Description is required");
            success = false;
        }

        if (description.length() < 25) {
            editProjectDescriptionEditText.setError("Minimum of 25 characters required");
            success = false;
        }


        if(TextUtils.isEmpty(dateDue)) {
            editProjectDueDateEditText.setError("Due date is required");
            success = false;
        }

        if(TextUtils.isEmpty(timeDue)) {
            editProjectDueTimeEditText.setError("Due time is required");
            success = false;
        }

        if(TextUtils.isEmpty(dateDue) && TextUtils.isEmpty(timeDue)) {
            editProjectDueDateEditText.setError("Due date is required");
            editProjectDueTimeEditText.setError("Due time is required");
            success = false;

        } else {

            if(success) {
                String dateTime = dateDue + " " + timeDue;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                Project theProject = new Project(projectId, title, description, LocalDateTime.parse(dateTime, formatter), priority, remindMeInterval, checklistItemList.toString(), userId);
                boolean result = projectHelper.editProject(theProject);

                if (result) {
                    clearInput();
                    displayDialog(R.layout.post_edited_success_dialog);
                    Button Okay = dialog.findViewById(R.id.btn_okay);

                    Okay.setOnClickListener(view -> {
                        dialog.dismiss();

                        Intent goToProjectActivityIntent = new Intent(getApplicationContext(), ProjectActivity.class);
                        goToProjectActivityIntent.putExtra(DBUtils.AUTHENTICATED_USER, authenticatedUser);
                        startActivity(goToProjectActivityIntent);
                    });

                    dialog.show();
                } else {
                    Toast.makeText(EditProjectActivity.this, "Error editing project, try again shortly.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void clearInput() {
        editProjectTitleEditText.setText("");
        editProjectDescriptionEditText.setText("");
        editProjectDueDateEditText.setText("");
        editProjectDueTimeEditText.setText("");
        editProjectRemindMe2WeeksCheckbox.setChecked(false);
        editProjectRemindMe1WeekCheckbox.setChecked(false);
        editProjectRemindMe1DayCheckbox.setChecked(false);
        editProjectRemindMe1HourCheckbox.setChecked(false);
        editProjectRemindMe30MinutesCheckbox.setChecked(false);
        editProjectPriorityRadioGroup.setSelected(false);
        checklistItemList.clear();

        editProjectChecklistListView.setAdapter(checklistItemAdapter);
    }


    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private String getProjectPriorityValue() {
        int radioId = editProjectPriorityRadioGroup.getCheckedRadioButtonId();
        editProjectPriorityRadioBtn = findViewById(radioId);
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

        return ArrayConversionUtils.convertArrayToString(msg);
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