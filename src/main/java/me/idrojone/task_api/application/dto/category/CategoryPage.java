package me.idrojone.task_api.application.dto.category;

import java.util.List;

import me.idrojone.task_api.application.dto.PageInfo;

public record CategoryPage(List<CategoryDto> items, PageInfo pageInfo) {}
