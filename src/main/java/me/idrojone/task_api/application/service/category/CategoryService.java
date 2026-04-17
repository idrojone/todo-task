package me.idrojone.task_api.application.service.category;

import me.idrojone.task_api.application.dto.category.CategoryDto;
import me.idrojone.task_api.application.dto.category.CategoryInput;
import me.idrojone.task_api.application.dto.category.CategoryPage;
import me.idrojone.task_api.application.dto.category.CategoryUpdateInput;

public interface CategoryService {
    CategoryPage getAllCategories(Integer offset, Integer limit);
    CategoryDto getCategoryById(String id);
    CategoryDto createNewCategory(CategoryInput input);
    CategoryDto updateCategory(String id, CategoryUpdateInput input);
    CategoryDto toggleDeleteCategory(String id);
}
