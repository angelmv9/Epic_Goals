# Epic Goals Implementation Prompt Plan

## Overview
This document contains the complete set of implementation prompts for building the Epic Goals application. Each prompt builds on the previous work, ensuring no orphaned code and full integration at each step.

## Backend Implementation Prompts

### Prompt 1: Spring Boot Foundation ✅ COMPLETED

```text
Create a Spring Boot 3.x application for the Epic Goals habit tracking system. 

Requirements:
1. Use Java 17+ with Spring Boot 3.x
2. Configure PostgreSQL database connection
3. Include these dependencies: Spring Web, Spring Data JPA, Spring Security, JWT (jjwt-api), Lombok, Validation
4. Create application.yml with:
   - Database configuration (use environment variables for credentials)
   - JWT secret configuration
   - Server port 8080
   - JPA/Hibernate settings with create-drop for development
5. Create a HealthController with GET /api/health endpoint returning {"status": "UP", "timestamp": current_time}
6. Configure CORS for localhost:4200 (Angular dev server)
7. Create GlobalExceptionHandler for consistent error responses following the format:
   {"error": {"code": "ERROR_CODE", "message": "error message", "timestamp": "ISO-8601"}}
8. Write SpringBootTest to verify application starts successfully
9. Write integration test for health endpoint

Project structure:
- com.epicgoals.api
  - controller/
  - service/
  - repository/
  - entity/
  - dto/
  - config/
  - exception/
  - security/

Ensure all code is production-ready with proper error handling and logging.
```

### Prompt 2: User Authentication System ✅ COMPLETED

```text
Building on the Spring Boot foundation, implement a complete user authentication system.

Requirements:
1. Create User entity with:
   - UUID id (auto-generated)
   - String email (unique, validated)
   - String passwordHash
   - Timestamp createdAt, updatedAt
   - JSON settings field (for future preferences)

2. Create authentication DTOs:
   - RegisterRequest (email, password, confirmPassword)
   - LoginRequest (email, password)
   - AuthResponse (user info, accessToken, refreshToken, expiresIn)

3. Implement AuthController with:
   - POST /api/auth/register
   - POST /api/auth/login
   - POST /api/auth/refresh
   - POST /api/auth/logout

4. Create JwtService for token operations:
   - Generate access tokens (24h expiry)
   - Generate refresh tokens (30d expiry)
   - Validate and parse tokens
   - Extract user claims

5. Implement Spring Security configuration:
   - JWT authentication filter
   - Stateless session management
   - BCrypt password encoder (12 rounds)
   - Permit auth endpoints, secure others

6. Add validation:
   - Email format validation
   - Password strength (min 8 chars, 1 upper, 1 lower, 1 number)
   - Duplicate email check

7. Write comprehensive tests:
   - Unit tests for JwtService
   - Integration tests for all auth endpoints
   - Test invalid credentials, expired tokens, refresh flow

Ensure this integrates with the existing GlobalExceptionHandler and follows the established error format.
```

### Prompt 3: Category and Habit Management ✅ COMPLETED

```text
Extend the application with category and habit management, building on the authenticated user system.

Requirements:

1. Create Category entity:
   - UUID id
   - User user (ManyToOne)
   - String name (max 50 chars)
   - Boolean isDefault
   - Timestamps

2. Create Habit entity:
   - UUID id  
   - User user (ManyToOne)
   - Category category (ManyToOne)
   - String name (max 100 chars)
   - Integer frequency (1-21)
   - Boolean isActive
   - Timestamps

3. Create HabitCompletion entity:
   - UUID id
   - Habit habit (ManyToOne)
   - LocalDate date
   - Boolean completed
   - Timestamps

4. Implement CategoryController:
   - GET /api/categories (user's categories)
   - POST /api/categories (create custom)
   - PUT /api/categories/{id}
   - DELETE /api/categories/{id} (move habits to uncategorized)

5. Implement HabitController:
   - GET /api/habits (user's habits)
   - POST /api/habits (enforce 15 habit limit)
   - PUT /api/habits/{id}
   - DELETE /api/habits/{id}
   - GET /api/habits/{id}/completions?startDate=X&endDate=Y
   - POST /api/habits/{id}/completions (toggle completion)

6. Create services with business logic:
   - CategoryService: Handle default categories creation on user registration
   - HabitService: Validate limits, handle completions
   - Add method to AuthService to create default categories on registration

7. Security considerations:
   - Ensure users can only access their own data
   - Add @PreAuthorize annotations where needed
   - Validate ownership in service layer

8. Write tests:
   - Test 15 habit limit enforcement
   - Test category deletion with habit reassignment
   - Test completion tracking across date ranges
   - Test security boundaries between users

Integrate with existing auth system - extract user from JWT in controllers.
```

