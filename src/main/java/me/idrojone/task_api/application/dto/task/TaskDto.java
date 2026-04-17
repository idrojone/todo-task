package me.idrojone.task_api.application.dto.task;

public record TaskDto(String id, String title, String description, boolean completed, String categoryId, boolean deleted, String createdAt, String updatedAt) {}
