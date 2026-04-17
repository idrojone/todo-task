package me.idrojone.task_api.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.idrojone.task_api.infrastructure.model.TaskDocument;

public interface TaskMongoRepository extends MongoRepository<TaskDocument, String> {

	java.util.List<TaskDocument> findByCategoryId(String categoryId);

}