### Prompt 4: Weekly Score Calculation System ✅ COMPLETED

```text
Implement the weekly score calculation system that tracks habit completion rates.

Requirements:

1. Create WeeklyScore entity:
   - UUID id
   - User user (ManyToOne)
   - LocalDate weekStartDate (Monday)
   - Integer score (0-100)
   - Integer completedHabits
   - Integer totalHabits
   - Timestamp calculatedAt

2. Create ScoreService with:
   - Calculate current week score for user
   - Calculate score for specific week
   - Get historical scores (last 12 weeks)
   - Recalculate when habits added/removed
   - Cache current week score (invalidate on changes)

3. Scoring algorithm:
   - For each habit: expected = frequency, actual = completions in week
   - Habit score = (actual / expected) * 100
   - Weekly score = sum of all habit scores / number of habits
   - Store snapshot to preserve history

4. Create ProgressController:
   - GET /api/progress/weekly-scores?weeks=12
   - GET /api/progress/current-week
   - GET /api/progress/category-scores (user-rated, stored separately)

5. Add automatic triggers:
   - Recalculate on habit completion toggle
   - Recalculate on habit creation/deletion
   - Preserve historical scores when habits change

6. Performance optimizations:
   - Batch load completions for week
   - Use @Transactional properly
   - Add database indexes for date queries

7. Write tests:
   - Test calculation accuracy
   - Test historical preservation
   - Test edge cases (no habits, all completed, none completed)
   - Test performance with many habits

Ensure this integrates with existing habit completion system and updates in real-time.
```

### Prompt 5: Goal Management System ✅ COMPLETED

```text
Implement the multi-timeframe goal management system with three goal types.

Requirements:

1. Create Goal entity:
   - UUID id
   - User user (ManyToOne)
   - Category category (ManyToOne, nullable)
   - GoalTimeframe timeframe (enum: TEN_YEAR, FIVE_YEAR, TWELVE_WEEK, FOUR_WEEK, ONE_WEEK)
   - GoalType type (enum: QUANTIFIABLE, LEVEL_BASED, QUALITATIVE)
   - String name (max 100)
   - String description (max 500)
   - JSON targetValue (store different structures per type)
   - JSON currentValue
   - UUID parentGoalId (for hierarchy)
   - Boolean isActive
   - Timestamps

2. Goal type structures in JSON:
   Quantifiable: {"value": 10000, "unit": "dollars"}
   Level-based: {"level": "Purple Belt", "allLevels": ["White", "Blue", "Purple"]}
   Qualitative: {"rating": 5, "scale": 10}

3. Create GoalController:
   - GET /api/goals (all user goals)
   - GET /api/goals/timeframe/{timeframe}
   - POST /api/goals
   - PUT /api/goals/{id} (update progress)
   - DELETE /api/goals/{id}
   - GET /api/goals/{id}/children (nested goals)

4. Create GoalService:
   - Validate goal type data structures
   - Calculate progress percentages
   - Manage goal hierarchies
   - Connect shorter timeframes to longer ones

5. Add DTOs:
   - GoalCreateRequest (validate based on type)
   - GoalUpdateRequest  
   - GoalResponse (include calculated progress)

6. Business rules:
   - Goals can exist without categories
   - Multiple goals per timeframe allowed
   - Child goals should align with parent timeframe

7. Write tests:
   - Test each goal type creation/update
   - Test hierarchy relationships
   - Test progress calculations
   - Test JSON serialization/deserialization

Integrate with existing category system and ensure user data isolation.
```

## Frontend Implementation Prompts

### Prompt 6: Angular Frontend Foundation

