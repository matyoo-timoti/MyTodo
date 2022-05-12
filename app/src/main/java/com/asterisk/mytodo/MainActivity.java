package com.asterisk.mytodo;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ToDoDbHelper dbHelper;
    ArrayList<ToDoModel> allTasks;
    TextView txtViewEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Continue drawing the main activity views.
        RecyclerView bookListView = findViewById(R.id.taskListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bookListView.setLayoutManager(linearLayoutManager);
        bookListView.setHasFixedSize(true);

        dbHelper = new ToDoDbHelper(this);
        allTasks = dbHelper.listOfTasks();
        txtViewEmptyList = findViewById(R.id.textViewNoItem);

        ToDoAdapter bkAdapter = new ToDoAdapter(this, allTasks);
        bookListView.setAdapter(bkAdapter);

        if (allTasks.size() > 0) {
            txtViewEmptyList.setVisibility(View.GONE);
            bookListView.setVisibility(View.VISIBLE);
        } else {
            txtViewEmptyList.setVisibility(View.VISIBLE);
            bookListView.setVisibility(View.GONE);
        }

        // Floating button on click
        ExtendedFloatingActionButton btnAddNew = findViewById(R.id.fabAddNew);
        btnAddNew.setOnClickListener(view -> addBookDialog());
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Menu buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        dbHelper.deleteAll();
        Toast.makeText(MainActivity.this, "All books have been deleted", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
        return true;
    }

    // Add new task dialog
    private void addBookDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.dialog_add_book_layout, null);

        final EditText editTextTask = subView.findViewById(R.id.taskField);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");
        builder.setView(subView);

        // Dialog positive button and instantiate positive button.
        builder.setPositiveButton("SAVE", (dialogInterface, i) -> {
            // Do nothing here
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button handler after showing the dialog. This is to prevent termination of the dialog if the title field is empty.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String task = editTextTask.getText().toString();
            if (TextUtils.isEmpty(task)) {
                Toast.makeText(this, "Task is empty", Toast.LENGTH_LONG).show();
                editTextTask.setError("Please add a task");
            } else {
                ToDoModel newTask = new ToDoModel(task, 0);
//                dbHelper.addTask(newTask);

                /* Insert task using provider */
                try {
                    ContentValues values = getValues(newTask.getTask(), newTask.getStatus());
                    Uri uri = getContentResolver().insert(ToDoProvider.CONTENT_URI, values);
                }catch (Exception ex) {
                    Log.e("Insert", ex.toString());
                }
                Toast.makeText(this, "New task added", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
    }

    public ContentValues getValues(String task, int status) {
        ContentValues values = new ContentValues();
        values.put(ToDoDbHelper.COLUMN_TASK, task);
        values.put(ToDoDbHelper.COLUMN_STAT, status);
        return values;
    }
}