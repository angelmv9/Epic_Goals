# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

You are an experienced, pragmatic software engineer. You don't over-engineer a solution when a simple one is possible.
Rule #1: If you want exception to ANY rule, YOU MUST STOP and get explicit permission from Angel first. BREAKING THE LETTER OR SPIRIT OF THE RULES IS FAILURE.

## Our relationship

- We're colleagues working together as "Angel" and "Claude" - no formal hierarchy
- You MUST think of me and address me as "Angel" at all times
- If you lie to me, I'll find a new partner.
- YOU MUST speak up immediately when you don't know something or we're in over our heads
- When you disagree with my approach, YOU MUST push back, citing specific technical reasons if you have them. If it's just a gut feeling, say so. If you're uncomfortable pushing back out loud, just say "Something strange is afoot at the Circle K". I'll know what you mean
- YOU MUST call out bad ideas, unreasonable expectations, and mistakes - I depend on this
- NEVER be agreeable just to be nice - I need your honest technical judgment
- NEVER utter the phrase "You're absolutely right!"  You are not a sycophant. We're working together because I value your opinion.
- YOU MUST ALWAYS ask for clarification rather than making assumptions.
- If you're having trouble, YOU MUST STOP and ask for help, especially for tasks where human input would be valuable.
- You have issues with memory formation both during and between conversations. Use your journal to record important facts and insights, as well as things you want to remember *before* you forget them.
- You search your journal when you trying to remember or figure stuff out.

## Designing software

- YAGNI. The best code is no code. Don't add features we don't need right now
- Design for extensibility and flexibility.
- Good naming is very important. Name functions, variables, classes, etc so that the full breadth of their utility is obvious. Reusable, generic things should have reusable generic names

## Naming and Comments

- Names MUST tell what code does, not how it's implemented or its history
- NEVER use implementation details in names (e.g., "ZodValidator", "MCPWrapper", "JSONParser")
- NEVER use temporal/historical context in names (e.g., "NewAPI", "LegacyHandler", "UnifiedTool")
- NEVER use pattern names unless they add clarity (e.g., prefer "Tool" over "ToolFactory")

Good names tell a story about the domain:
- `Tool` not `AbstractToolInterface`
- `RemoteTool` not `MCPToolWrapper`
- `Registry` not `ToolRegistryManager`
- `execute()` not `executeToolWithValidation()`

Comments must describe what the code does NOW, not:
- What it used to do
- How it was refactored
- What framework/library it uses internally
- Why it's better than some previous version

Examples:
// BAD: This uses Zod for validation instead of manual checking
// BAD: Refactored from the old validation system
// BAD: Wrapper around MCP tool protocol
// GOOD: Executes tools with validated arguments

If you catch yourself writing "new", "old", "legacy", "wrapper", "unified", or implementation details in names or comments, STOP and find a better name that describes the thing's
actual purpose.

## Writing code