```text
Create the Angular frontend application for Epic Goals with authentication and navigation structure.

Requirements:

1. Initialize Angular 20+ project with:
   - Standalone components
   - Angular Material
   - Routing
   - HttpClient
   - Reactive Forms

2. Create core module structure:
   src/app/
   - core/
     - services/ (auth, api)
     - guards/ (auth)
     - interceptors/ (jwt, error)
   - shared/
     - components/ (loading, error)
   - features/
     - auth/ (login, register)
     - dashboard/ (main layout)
     - habits/
     - overview/
     - plan/
     - reflect/

3. Implement AuthService:
   - Login/register methods
   - Token storage (localStorage)
   - Current user observable
   - Logout with cleanup
   - Auto-refresh tokens

4. Create authentication components:
   - Login with email/password
   - Register with validation
   - Password strength indicator
   - Form error handling
   - Loading states

5. Implement main dashboard:
   - Tab navigation (Overview, Habits, Plan, Reflect)
   - Responsive layout
   - Week score display in header
   - Route guards for auth
   - Lazy load feature modules

6. Add HTTP interceptor:
   - Attach JWT to requests
   - Handle 401 responses
   - Refresh token logic
   - Global error handling

7. Create shared components:
   - Loading spinner
   - Error message display
   - Confirmation dialog
   - Empty state

8. Write tests:
   - Auth service unit tests
   - Component tests with TestBed
   - Guard tests
   - Interceptor tests

Style with Angular Material theme matching the Epic Goals brand. Ensure responsive design.
```

### Prompt 7: Habits Tab Implementation

```text
Implement the Habits tab interface with daily tracking and weekly view, integrating with the backend API.

Requirements:

1. Create habit models:
   - Habit interface matching backend
   - HabitCompletion interface
   - WeeklyProgress interface

2. Implement HabitService:
   - CRUD operations for habits
   - Load habits with completions
   - Toggle completion for date
   - Calculate weekly progress
   - Real-time updates via RxJS

3. Create Habits tab components:
   - HabitListComponent (main container)
   - WeekNavigatorComponent (Mon-Sun selector)
   - HabitItemComponent (individual habit row)
   - AddHabitDialogComponent
   - WeeklyScoreComponent

4. Week navigation features:
   - Display current week (Mon-Sun)
   - Highlight today
   - Navigate previous/next weeks
   - Show date for selected day

5. Habit item display:
   - Checkbox for completion
   - Habit name and category
   - Weekly progress (3/5 times)
   - Progress dots for week
   - Visual feedback on toggle

6. Add habit dialog:
   - Name input (max 100 chars)
   - Category selector
   - Frequency picker (1-21)
   - Validation messages
   - Loading state on save

7. Real-time updates:
   - Update weekly score on completion
   - Animate progress changes
   - Optimistic updates with rollback
   - Handle offline scenarios

8. Write tests:
   - Service method tests
   - Component interaction tests
   - Week navigation tests
   - API error handling tests

Ensure smooth animations and immediate feedback. Handle the 15 habit limit gracefully.
```

### Prompt 8: Overview Tab with Visualizations

```text
Implement the Overview tab with radar/rings visualizations showing life balance across categories.

Requirements:

1. Install and configure Chart.js:
   - Add chart.js and ng2-charts
   - Create chart service
   - Configure responsive options

2. Create visualization components:
   - RadarChartComponent
   - RingsChartComponent  
   - CategoryDetailComponent
   - VisualizationToggleComponent

3. Implement CategoryScoreService:
   - Fetch category scores
   - Calculate from habit data
   - Support manual ratings
   - Cache and update logic

4. Radar chart features:
   - Display all categories
   - Show scores 0-100
   - Highlight low scores (<80%)
   - Smooth animations
   - Click to drill down

5. Rings visualization:
   - Apple Fitness style rings
   - One ring per category
   - Color coding by score
   - Animation on load
   - Hover interactions

6. Category drill-down:
   - Show detailed radar for subcategories
   - Display related habits
   - Show trend over time
   - Back navigation

7. Toggle between views:
   - Smooth transition
   - Preserve selection
   - Remember preference
   - Responsive sizing

8. Visual indicators:
   - Warning for low scores
   - Comparison to last week
   - Achievement celebrations
   - Loading states

9. Write tests:
   - Chart rendering tests
   - Data transformation tests
   - Interaction tests
   - Responsive behavior tests

Ensure charts are performant and accessible. Add legends and labels for clarity.
```

