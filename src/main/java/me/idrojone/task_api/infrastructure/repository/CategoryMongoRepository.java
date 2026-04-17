package me.idrojone.task_api.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.idrojone.task_api.infrastructure.model.CategoryDocument;

public interface CategoryMongoRepository  extends MongoRepository<CategoryDocument, String> {

    
} 