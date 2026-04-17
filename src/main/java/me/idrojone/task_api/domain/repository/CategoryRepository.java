package me.idrojone.task_api.domain.repository;

import java.util.List;
import java.util.Optional;

import me.idrojone.task_api.domain.model.Category;


public interface CategoryRepository {
    List<Category> findAll();
    List<Category> findAll(int offset, int limit);
    Optional<Category> findById(String id);
    Optional<Category> findByIdIncludeDeleted(String id);
    long count();
    Category save(Category category);
}
