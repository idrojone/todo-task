# Copilot Instructions for Task API

## Build and Test Commands

### Build
```bash
./mvnw clean package
```

### Run Tests
```bash
./mvnw test
```

### Run a Single Test Class or Method
```bash
./mvnw test -Dtest=TaskApiApplicationTests
./mvnw test -Dtest=TaskApiApplicationTests#testMethodName
```

### Run the Application Locally
```bash
./mvnw spring-boot:run
```

Requires MongoDB running on `mongodb://localhost:27017/task_db`. Start MongoDB via Docker Compose:
```bash
docker-compose up -d mongodb
```

### Docker Development
```bash
docker-compose up
```

This starts MongoDB with replica set and the Spring Boot application on port 8080. The GraphQL endpoint is available at `http://localhost:8080/graphql`.

---

## Architecture

### High-Level Structure

This is a **Spring Boot 4.0.5 GraphQL API** for a task/todo management system with MongoDB persistence. The architecture follows **domain-driven design with clean architecture layers**:

```
GraphQL Request
    ↓
Controller (@QueryMapping, @MutationMapping)
    ↓
Service (Business Logic)
    ↓
Repository (Domain Interface)
    ↓
Repository Adapter + MongoDB (Infrastructure)
    ↓
MongoDB Document Models
```

### Key Layers

- **`controller/`** - GraphQL entry points using Spring GraphQL annotations (`@QueryMapping`, `@MutationMapping`, `@SchemaMapping`)
- **`application/service/`** - Business logic (interfaces in `*Service.java`, implementations in `*ServiceImpl.java`). Services handle pagination, filtering, and construct `PageInfo` objects for GraphQL responses
- **`application/dto/`** - Data transfer objects for API contracts (separate from domain models)
- **`application/mapper/`** - Converts between domain models and DTOs
- **`domain/model/`** - Core business models (`Task`, `Category`)
- **`domain/repository/`** - Repository interfaces (contracts, no implementation)
- **`domain/exception/`** - Custom domain exceptions (e.g., `NotFoundException`)
- **`infrastructure/repository/`** - Repository implementations using Spring Data MongoDB
- **`infrastructure/model/`** - MongoDB document classes (suffixed with `Document`, e.g., `TaskDocument`)
- **`infrastructure/mapper/`** - Converts between domain models and MongoDB documents

### Data Flow for a Query

1. GraphQL query arrives at `TaskController.findAllTasks()`
2. Controller calls `TaskService.getAllTasks(offset, limit, deleted, categoryIds)`
3. Service queries `TaskRepository.findByCategoryIds()` or `findAll()`
4. Repository adapter `TaskRepositoryAdapter` calls Spring Data's `TaskMongoRepository`
5. MongoDB returns `TaskDocument` objects
6. `TaskDocumentMapper` converts documents → domain `Task` objects
7. `TaskMapper` converts domain models → `TaskDto` DTO objects
8. Service constructs `PageInfoTask` and returns `TaskPage`
9. GraphQL serializes response

### Query and Mutation Coverage

**Queries:**
- `findAllTasks(categoryIds, deleted, offset, limit)` - Paginated task list (supports multi-category filtering)
- `taskById(id)`
- `categories(offset, limit)` - Paginated categories
- `categoryById(id)`

**Mutations:**
- `createTask(input: TaskInput!)` - Required: `title`; optional: `description`, `completed`, `categoryId`
- `updateTask(id, input: TaskUpdateInput!)` - All fields optional
- `toggleTask(id)` - Flip `completed` status
- `toggleDeleteTask(id)` - Soft-delete via `deleted` flag
- `createCategory(input: CategoryInput!)` - Required: `name`
- `updateCategory(id, input: CategoryUpdateInput!)` - Optional: `name`, `isActive`
- `toggleDeleteCategory(id)` - Soft-delete

### Pagination Pattern

