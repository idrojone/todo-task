package me.idrojone.task_api.application.service.task;

import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;

import org.springframework.stereotype.Service;

import me.idrojone.task_api.application.dto.PageInfoTask;
import me.idrojone.task_api.application.dto.task.TaskDto;
import me.idrojone.task_api.application.dto.task.TaskInput;
import me.idrojone.task_api.application.dto.task.TaskUpdateInput;
import me.idrojone.task_api.application.dto.task.TaskPage;
import me.idrojone.task_api.application.mapper.TaskMapper;
import me.idrojone.task_api.domain.exception.NotFoundException;
import me.idrojone.task_api.domain.model.Task;
import me.idrojone.task_api.domain.repository.CategoryRepository;
import me.idrojone.task_api.domain.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskServiceImpl(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TaskPage getAllTasks(int offset, int limit, Boolean deleted, List<String> categoryIds) {
        List<Task> tasks;
        int total;
        int taskCompleted;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            tasks = taskRepository.findByCategoryIds(categoryIds, offset, limit, deleted);
            total = (int) taskRepository.countByCategoryIds(categoryIds, deleted);
            taskCompleted = (int) taskRepository.countCompletedByCategoryIds(categoryIds, deleted);
        } else {
            tasks = taskRepository.findAll(offset, limit, deleted);
            total = (int) taskRepository.count(deleted);
            taskCompleted = (int) taskRepository.countCompleted(deleted);
        }
        List<TaskDto> items = tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
        boolean hasNext = offset + items.size() < total;
        boolean hasPrevious = offset > 0;
        PageInfoTask pageInfo = new PageInfoTask(offset, limit, total, taskCompleted, hasNext, hasPrevious);
        return new TaskPage(items, pageInfo);
    }

    @Override
    public TaskDto getTaskById(String id) {
        final Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tarea no encontrada con ID: " + id));
        return TaskMapper.toDto(task);
    }

    @Override
    public TaskDto createNewTask(TaskInput input) {
        final Task taskToCreate = TaskMapper.toEntity(input);
        if (taskToCreate.getCategoryId() != null) {
            final String catId = taskToCreate.getCategoryId();
            categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + catId));
        }
        taskToCreate.setCreatedAt(Instant.now().toString());
        final Task savedTask = taskRepository.save(taskToCreate);
        return TaskMapper.toDto(savedTask);
    }

    @Override
    public TaskDto toggleTaskStatus(String id) {
        final Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tarea no encontrada con ID: " + id));
        task.setCompleted(!task.isCompleted());
        task.setUpdatedAt(Instant.now().toString());
        final Task savedTask = taskRepository.save(task);
        return TaskMapper.toDto(savedTask);
    }

    @Override
    public TaskDto updateTask(String id, TaskUpdateInput input) {
        final Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tarea no encontrada con ID: " + id));

        if (input.title() != null) {
            task.setTitle(input.title());
        }
        if (input.description() != null) {
            task.setDescription(input.description());
        }

        if (input.completed() != null) {
            task.setCompleted(input.completed());
        }

        final String newCategoryId = input.categoryId();
        if (newCategoryId != null) {
            // ensure category exists
            categoryRepository.findById(newCategoryId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + newCategoryId));
            task.setCategoryId(newCategoryId);
        } else {
            task.setCategoryId(null);
        }

        task.setUpdatedAt(Instant.now().toString());
        final Task saved = taskRepository.save(task);
        return TaskMapper.toDto(saved);
    }

    @Override
    public TaskDto toggleDeleteTask(String id) {
        final Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tarea no encontrada con ID: " + id));
        task.setDeleted(!task.isDeleted());
        task.setUpdatedAt(Instant.now().toString());
        final Task saved = taskRepository.save(task);
        return TaskMapper.toDto(saved);
    }
}
