package me.idrojone.task_api.application.dto.task;

public record TaskUpdateInput(String title, String description, Boolean completed, String categoryId) {}
