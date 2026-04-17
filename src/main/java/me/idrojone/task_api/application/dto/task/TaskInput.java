package me.idrojone.task_api.application.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskInput(@NotBlank String title, @Size(max = 500) String description, Boolean completed, String categoryId) {}
