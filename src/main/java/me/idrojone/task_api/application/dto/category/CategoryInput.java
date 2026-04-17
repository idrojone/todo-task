package me.idrojone.task_api.application.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryInput(@NotBlank String name) {}
