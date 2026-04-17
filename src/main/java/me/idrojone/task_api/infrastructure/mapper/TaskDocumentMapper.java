package me.idrojone.task_api.infrastructure.mapper;

import me.idrojone.task_api.domain.model.Task;
import me.idrojone.task_api.infrastructure.model.TaskDocument;

public final class TaskDocumentMapper {
    private TaskDocumentMapper() {}

    public static TaskDocument toDocument(Task task) {
        if (task == null) {
            return null;
        }
        TaskDocument doc = new TaskDocument();
        doc.setId(task.getId());
        doc.setTitle(task.getTitle());
        doc.setDescription(task.getDescription());
        doc.setCompleted(task.isCompleted());
        doc.setCategoryId(task.getCategoryId());
        doc.setDeleted(task.isDeleted());
        doc.setDeletedByCategory(task.isDeletedByCategory());
        doc.setCreatedAt(task.getCreatedAt());
        doc.setUpdatedAt(task.getUpdatedAt());
        return doc;
    }

    public static Task toDomain(TaskDocument doc) {
        if (doc == null) {
            return null;
        }
        Task task = new Task();
        task.setId(doc.getId());
        task.setTitle(doc.getTitle());
        task.setDescription(doc.getDescription());
        task.setCompleted(doc.isCompleted());
        task.setCategoryId(doc.getCategoryId());
        task.setDeleted(doc.isDeleted());
        task.setDeletedByCategory(doc.isDeletedByCategory());
        task.setCreatedAt(doc.getCreatedAt());
        task.setUpdatedAt(doc.getUpdatedAt());
        return task;
    }
}
