package me.idrojone.task_api.application.service.task;

import java.util.List;
import me.idrojone.task_api.application.dto.task.TaskDto;
import me.idrojone.task_api.application.dto.task.TaskInput;
import me.idrojone.task_api.application.dto.task.TaskUpdateInput;
import me.idrojone.task_api.application.dto.task.TaskPage;

public interface TaskService {
    TaskPage getAllTasks(int offset, int limit, Boolean deleted, List<String> categoryIds);
    TaskDto getTaskById(String id);
    TaskDto createNewTask(TaskInput input);
    TaskDto toggleTaskStatus(String id);
    TaskDto updateTask(String id, TaskUpdateInput input);
    TaskDto toggleDeleteTask(String id);
}
