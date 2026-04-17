package me.idrojone.task_api.infrastructure.mapper;

import me.idrojone.task_api.domain.model.Category;
import me.idrojone.task_api.infrastructure.model.CategoryDocument;

public final class CategoryDocumentMapper {
    private CategoryDocumentMapper() {}

    public static CategoryDocument toDocument(Category category) {
        if (category == null) return null;
        CategoryDocument doc = new CategoryDocument();
        doc.setId(category.getId());
        doc.setName(category.getName());
        doc.setActive(category.isActive());
        doc.setDeleted(category.isDeleted());
        doc.setDeletedAt(category.getDeletedAt());
        return doc;
    }

    public static Category toDomain(CategoryDocument doc) {
        if (doc == null) return null;
        Category c = new Category();
        c.setId(doc.getId());
        c.setName(doc.getName());
        c.setActive(doc.isActive());
        c.setDeleted(doc.isDeleted());
        c.setDeletedAt(doc.getDeletedAt());
        return c;
    }
}
