package com.asterisk.mytodo;

public class ToDoModel {

    public int getId() {
        return _id;
    }

    public int getStatus() {
        return status;
    }

    public String getTask() {
        return task;
    }

    private int _id;
    private final int status;
    private final String task;

    public ToDoModel(String task, int status) {
        this.status = status;
        this.task = task;
    }

    public ToDoModel(int id, String task, int status) {
        _id = id;
        this.status = status;
        this.task = task;
    }

}
