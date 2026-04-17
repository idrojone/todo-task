package me.idrojone.task_api.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import me.idrojone.task_api.domain.model.Category;
import me.idrojone.task_api.domain.repository.CategoryRepository;
import me.idrojone.task_api.infrastructure.mapper.CategoryDocumentMapper;
import me.idrojone.task_api.infrastructure.model.CategoryDocument;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    public CategoryRepositoryAdapter(CategoryMongoRepository mongoRepository, MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Category> findAll() {
        Query q = new Query(Criteria.where("deleted").is(false));
        List<CategoryDocument> docs = mongoTemplate.find(q, CategoryDocument.class);
        return docs.stream().map(CategoryDocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Category> findAll(int offset, int limit) {
        Query q = new Query(Criteria.where("deleted").is(false)).skip(offset).limit(limit);
        List<CategoryDocument> docs = mongoTemplate.find(q, CategoryDocument.class);
        return docs.stream().map(CategoryDocumentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(String id) {
        return mongoRepository.findById(id)
            .filter(doc -> !doc.isDeleted())
            .map(CategoryDocumentMapper::toDomain);
    }

    @Override
    public Optional<Category> findByIdIncludeDeleted(String id) {
        return mongoRepository.findById(id)
                .map(CategoryDocumentMapper::toDomain);
    }

    @Override
    public long count() {
        Query q = new Query(Criteria.where("deleted").is(false));
        return mongoTemplate.count(q, CategoryDocument.class);
    }

    @Override
    public Category save(Category category) {
        var doc = CategoryDocumentMapper.toDocument(category);
        var saved = mongoRepository.save(doc);
        return CategoryDocumentMapper.toDomain(saved);
    }
}