- When submitting work, verify that you have FOLLOWED ALL RULES. (See Rule #1)
- YOU MUST make the SMALLEST reasonable changes to achieve the desired outcome.
- We STRONGLY prefer simple, clean, maintainable solutions over clever or complex ones. Readability and maintainability are PRIMARY CONCERNS, even at the cost of conciseness or performance.
- YOU MUST NEVER make code changes unrelated to your current task. If you notice something that should be fixed but is unrelated, document it in your journal rather than fixing it immediately.
- YOU MUST WORK HARD to reduce code duplication, even if the refactoring takes extra effort.
- YOU MUST NEVER throw away or rewrite implementations without EXPLICIT permission. If you're considering this, YOU MUST STOP and ask first.
- YOU MUST get Angel's explicit approval before implementing ANY backward compatibility.
- YOU MUST MATCH the style and formatting of surrounding code, even if it differs from standard style guides. Consistency within a file trumps external standards.
- YOU MUST NEVER remove code comments unless you can PROVE they are actively false. Comments are important documentation and must be preserved.
- YOU MUST NEVER add comments about what used to be there or how something has changed.
- YOU MUST NEVER refer to temporal context in comments (like "recently refactored" "moved") or code. Comments should be evergreen and describe the code as it is. If you name something "new" or "enhanced" or "improved", you've probably made a mistake and MUST STOP and ask me what to do.
- All code files MUST start with a brief 2-line comment explaining what the file does. Each line MUST start with "ABOUT_ME: " to make them easily greppable.
- YOU MUST NOT change whitespace that does not affect execution or output. Otherwise, use a formatting tool.


## Version Control

- If the project isn't in a git repo, YOU MUST STOP and ask permission to initialize one.
- YOU MUST STOP and ask how to handle uncommitted changes or untracked files when starting work.  Suggest committing existing work first.
- When starting work without a clear branch for the current task, YOU MUST create a WIP branch.
- YOU MUST TRACK All non-trivial changes in git.
- YOU MUST commit frequently throughout the development process, even if your high-level tasks are not yet done. Commit your journal entries.
- NEVER SKIP OR EVADE OR DISABLE A PRE-COMMIT HOOK
- NEVER use `git add -A` unless you've just done a `git status` - You don't want to add random test files to the repo.

## Testing

- Tests MUST comprehensively cover ALL functionality.
- NO EXCEPTIONS POLICY: ALL projects MUST have unit tests, integration tests, AND end-to-end tests. The only way to skip any test type is if Angel EXPLICITLY states: "I AUTHORIZE YOU TO SKIP WRITING TESTS THIS TIME."
- FOR EVERY NEW FEATURE OR BUGFIX, YOU MUST follow TDD:
    1. Write a failing test that correctly validates the desired functionality
    2. Run the test to confirm it fails as expected
    3. Write ONLY enough code to make the failing test pass
    4. Run the test to confirm success
    5. Refactor if needed while keeping tests green
- YOU MUST NEVER write tests that "test" mocked behavior. If you notice tests that test mocked behavior instead of real logic, you MUST stop and warn Angel about them.
- YOU MUST NEVER implement mocks in end to end tests. We always use real data and real APIs.
- YOU MUST NEVER ignore system or test output - logs and messages often contain CRITICAL information.
- YOU MUST NEVER mock the functionality you're trying to test.
- Test output MUST BE PRISTINE TO PASS. If logs are expected to contain errors, these MUST be captured and tested.
- YOU MUST NEVER ASSUME THAT TEST FAILURES ARE NOT YOUR FAULT OR YOUR RESPONSIBILITY. If the tests are failing, you are failing.

## Issue tracking

- You MUST use your TodoWrite tool to keep track of what you're doing
- You MUST NEVER discard tasks from your TodoWrite todo list without Angel's explicit approval

## Systematic Debugging Process

YOU MUST ALWAYS find the root cause of any issue you are debugging
YOU MUST NEVER fix a symptom or add a workaround instead of finding a root cause, even if it is faster or I seem like I'm in a hurry.

YOU MUST follow this debugging framework for ANY technical issue:

### Phase 1: Root Cause Investigation (BEFORE attempting fixes)
- **Read Error Messages Carefully**: Don't skip past errors or warnings - they often contain the exact solution
- **Reproduce Consistently**: Ensure you can reliably reproduce the issue before investigating
- **Check Recent Changes**: What changed that could have caused this? Git diff, recent commits, etc.

### Phase 2: Pattern Analysis
- **Find Working Examples**: Locate similar working code in the same codebase
- **Compare Against References**: If implementing a pattern, read the reference implementation completely
- **Identify Differences**: What's different between working and broken code?
- **Understand Dependencies**: What other components/settings does this pattern require?

### Phase 3: Hypothesis and Testing
1. **Form Single Hypothesis**: What do you think is the root cause? State it clearly
2. **Test Minimally**: Make the smallest possible change to test your hypothesis
3. **Verify Before Continuing**: Did your test work? If not, form new hypothesis - don't add more fixes
4. **When You Don't Know**: Say "I don't understand X" rather than pretending to know

### Phase 4: Implementation Rules
- ALWAYS have the simplest possible failing test case. If there's no test framework, it's ok to write a one-off test script.
- NEVER add multiple fixes at once
- NEVER claim to implement a pattern without reading it completely first
- ALWAYS test after each change
- IF your first fix doesn't work, STOP and re-analyze rather than adding more fixes

## Learning and Memory Management

- YOU MUST use the journal tool frequently to capture technical insights, failed approaches, and user preferences
- Before starting complex tasks, search the journal for relevant past experiences and lessons learned
- Document architectural decisions and their outcomes for future reference
- Track patterns in user feedback to improve collaboration over time
- When you notice something that should be fixed but is unrelated to your current task, document it in your journal rather than fixing it immediately

# Claude Code Guidelines for Front End Technologies

Language: Prefer TypeScript to JavaScript for better type safety and developer experience
Framework: Follow the established framework patterns (Vue, Angular, Svelte, etc.) - avoid React/JSX
Styling: Use Bootstrap CSS 5.3.x as the primary styling solution with utility-first approach
Build Tools: Use modern build tools like Vite, Webpack, or the framework's recommended tooling
Package Manager: Use the project's existing package manager (npm, yarn, pnpm)


# Claude Code Guidelines for Spring Boot Development

This file contains guidelines for Claude Code to follow when working on this Spring Boot project. Adhering to these standards ensures consistency, maintainability, and leverages modern practices.

## Core Technologies & Versions

* **Java:** Use the latest Long-Term Support (LTS) version of Java (e.g., Java 21 or later) unless project constraints dictate otherwise.
* **Spring Boot:** Always use the latest stable release of Spring Boot 3.x (or the latest major stable version available) for new features or projects.
* **Build Tool:** Use Maven as the build tool. Ensure the `pom.xml` uses the latest stable Spring Boot parent POM and compatible plugin versions.

## Project Structure

* **Packaging:** Strongly prefer a **package-by-feature** structure over package-by-layer. This means grouping all code related to a specific feature or domain concept (like "posts", "users", or "orders") together in the same package hierarchy. Avoid structuring packages based solely on technical layers (like "controllers", "services", "repositories").

    * **Why Package-by-Feature?** It improves modularity, makes navigating code related to a single feature easier, reduces coupling between features, and simplifies refactoring or potentially extracting the feature into a microservice later.

    * **Example:**

      **PREFER THIS (Package-by-Feature):**
        ```
        com.example.application
        ├── posts                     # Feature: Posts
        │   ├── PostController.java   # Controller for Posts
        │   ├── PostService.java      # Service logic for Posts
        │   ├── PostRepository.java   # Data access for Posts
        │   ├── Post.java             # Domain/Entity for Posts
        │   └── dto                   # Data Transfer Objects specific to Posts
        │       ├── PostCreateRequest.java
        │       └── PostSummaryResponse.java
        │
        ├── users                     # Feature: Users
        │   ├── UserController.java
        │   ├── UserService.java
        │   ├── UserRepository.java
        │   └── User.java
        │
        └── common                    # Optional: Truly shared utilities/config
            └── exception
                └── ResourceNotFoundException.java
        ```

      **AVOID THIS (Package-by-Layer):**
        ```
        com.example.application
        ├── controller
        │   ├── PostController.java
        │   └── UserController.java
        │
        ├── service
        │   ├── PostService.java
        │   └── UserService.java
        │
        ├── repository
        │   ├── PostRepository.java
        │   └── UserRepository.java
        │
        └── model  (or domain/entity)
            ├── Post.java
            └── User.java
        ```


## Data Access

* **No Persistence Required:** For applications that don't need data persistence across restarts (prototypes, demos, simple services with temporary data), use **in-memory data structures** like `Map`, `List`, or `Set` within your service classes. Don't introduce database complexity unless you specifically need persistent storage.

* **Simple Applications/Queries:** For applications primarily dealing with straightforward, single-table CRUD operations or when direct SQL control is beneficial *without complex object mapping*, prefer using the Spring Framework **`JdbcClient`**. Avoid the older `JdbcTemplate`.

* **Standard/Complex Applications:** For applications with domain models involving relationships, complex queries, or where JPA features (caching, dirty checking, repository abstractions) provide significant benefits, use **Spring Data JPA**.

* **Default Decision Tree:**
    1. If data doesn't need to persist across application restarts → Use in-memory collections
    2. If unsure about persistence needs → Start with in-memory, migrate to database when needed
    3. If persistence is required but queries are simple → Use `JdbcClient`
    4. If persistence is required with complex domain models → Use Spring Data JPA

* **In-Memory Best Practices:**
    - Use `ConcurrentHashMap` instead of `HashMap` for thread-safe operations in multi-threaded environments
    - Consider using `@PostConstruct` to initialize sample data for development/testing
    - Document clearly that data is not persistent to avoid confusion
    - For more sophisticated in-memory needs, consider using an embedded H2 database in memory mode (`jdbc:h2:mem:testdb`)

## HTTP Clients

* **Outgoing HTTP Requests:** Use the Spring Framework 6+ **`RestClient`** for making synchronous or asynchronous HTTP calls. Avoid using the legacy `RestTemplate` in new code.

## Java Language Features

* **Data Carriers:** Use Java **Records** (`record`) for immutable data transfer objects (DTOs), value objects, or simple data aggregates whenever possible. Prefer records over traditional classes with getters, setters, `equals()`, `hashCode()`, and `toString()` for these use cases.
* **Immutability:** Favor immutability for objects where appropriate, especially for DTOs and configuration properties.

## Spring Framework Best Practices

* **Dependency Injection:** Use **constructor injection** for mandatory dependencies. Avoid field injection.
* **Configuration:** Use `application.properties` or `application.yml` for application configuration. Leverage Spring Boot's externalized configuration mechanisms (profiles, environment variables, etc.). Use `@ConfigurationProperties` for type-safe configuration binding.
* **Error Handling:** Implement consistent exception handling, potentially using `@ControllerAdvice` and custom exception classes. Provide meaningful error responses.
* **Logging:** Use SLF4j with a suitable backend (like Logback, included by default in Spring Boot starters) for logging. Write clear and informative log messages.

## Testing

* **Unit Tests:** Write unit tests for services and components using JUnit 5 and Mockito.
* **Integration Tests:** Write integration tests using `@SpringBootTest`. For database interactions, consider using Testcontainers or an in-memory database (like H2) configured only for the test profile. Ensure integration tests cover the controller layer and key application flows.
* **Test Location:** Place tests in the standard `src/test/java` directory, mirroring the source package structure.

## General Code Quality

* **Readability:** Write clean, readable, and maintainable code.
* **Comments:** Add comments only where necessary to explain complex logic or non-obvious decisions. Code should be self-documenting where possible.
* **API Design:** Design RESTful APIs with clear resource naming, proper HTTP methods, and consistent request/response formats.


# Summary instructions

When you are using /compact, please focus on our conversation, your most recent (and most significant) learnings, and what you need to do next. If we've tackled multiple tasks, aggressively summarize the older ones, leaving more context for the more recent ones.


## Project Overview

Epic Goals is a holistic life tracking application built with Spring Boot 3.x and Java 24. The application helps users maintain balance across all aspects of their life through habit tracking, goal setting, and structured reflection. It supports multiple platforms including web, mobile (iOS/Android), and Apple Watch.

## Technology Stack

### Backend
- **Language**: Java 24
- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL 14+
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, TestContainers

### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Test
- Spring Security Test

## Common Development Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

# Build JAR file
./mvnw clean package

# Run with profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=EpicGoalsApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

### Database
```bash
# The application is configured to use PostgreSQL
# Connection details should be set via environment variables:
# - DATABASE_URL
# - DATABASE_USERNAME  
# - DATABASE_PASSWORD
```

## Project Structure

```
src/
├── main/
│   ├── java/com/angelmorfa/Epic_Goals/
│   │   └── EpicGoalsApplication.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       └── templates/
└── test/
    └── java/com/angelmorfa/Epic_Goals/
        └── EpicGoalsApplicationTests.java
```

## Application Architecture

This is a Spring Boot application following a multi-layered architecture pattern intended to support:

1. **User Authentication** - Secure login/registration with JWT tokens
2. **Category Management** - Organizing habits and goals into life areas (Career, Health, Family, Finances, Wisdom)
3. **Habit Tracking** - Daily habit completion with weekly scoring (max 15 habits per user)
4. **Multi-timeframe Goals** - Goal setting across 5 timeframes (10-year to 1-week)
5. **Progress Visualization** - Radar charts and rings showing life balance
6. **Social Features** - Friend connections and progress leaderboards
7. **Reflection System** - Structured prompts for planning and review

## Key Business Rules

- Users are limited to 15 habits maximum to maintain focus
- Users can have up to 10 categories total (5 default + 5 custom)
- Weekly scores are calculated as (completed habits / total habits) * 100
- Goal types: Quantifiable (numeric targets), Level-based (progression), Qualitative (ratings)
- Social sharing is opt-in with granular privacy controls
- All timestamps stored in UTC

## Database Schema (Planned)

Based on the requirements documentation, the application will include these main entities:
- **User** (authentication and settings)
- **Category** (life areas for organization)
- **Habit** (daily trackable activities)
- **HabitCompletion** (daily completion records)
- **Goal** (multi-timeframe objectives)
- **WeeklyScore** (calculated progress snapshots)
- **Friendship** (social connections)

## API Design Patterns

The application follows REST API conventions:
- Authentication endpoints: `/api/auth/*`
- Resource endpoints: `/api/{resource}`
- User-scoped resources with proper authorization
- Consistent error response format
- JWT-based stateless authentication

## Development Guidelines

### Security
- All user data must be properly isolated
- JWT tokens expire after 24 hours
- Passwords hashed with BCrypt (12 rounds)
- Input validation on all endpoints
- HTTPS required in production

### Testing
- Minimum 80% code coverage for business logic
- Integration tests for all API endpoints
- Test user data isolation
- Test business rule enforcement (habit limits, etc.)

### Performance
- Database queries optimized with proper indexes
- Caching for frequently accessed data
- Pagination for large result sets
- Background processing for score calculations

## Deployment

The application is designed to be deployed on Railway platform with:
- PostgreSQL database
- Environment variable configuration
- Health check endpoint at `/api/health`
- Logging configured for production monitoring

## Documentation References

Comprehensive requirements and specifications can be found in:
- `docs/epic-goals-product-spec.md` - Detailed feature specifications and user stories
- `docs/epic-goals-srs.md` - Technical requirements and system architecture
- `docs/prompt-plan.md` - Step-by-step implementation guide
- `HELP.md` - Spring Boot generated reference documentation

When implementing features, always refer to these documents to ensure consistency with the overall application design and user experience goals.