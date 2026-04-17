package me.idrojone.task_api.application.mapper;

import me.idrojone.task_api.application.dto.task.TaskDto;
import me.idrojone.task_api.application.dto.task.TaskInput;
import me.idrojone.task_api.domain.model.Task;

public final class TaskMapper {
    private TaskMapper() {}
    public static TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskDto(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCategoryId(),
            task.isDeleted(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }

    public static Task toEntity(TaskInput input) {
        if (input == null) {
            return null;
        }
        Task task = new Task();
        task.setTitle(input.title());
        task.setDescription(input.description());
        task.setCompleted(input.completed() != null ? input.completed() : false);
        task.setCategoryId(input.categoryId());
        return task;
    }
}
