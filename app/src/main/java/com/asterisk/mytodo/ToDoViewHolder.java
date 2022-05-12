package com.asterisk.mytodo;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoViewHolder extends RecyclerView.ViewHolder {
    CheckBox chkBxTask;
    ImageButton btnEdit, btnDelete;


    public ToDoViewHolder(@NonNull View itemView) {
        super(itemView);
        chkBxTask = itemView.findViewById(R.id.checkBox);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }
}