### Prompt 9: Plan Tab with Goal Management

```text
Implement the Plan tab for multi-timeframe goal setting and tracking.

Requirements:

1. Create goal models and services:
   - Goal interfaces for three types
   - GoalService for CRUD operations
   - GoalHierarchyService for relationships

2. Plan tab components:
   - TimeframeBreadcrumbComponent
   - GoalListComponent
   - GoalCardComponent
   - AddGoalDialogComponent
   - GoalTypeSelector

3. Timeframe navigation:
   - Breadcrumb: 10yr > 5yr > 12wk > 4wk > 1wk
   - Click to navigate between levels
   - Show current timeframe clearly
   - Zoom in/out animations

4. Goal display features:
   - Cards for each goal
   - Progress visualization per type
   - Color coding by category
   - Edit/delete actions
   - Link to parent goal

5. Add goal dialog:
   - Timeframe selector
   - Goal type chooser with examples
   - Dynamic form based on type
   - Category assignment (optional)
   - Parent goal selector

6. Goal type forms:
   Quantifiable:
   - Current value input
   - Target value input
   - Unit selector/input
   
   Level-based:
   - Current level dropdown
   - Target level dropdown
   - Custom levels option
   
   Qualitative:
   - Current rating slider
   - Target rating
   - Scale selector (5 or 10)

7. Progress updates:
   - Quick update from card
   - Full edit in dialog
   - Visual progress bars
   - Percentage calculations

8. Write tests:
   - Goal creation for each type
   - Hierarchy navigation
   - Progress calculations
   - Form validation

Ensure smooth navigation between timeframes and clear visual hierarchy.
```

### Prompt 10: Social Features and Leaderboard

```text
Implement social features including friend connections and weekly leaderboards.

Requirements:

1. Create social models:
   - Friend interface
   - FriendRequest interface
   - LeaderboardEntry interface
   - PrivacySettings interface

2. Implement SocialService:
   - Send friend requests
   - Accept/decline requests
   - Unfriend users
   - Fetch leaderboard
   - Update privacy settings

3. Social components:
   - FriendsListComponent
   - LeaderboardComponent
   - FriendRequestsComponent
   - PrivacySettingsComponent
   - AddFriendDialogComponent

4. Friend management:
   - Search by email/username
   - Send request with message
   - Show pending requests
   - Accept/decline flow
   - Unfriend confirmation

5. Leaderboard display:
   - Weekly score ranking
   - Show: Name, Score%, Habits count
   - Optional category scores
   - Your position highlighted
   - Tie-breaking by habit count

6. Privacy controls:
   - Global sharing toggle
   - Per-category sharing
   - Hide specific metrics
   - Private mode option
   - Save preferences

7. Visual features:
   - Rank badges (1st, 2nd, 3rd)
   - Trend arrows
   - Profile pictures/avatars
   - Celebration animations
   - Refresh pull-down

8. Write tests:
   - Friend request flow
   - Privacy setting changes
   - Leaderboard sorting
   - Edge cases (no friends)

Implement with user privacy as top priority. Make sharing opt-in and granular.
```

### Prompt 11: Reflect Tab and Review System

```text
Implement the Reflect tab with structured reflection prompts and review cycles.

Requirements:

1. Create reflection models:
   - ReflectionPrompt interface
   - PromptCategory (Planning/Review)
   - ReviewReminder interface

2. Reflection data structure:
   - Load prompts from static data
   - Organize by category
   - No text input in v1
   - Future: response storage

3. Reflect tab components:
   - ReflectMainComponent
   - PromptSectionComponent
   - PromptCardComponent
   - CollapsibleSection

4. Planning prompts section:
   - "If this were easy, what would it look like?"
   - "What's the ONE thing..."
   - Additional prompts from spec
   - Card-based layout
   - Expandable for reading

5. Review prompts section:
   - "What did I accomplish?"
   - "What challenges did I face?"
   - Additional prompts from spec
   - Organized by purpose
   - Visual separation

6. UI features:
   - Collapsible sections
   - Clean typography
   - Reading mode
   - Copy prompt option
   - Mark as reviewed (local)

7. Review cycle integration:
   - 12-week reminder system
   - Annual review prompts
   - Quick access during reviews
   - Category rating flow

8. Write tests:
   - Prompt display tests
   - Section collapse/expand
   - Navigation tests
   - Responsive layout tests

Focus on readability and contemplative design. These are thinking tools, not forms.
```

