package me.idrojone.task_api.application.service.category;

import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;

import org.springframework.stereotype.Service;

import me.idrojone.task_api.application.dto.PageInfo;
import me.idrojone.task_api.application.dto.category.CategoryDto;
import me.idrojone.task_api.application.dto.category.CategoryInput;
import me.idrojone.task_api.application.dto.category.CategoryPage;
import me.idrojone.task_api.application.dto.category.CategoryUpdateInput;
import me.idrojone.task_api.application.mapper.CategoryMapper;
import me.idrojone.task_api.domain.exception.NotFoundException;
import me.idrojone.task_api.domain.model.Category;
import me.idrojone.task_api.domain.repository.CategoryRepository;
import me.idrojone.task_api.domain.repository.TaskRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public CategoryPage getAllCategories(Integer offset, Integer limit) {
        int off = offset == null ? 0 : offset;
        int lim = limit == null ? 10 : limit;
        List<Category> cats = categoryRepository.findAll(off, lim);
        int total = (int) categoryRepository.count();
        List<CategoryDto> items = cats.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
        boolean hasNext = off + items.size() < total;
        boolean hasPrevious = off > 0;
        PageInfo pageInfo = new PageInfo(off, lim, total, hasNext, hasPrevious);
        return new CategoryPage(items, pageInfo);
    }

    @Override
    public CategoryDto getCategoryById(String id) {
        final Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + id));
        return CategoryMapper.toDto(c);
    }

    @Override
    public CategoryDto createNewCategory(CategoryInput input) {
        final Category cat = CategoryMapper.toEntity(input);
        final Category saved = categoryRepository.save(cat);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto updateCategory(String id, CategoryUpdateInput input) {
        final Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + id));

        if (input.name() != null) {
            existing.setName(input.name());
        }
        if (input.isActive() != null) {
            existing.setActive(input.isActive());
        }

        final Category saved = categoryRepository.save(existing);

        // If category was deactivated, remove relation from tasks
        if (input.isActive() != null && !input.isActive()) {
            taskRepository.findByCategoryId(id).forEach(t -> {
                t.setCategoryId(null);
                taskRepository.save(t);
            });
        }

        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto toggleDeleteCategory(String id) {
        final Category c = categoryRepository.findByIdIncludeDeleted(id)
            .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + id));

        boolean newDeleted = !c.isDeleted();
        c.setDeleted(newDeleted);
        c.setDeletedAt(newDeleted ? Instant.now().toString() : null);

        final Category saved = categoryRepository.save(c);

        // Cascade to tasks: mark tasks deleted / restore those deleted by category
        if (newDeleted) {
            taskRepository.findByCategoryId(id).stream()
                .filter(t -> !t.isDeleted())
                .forEach(t -> {
                    t.setDeleted(true);
                    t.setDeletedByCategory(true);
                    t.setUpdatedAt(Instant.now().toString());
                    taskRepository.save(t);
                });
        } else {
            taskRepository.findByCategoryId(id).stream()
                .filter(t -> t.isDeletedByCategory())
                .forEach(t -> {
                    t.setDeleted(false);
                    t.setDeletedByCategory(false);
                    t.setUpdatedAt(Instant.now().toString());
                    taskRepository.save(t);
                });
        }

        return CategoryMapper.toDto(saved);
    }
}
