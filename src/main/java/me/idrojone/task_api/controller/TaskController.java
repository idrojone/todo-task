package me.idrojone.task_api.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import me.idrojone.task_api.application.dto.category.CategoryDto;
import java.util.List;
import me.idrojone.task_api.application.dto.task.TaskDto;
import me.idrojone.task_api.application.dto.task.TaskInput;
import me.idrojone.task_api.application.dto.task.TaskUpdateInput;
import me.idrojone.task_api.application.dto.task.TaskPage;
import me.idrojone.task_api.application.service.category.CategoryService;
import me.idrojone.task_api.application.service.task.TaskService;

@Controller
@Validated
public class TaskController {
    private final TaskService taskService;
    private final CategoryService categoryService;

    public TaskController(TaskService taskService, CategoryService categoryService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @QueryMapping
    public TaskPage findAllTasks(@Argument("categoryIds") List<String> categoryIds,
                                @Argument Boolean deleted,
                                @Argument Integer offset,
                                @Argument Integer limit) {
        int off = offset == null ? 0 : offset;
        int lim = limit == null ? 10 : limit;
        return taskService.getAllTasks(off, lim, deleted, categoryIds);
    }

    @SchemaMapping(typeName = "Task", field = "category")
    public CategoryDto category(TaskDto task) {
        if (task == null || task.categoryId() == null) return null;
        return categoryService.getCategoryById(task.categoryId());
    }

    @QueryMapping
    public TaskDto taskById(@Argument String id) {
        return taskService.getTaskById(id);
    }

    @MutationMapping
    public TaskDto createTask(@Argument @Valid TaskInput input) {
        return taskService.createNewTask(input);
    }

    @MutationMapping
    public TaskDto toggleTask(@Argument String id) {
        return taskService.toggleTaskStatus(id);
    }

    @MutationMapping
    public TaskDto updateTask(@Argument String id, @Argument TaskUpdateInput input) {
        return taskService.updateTask(id, input);
    }

    @MutationMapping
    public TaskDto toggleDeleteTask(@Argument String id) {
        return taskService.toggleDeleteTask(id);
    }
}
