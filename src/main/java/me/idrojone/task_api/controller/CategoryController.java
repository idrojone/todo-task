package me.idrojone.task_api.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import me.idrojone.task_api.application.dto.category.CategoryDto;
import me.idrojone.task_api.application.dto.category.CategoryInput;
import me.idrojone.task_api.application.dto.category.CategoryPage;
import me.idrojone.task_api.application.dto.category.CategoryUpdateInput;
import me.idrojone.task_api.application.service.category.CategoryService;

@Controller
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @QueryMapping
    public CategoryPage categories(@Argument Integer offset, @Argument Integer limit) {
        return categoryService.getAllCategories(offset, limit);
    }

    @QueryMapping
    public CategoryDto categoryById(@Argument String id) {
        return categoryService.getCategoryById(id);
    }

    @MutationMapping
    public CategoryDto createCategory(@Argument @Valid CategoryInput input) {
        return categoryService.createNewCategory(input);
    }

    @MutationMapping
    public CategoryDto updateCategory(@Argument String id, @Argument CategoryUpdateInput input) {
        return categoryService.updateCategory(id, input);
    }

    @MutationMapping
    public CategoryDto toggleDeleteCategory(@Argument String id) {
        return categoryService.toggleDeleteCategory(id);
    }
}