## Mobile Implementation Prompts

### Prompt 12: Mobile App Foundation

```text
Create a React Native mobile app for Epic Goals with offline-first architecture.

Requirements:

1. Initialize React Native project:
   - React Navigation
   - AsyncStorage
   - React Query for caching
   - Native Base or RN Elements UI

2. Project structure:
   src/
   - screens/
   - components/
   - services/
   - store/
   - utils/
   - navigation/

3. Implement offline storage:
   - Store habits locally
   - Queue completions offline
   - Sync when online
   - Conflict resolution

4. Authentication flow:
   - Secure token storage
   - Biometric login option
   - Remember me option
   - Auto-refresh tokens

5. Bottom tab navigation:
   - Overview
   - Habits
   - Plan
   - Reflect
   - Profile/Settings

6. Habits screen:
   - Pull to refresh
   - Swipe to complete
   - Haptic feedback
   - Weekly view
   - Quick add button

7. Sync service:
   - Background sync
   - Queue failed requests
   - Retry with backoff
   - Show sync status

8. Platform-specific:
   - iOS safe areas
   - Android back button
   - Platform UI patterns
   - Native animations

9. Write tests:
   - Offline scenarios
   - Sync queue tests
   - Navigation tests
   - Platform tests

Ensure smooth performance and native feel. Handle offline gracefully.
```

### Prompt 13: Apple Watch Companion

```text
Create an Apple Watch companion app for quick habit tracking.

Requirements:

1. WatchOS project setup:
   - SwiftUI interface
   - Watch Connectivity
   - Complications
   - Background refresh

2. Main features:
   - Habit list view
   - Check off habits
   - Weekly score display
   - Category scores

3. Data sync:
   - Sync with iPhone app
   - Store subset locally
   - Handle offline mode
   - Efficient updates

4. Complication:
   - Show weekly score
   - Quick launch
   - Update regularly
   - Multiple styles

5. Interface design:
   - Large touch targets
   - Clear typography
   - Smooth scrolling
   - Loading states

6. Performance:
   - Minimize battery drain
   - Optimize data transfer
   - Cache appropriately
   - Fast launch time

7. Write tests:
   - Connectivity tests
   - UI tests
   - Performance tests

Focus on quick interactions and glanceable information.
```

## Final Integration Prompt

### Prompt 14: Integration and Polish

```text
Complete the integration of all components and add final polish.

Requirements:

1. End-to-end testing:
   - Full user journey tests
   - Cross-platform sync tests
   - Performance benchmarks
   - Load testing

2. Error handling:
   - Graceful degradation
   - User-friendly messages
   - Retry mechanisms
   - Fallback states

3. Analytics integration:
   - User events tracking
   - Performance monitoring
   - Crash reporting
   - Usage patterns

4. Accessibility:
   - Screen reader support
   - Keyboard navigation
   - Color contrast
   - Focus management

5. Performance optimization:
   - Code splitting
   - Lazy loading
   - Image optimization
   - Database indexes

6. Security hardening:
   - Input sanitization
   - Rate limiting
   - HTTPS enforcement
   - Security headers

7. Documentation:
   - API documentation
   - Deployment guide
   - User manual
   - Developer guide

8. Final polish:
   - Animations
   - Transitions
   - Empty states
   - Success feedback

Ensure everything works together seamlessly across all platforms.
```

## Implementation Notes

### Order of Execution
1. **Backend API** (Prompts 1-5): Establish data model and API foundation
2. **Web Application** (Prompts 6-11): Build and test core features
3. **Mobile App** (Prompt 12): Extend to mobile once web is stable
4. **Apple Watch** (Prompt 13): Add companion app
5. **Polish & Integration** (Prompt 14): Final refinements

### Key Principles
- Each prompt builds on previous work - no orphaned code
- Test-driven development throughout
- User data isolation and security at every step
- Performance optimization from the start
- Offline-first architecture for mobile
- Privacy-first approach for social features

### Success Criteria
- All tests passing
- Performance benchmarks met
- Security audit passed
- User acceptance testing completed
- Documentation complete