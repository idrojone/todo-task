package me.idrojone.task_api.application.dto;

public record PageInfo(int offset, int limit, int totalCount, boolean hasNextPage, boolean hasPreviousPage) {}
