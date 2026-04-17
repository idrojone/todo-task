package me.idrojone.task_api.application.dto.task;

import java.util.List;

import me.idrojone.task_api.application.dto.PageInfoTask;

public record TaskPage(List<TaskDto> items, PageInfoTask pageInfo) {}
