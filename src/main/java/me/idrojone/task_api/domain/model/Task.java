package me.idrojone.task_api.domain.model;

public class Task {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private String categoryId;
    private boolean deleted;
    private boolean deletedByCategory;
    private String createdAt;
    private String updatedAt;



    /**
     * ==============================================================================
     * Getters and Setters
     * ==============================================================================
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeletedByCategory() {
        return deletedByCategory;
    }

    public void setDeletedByCategory(boolean deletedByCategory) {
        this.deletedByCategory = deletedByCategory;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
