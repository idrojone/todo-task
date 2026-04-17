package me.idrojone.task_api.application.mapper;

import me.idrojone.task_api.application.dto.category.CategoryDto;
import me.idrojone.task_api.application.dto.category.CategoryInput;
import me.idrojone.task_api.domain.model.Category;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryDto toDto(Category c) {
        if (c == null) return null;
        return new CategoryDto(c.getId(), c.getName(), c.isActive());
    }

    public static Category toEntity(CategoryInput input) {
        if (input == null) return null;
        Category c = new Category();
        c.setName(input.name());
        c.setActive(true);
        c.setDeleted(false);
        c.setDeletedAt(null);
        return c;
    }
}
