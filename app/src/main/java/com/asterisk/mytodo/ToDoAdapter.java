package com.asterisk.mytodo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoViewHolder> {

    private final Context context;
    private final ArrayList<ToDoModel> listOfTasks;
    private final ToDoDbHelper database;

    ToDoAdapter(Context context, ArrayList<ToDoModel> listOfTasks) {
        this.context = context;
        this.listOfTasks = listOfTasks;
        database = new ToDoDbHelper(context);
    }


    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_layout, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {
        final ToDoModel toDoModel = listOfTasks.get(position);
        holder.chkBxTask.setText(toDoModel.getTask());

        // Change the status on check/uncheck
        holder.chkBxTask.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            ToDoModel todo = new ToDoModel(toDoModel.getId(), toDoModel.getTask(), (isChecked) ? 1 : 0);
//            database.updateTask(new ToDoModel(toDoModel.getId(), toDoModel.getTask(), (isChecked) ? 1 : 0));

            /* Update status using content provider */
            ContentValues statValue = new ContentValues();
            statValue.put(ToDoDbHelper.COLUMN_ID, todo.getId());
            statValue.put(ToDoDbHelper.COLUMN_TASK, todo.getTask());
            statValue.put(ToDoDbHelper.COLUMN_STAT, todo.getStatus());
            try {
                context.getContentResolver().update(Uri.parse(ToDoProvider.CONTENT_URI + "/" + toDoModel.getId()), statValue, null, null);
                Log.e("Update", "Change status of Task ID:" + toDoModel.getId() + " to " + todo.getStatus());
            } catch (Exception ex) {
                Log.e("Insert", ex.toString());
            }
        });

        // Listener for edit button
        holder.btnEdit.setOnClickListener(view -> editBookDialog(toDoModel));

        // Listener for delete button
        holder.btnDelete.setOnClickListener(view -> deleteTask(toDoModel.getId()));
    }

    private void editBookDialog(ToDoModel toDoModel) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.dialog_add_book_layout, null);

        final EditText taskField = subView.findViewById(R.id.taskField);

        // Retrieve data from book object and set as text on edit text.

        if (toDoModel != null) {
            taskField.setText(toDoModel.getTask());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");
        builder.setView(subView);

        // Instantiate positive button (will be overridden)
        builder.setPositiveButton("SAVE CHANGES", (dialogInterface, i) -> {
            // Do nothing here
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Override the button handler as to prevent closing of the dialog if the title field is empty.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newTask = taskField.getText().toString();

            if (TextUtils.isEmpty(newTask)) {
                Toast.makeText(context, "Task is empty.", Toast.LENGTH_LONG).show();
                taskField.setError("You can't save an empty task");
            } else {
                assert toDoModel != null;
                ToDoModel newTodo = new ToDoModel(toDoModel.getId(), newTask, toDoModel.getStatus());
//                database.updateTask(new ToDoModel(toDoModel.getId(), newTask, toDoModel.getStatus()));

                /* Update using provider */
                ContentValues newValues = new ContentValues();
                newValues.put(ToDoDbHelper.COLUMN_ID, newTodo.getId());
                newValues.put(ToDoDbHelper.COLUMN_TASK, newTodo.getTask());
                newValues.put(ToDoDbHelper.COLUMN_STAT, newTodo.getStatus());

                try {
                    context.getContentResolver().update(Uri.parse(ToDoProvider.CONTENT_URI + "/" + toDoModel.getId()), newValues, null, null);
                } catch (Exception ex) {
                    Log.e("Insert", ex.toString());
                }

                Toast.makeText(context, "Changes saved", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

    }

    private void deleteTask(int ID) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
//                    database.deleteBook(ID);
                    context.getContentResolver().delete(Uri.parse(ToDoProvider.CONTENT_URI + "/" + ID), null, null);
                    Toast.makeText(context, "Task has been deleted", Toast.LENGTH_LONG).show();
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Confirm Delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public int getItemCount() {
        return listOfTasks.size();
    }
}