All paginated queries return `TaskPage` or `CategoryPage` with:
- `items: [Task]` or `items: [Category]`
- `pageInfo: PageInfoTask` or `pageInfo: PageInfo`
  - `offset`, `limit`, `totalCount`, `hasNextPage`, `hasPreviousPage`
  - `TaskPage` also includes `taskCompleted` (count of completed tasks in current page)

---

## Key Conventions

### Field Naming
- DTOs use **record types** (immutable, compact)
- Domain models are POJOs with private fields
- MongoDB documents use Spring Data `@Field` for explicit mapping when needed

### Repository Pattern
- Domain repositories are interfaces in `domain/repository/` (e.g., `TaskRepository`)
- Implementations are adapters named `*RepositoryAdapter` that delegate to Spring Data repositories (e.g., `TaskMongoRepository extends MongoRepository`)
- All filtering, pagination, and soft-delete logic lives in services, not repositories
- Repositories return domain models (via mappers), not documents

### Service Layer
- Services use constructor injection for repositories
- Services handle:
  - Validation (in addition to `@Valid` on inputs)
  - Business logic (toggling, soft-deletes)
  - Pagination calculations
  - DTO/domain/document conversions (via mappers)
- All `findXxx()` methods in repositories take `offset`, `limit`, and filtering parameters; services calculate `hasNextPage` and `hasPreviousPage`

### Exception Handling
- `NotFoundException` is thrown when querying by ID that doesn't exist
- GraphQL exception handler (`GraphQlExceptionHandler`) transforms domain exceptions into GraphQL-friendly error responses

### GraphQL Schema
- Schema definition is in `src/main/resources/graphql/schema.graphqls`
- Controllers implement schema types via `@QueryMapping`, `@MutationMapping`, and `@SchemaMapping`
- `@SchemaMapping(typeName = "Task", field = "category")` resolves nested `category` field lazily by calling the category service

### Soft Deletes
- `Task` and `Category` both have a `deleted` Boolean field (default `false`)
- Queries filter by `deleted` status unless explicitly passing a filter
- `toggleDeleteTask()` and `toggleDeleteCategory()` set `deleted = !deleted`

### Configuration
- MongoDB URI: `spring.data.mongodb.uri` (default: `mongodb://localhost:27017/task_db`)
  - Override via environment variable `SPRING_DATA_MONGODB_URI`
- CORS is enabled for GraphQL endpoint (see `CorsConfig.java`)
- GraphQL endpoint: `/graphql`
- GraphiQL enabled on `/graphiql` (browser UI for testing)
- Server port: `8080` (configurable via `server.port`)

---

## Testing

- **Test framework**: Spring Boot Test (JUnit 5), includes MongoDB and GraphQL test support
- Basic test scaffold exists in `src/test/java/me/idrojone/task_api/TaskApiApplicationTests.java`
- Tests can use `@DataMongoTest` for MongoDB-only tests or `@SpringBootTest` for full integration tests

---

## Recommended MCP Servers

### 1. **MongoDB / Database Tools**
Essential for working with MongoDB queries and data exploration.
```json
{
  "mcp_servers": {
    "mongodb": {
      "command": "npx",
      "args": ["@modelcontextprotocol/server-mongodb"],
      "env": {
        "MONGODB_URI": "mongodb://localhost:27017/task_db"
      }
    }
  }
}
```

### 2. **Docker** (Optional)
Useful for managing Docker Compose services (MongoDB, app containers).
```json
{
  "docker": {
    "command": "npx",
    "args": ["@modelcontextprotocol/server-docker"]
  }
}
```

**Setup instructions:**
1. Install MCP servers: `npm install -g @modelcontextprotocol/server-mongodb @modelcontextprotocol/server-docker`
2. Add the configurations above to your IDE's MCP server settings
3. Restart the IDE to activate the servers

These servers enable you to:
- Query MongoDB directly without switching to MongoDB Compass
- Check Docker container status and logs
- Test GraphQL queries in real-time against running data
