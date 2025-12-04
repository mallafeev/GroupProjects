package com.example.projectflow.model;

public enum TaskStatus {
    PENDING("Передана"),
    IN_PROGRESS("В работе"),
    DONE("Выполнена");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName; // теперь .toString() тоже будет возвращать русское имя
    }
}