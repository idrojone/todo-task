package me.idrojone.task_api.application.dto;

public record PageInfoTask(int offset, int limit, int totalCount, int taskCompleted, boolean hasNextPage, boolean hasPreviousPage) {}