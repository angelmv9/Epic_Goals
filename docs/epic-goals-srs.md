# Epic Goals - Software Requirements Specification
**Version 1.0**  
**Date: January 2025**

## 1. Introduction

### 1.1 Purpose
This document specifies the requirements for Epic Goals, a holistic life tracking application that helps users maintain balance across all aspects of their life through habit tracking, goal setting, and regular reflection cycles.

### 1.2 Scope
Epic Goals is a cross-platform application (Web, iOS, Android, Apple Watch) that enables users to:
- Track daily habits across customizable life categories
- Set and manage multi-timeframe goals (10-year vision to weekly plans)
- Monitor weekly progress through visual dashboards
- Reflect using structured prompts for planning and review
- Share progress with friends through social leaderboards

### 1.3 Document Conventions
- **SHALL/MUST**: Mandatory requirements
- **SHOULD**: Recommended requirements
- **MAY**: Optional requirements

## 2. Overall Description

### 2.1 Product Perspective
Epic Goals is a standalone system consisting of:
- **Web Application** (Single Page Application)
- **Mobile Applications** (iOS/Android with offline-first architecture)
- **Apple Watch Companion App**
- **Backend API Server**
- **PostgreSQL Database**

### 2.2 Product Functions
1. **Habit Management**: Create, track, and manage daily habits with weekly frequency targets
2. **Goal Setting**: Multi-timeframe goal planning with nested breakdown structure
3. **Progress Visualization**: Hybrid rings/radar charts showing category balance
4. **Reflection System**: Structured prompts for planning and review
5. **Social Features**: Friend connections and progress leaderboards
6. **Data Synchronization**: Cross-device sync with offline capability

### 2.3 User Classes
- **Primary Users**: Individuals seeking holistic life improvement through habit tracking
- **Social Users**: Users engaging with friends for accountability and motivation

### 2.4 Operating Environment
- **Web**: Modern browsers supporting ES6+ JavaScript
- **Mobile**: iOS 14+, Android 8+ (API level 26+)
- **Watch**: watchOS 7+
- **Backend**: Java 17+, Spring Boot 3.x
- **Database**: PostgreSQL 14+
- **Hosting**: Railway platform

## 3. System Features

### 3.1 User Authentication and Account Management

#### 3.1.1 Description
Secure user registration, login, and account management system.

#### 3.1.2 Functional Requirements

**AUTH-001**: The system SHALL support email/password authentication
**AUTH-002**: The system SHALL support social login (Google, Apple, Facebook)
**AUTH-003**: The system SHALL provide password reset functionality
**AUTH-004**: The system SHALL allow email address changes
**AUTH-005**: The system SHALL provide account deletion with complete data removal
**AUTH-006**: The system SHALL maintain user sessions across devices
**AUTH-007**: The system SHALL implement JWT token-based authentication

### 3.2 Category Management

#### 3.2.1 Description
Users can create and manage life categories for organizing goals and habits.

#### 3.2.2 Functional Requirements

**CAT-001**: The system SHALL provide 5 default categories: Career, Health, Family, Finances, Wisdom
**CAT-002**: Users SHALL be able to create custom categories (max 10 total)
**CAT-003**: Users SHALL be able to rename categories
**CAT-004**: Users SHALL be able to delete categories
**CAT-005**: When a category is deleted, associated habits SHALL be moved to "Uncategorized"
**CAT-006**: When a category is deleted, associated goals SHALL be moved to "Uncategorized"
**CAT-007**: Category names SHALL be limited to 50 characters
**CAT-008**: Duplicate category names SHALL NOT be allowed per user

### 3.3 Habit Management

#### 3.3.1 Description
Core habit tracking functionality with weekly frequency targets.

#### 3.3.2 Functional Requirements

**HAB-001**: Users SHALL be able to create habits with name, category, and weekly frequency
**HAB-002**: Users SHALL be limited to 15 total habits
**HAB-003**: Habit names SHALL be limited to 100 characters
**HAB-004**: Habit frequency SHALL accept 1-21 times per week
**HAB-005**: Users SHALL be able to mark habits complete/incomplete for any day
**HAB-006**: Users SHALL be able to modify past days' habit completions
**HAB-007**: Users SHALL be able to edit habit details (name, frequency, category)
**HAB-008**: Users SHALL be able to delete habits
**HAB-009**: Duplicate habit names SHALL NOT be allowed per user
**HAB-010**: Each habit SHALL belong to exactly one category

### 3.4 Goal Management

#### 3.4.1 Description
Multi-timeframe goal setting with nested breakdown structure.

#### 3.4.2 Functional Requirements

**GOAL-001**: The system SHALL support 5 timeframes: 10-Year, 5-Year, 12-Week, 4-Week, 1-Week
**GOAL-002**: Users SHALL be able to create goals for any timeframe
**GOAL-003**: Goals SHALL support three types:
- Quantifiable: numeric target with current value
- Level-based: progression through defined levels
- Qualitative: rating scale (1-5 or 1-10)
**GOAL-004**: Goal descriptions SHALL be limited to 500 characters
**GOAL-005**: Users SHALL be able to edit and delete goals
**GOAL-006**: Users MAY have multiple active goals per timeframe
**GOAL-007**: The system SHALL maintain goal hierarchy relationships

### 3.5 Progress Tracking and Scoring

#### 3.5.1 Description
Automated calculation of weekly habit scores and progress visualization.

#### 3.5.2 Functional Requirements

**SCORE-001**: The system SHALL calculate weekly habit scores as (completed habits / total habits) * 100
**SCORE-002**: Weekly scores SHALL display as "X% (Y/Z habits)" format
**SCORE-003**: Historical weekly scores SHALL be preserved when habits are added/removed
**SCORE-004**: Category scores SHALL be user-rated subjective assessments
**SCORE-005**: The system SHALL display progress through hybrid rings/radar visualization
**SCORE-006**: Users SHALL be able to toggle between rings and radar chart views
**SCORE-007**: Users SHALL be able to drill down into individual category details

### 3.6 Overview Tab

#### 3.6.1 Description
Main dashboard showing life balance through visual representations.

#### 3.6.2 Functional Requirements

**OVR-001**: The Overview tab SHALL display weekly score prominently
**OVR-002**: The Overview tab SHALL show category scores via rings or radar chart
**OVR-003**: Users SHALL be able to switch between rings and radar visualizations
**OVR-004**: Users SHALL be able to tap categories to view detailed breakdowns
**OVR-005**: Category detail view SHALL show submetrics via radar chart
**OVR-006**: Users SHALL be able to return to main overview from detail views

### 3.7 Habits Tab

#### 3.7.1 Description
Daily habit tracking interface with weekly context.

#### 3.7.2 Functional Requirements

**HTB-001**: The Habits tab SHALL display current weekly score at top
**HTB-002**: The Habits tab SHALL show day navigator for current week
**HTB-003**: Users SHALL be able to select any day to view/edit habits
**HTB-004**: Current day SHALL be highlighted in day navigator
**HTB-005**: The Habits tab SHALL show today's completion progress
**HTB-006**: Users SHALL be able to check/uncheck habits for selected day
**HTB-007**: Each habit SHALL show weekly progress percentage
**HTB-008**: Each habit SHALL show completion dots for the week
**HTB-009**: Users SHALL be able to add new habits via "Add New Habit" button

### 3.8 Plan Tab

#### 3.8.1 Description
Nested goal planning interface across multiple timeframes.

#### 3.8.2 Functional Requirements

**PLN-001**: The Plan tab SHALL display timeframe breadcrumb navigation
**PLN-002**: Users SHALL be able to navigate between timeframes via breadcrumbs
**PLN-003**: The Plan tab SHALL show goals for current selected timeframe
**PLN-004**: Users SHALL be able to add goals for current timeframe
**PLN-005**: Goals SHALL display progress bars when applicable
**PLN-006**: The Plan tab SHALL show connection to parent timeframe goals
**PLN-007**: Users SHALL be able to zoom in/out between related timeframes

### 3.9 Reflect Tab

#### 3.9.1 Description
Structured reflection prompts organized by purpose.

#### 3.9.2 Functional Requirements

**REF-001**: The Reflect tab SHALL organize prompts into "Planning" and "Review/Reflect" sections
**REF-002**: Each section SHALL be collapsible/expandable
**REF-003**: The Reflect tab SHALL display exact prompts from Planning and Reflection Questions.md
**REF-004**: Prompts SHALL be presented as clickable cards
**REF-005**: No text input or response storage SHALL be implemented in v1

### 3.10 Social Features

#### 3.10.1 Description
Friend connections and progress sharing functionality.

#### 3.10.2 Functional Requirements

**SOC-001**: Users SHALL be able to send friend requests by email/username
**SOC-002**: Users SHALL be able to accept/decline friend requests
**SOC-003**: Users SHALL be able to unfriend others
**SOC-004**: Weekly scores SHALL be shared automatically with friends (if enabled)
**SOC-005**: Users SHALL be able to optionally share category scores
**SOC-006**: Users SHALL be able to select which categories to share
**SOC-007**: Individual habits SHALL NOT be shared
**SOC-008**: The system SHALL display friend leaderboards ranked by weekly score
**SOC-009**: Equal scores SHALL be ranked by habit count (higher count wins)
**SOC-010**: Users SHALL be able to disable all sharing (private mode)
**SOC-011**: Users SHALL be limited to 100 friends maximum

### 3.11 Apple Watch Companion

#### 3.11.1 Description
Simplified watch interface for habit checking and quick progress views.

#### 3.11.2 Functional Requirements

**WATCH-001**: The watch app SHALL allow habit completion checking
**WATCH-002**: The watch app SHALL display current weekly score
**WATCH-003**: The watch app SHALL show weekly score comparison to previous week
**WATCH-004**: The watch app SHALL display category scores in list format
**WATCH-005**: Watch data SHALL sync with phone app
**WATCH-006**: The watch app SHALL work offline and sync when connected

## 4. Data Requirements

### 4.1 Data Model

#### 4.1.1 User Entity
```
User {
  id: UUID (PK)
  email: String (unique, max 255)
  password_hash: String
  created_at: Timestamp
  updated_at: Timestamp
  settings: JSON {
    default_categories: Array<String>
    custom_categories: Array<String>
    sharing_enabled: Boolean
    shared_categories: Array<String>
  }
}
```

#### 4.1.2 Category Entity
```
Category {
  id: UUID (PK)
  user_id: UUID (FK)
  name: String (max 50)
  is_default: Boolean
  created_at: Timestamp
  updated_at: Timestamp
}
```

#### 4.1.3 Habit Entity
```
Habit {
  id: UUID (PK)
  user_id: UUID (FK)
  category_id: UUID (FK)
  name: String (max 100)
  frequency: Integer (1-21)
  created_at: Timestamp
  updated_at: Timestamp
  is_active: Boolean
}
```

#### 4.1.4 Habit Completion Entity
```
HabitCompletion {
  id: UUID (PK)
  habit_id: UUID (FK)
  date: Date
  completed: Boolean
  created_at: Timestamp
  updated_at: Timestamp
}
```

#### 4.1.5 Goal Entity
```
Goal {
  id: UUID (PK)
  user_id: UUID (FK)
  category_id: UUID (FK)
  timeframe: ENUM (10_YEAR, 5_YEAR, 12_WEEK, 4_WEEK, 1_WEEK)
  type: ENUM (QUANTIFIABLE, LEVEL_BASED, QUALITATIVE)
  name: String (max 100)
  description: String (max 500)
  target_value: String (JSON for different goal types)
  current_value: String (JSON for different goal types)
  created_at: Timestamp
  updated_at: Timestamp
  is_active: Boolean
}
```

#### 4.1.6 Review Entity
```
Review {
  id: UUID (PK)
  user_id: UUID (FK)
  type: ENUM (12_WEEK, ANNUAL)
  date: Date
  category_ratings: JSON
  next_review_date: Date
  created_at: Timestamp
}
```

#### 4.1.7 Friendship Entity
```
Friendship {
  id: UUID (PK)
  requester_id: UUID (FK)
  addressee_id: UUID (FK)
  status: ENUM (PENDING, ACCEPTED, DECLINED)
  created_at: Timestamp
  updated_at: Timestamp
}
```

### 4.2 Data Constraints

**DATA-001**: All user data SHALL be stored indefinitely
**DATA-002**: Mobile apps SHALL cache 5 years of historical data locally
**DATA-003**: Data synchronization SHALL occur in background
**DATA-004**: Offline changes SHALL be queued and synced when online
**DATA-005**: Conflict resolution SHALL use "last write wins" strategy
**DATA-006**: All timestamps SHALL be stored in UTC
**DATA-007**: Soft deletes SHALL be used for user-created content

## 5. External Interface Requirements

### 5.1 User Interfaces

#### 5.1.1 Web Application
**UI-001**: The web app SHALL be a Single Page Application (SPA)
**UI-002**: The web app SHALL be responsive for desktop and tablet sizes
**UI-003**: The web app SHALL support modern browsers (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+)

#### 5.1.2 Mobile Applications
**UI-004**: Mobile apps SHALL follow platform design guidelines (iOS Human Interface, Material Design)
**UI-005**: Mobile apps SHALL support both portrait and landscape orientations
**UI-006**: Mobile apps SHALL be optimized for phones and tablets

#### 5.1.3 Apple Watch Application
**UI-007**: Watch app SHALL follow watchOS design guidelines
**UI-008**: Watch interface SHALL be optimized for quick interactions (<30 seconds)
**UI-009**: Watch app SHALL support both 40mm and 44mm+ screen sizes

### 5.2 API Interfaces

#### 5.2.1 Authentication Endpoints
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/social-login
```

#### 5.2.2 User Management Endpoints
```
GET /api/user/profile
PUT /api/user/profile
DELETE /api/user/account
PUT /api/user/settings
GET /api/user/export
```

#### 5.2.3 Category Endpoints
```
GET /api/categories
POST /api/categories
PUT /api/categories/{id}
DELETE /api/categories/{id}
```

#### 5.2.4 Habit Endpoints
```
GET /api/habits
POST /api/habits
PUT /api/habits/{id}
DELETE /api/habits/{id}
GET /api/habits/{id}/completions
POST /api/habits/{id}/completions
PUT /api/habits/{id}/completions/{date}
```

#### 5.2.5 Goal Endpoints
```
GET /api/goals
POST /api/goals
PUT /api/goals/{id}
DELETE /api/goals/{id}
GET /api/goals/timeframe/{timeframe}
```

#### 5.2.6 Progress Endpoints
```
GET /api/progress/weekly-scores
GET /api/progress/category-scores
GET /api/progress/trends
```

#### 5.2.7 Social Endpoints
```
GET /api/friends
POST /api/friends/request
PUT /api/friends/{id}/accept
PUT /api/friends/{id}/decline
DELETE /api/friends/{id}
GET /api/social/leaderboard
```

#### 5.2.8 Reflection Endpoints
```
GET /api/reflections/prompts
```

### 5.3 Third-Party Integrations

**INT-001**: The system SHALL integrate with Google OAuth 2.0
**INT-002**: The system SHALL integrate with Apple Sign-In
**INT-003**: The system SHALL integrate with Facebook Login
**INT-004**: The system MAY integrate with email service providers for notifications

## 6. System Constraints

### 6.1 Performance Requirements

**PERF-001**: Web app initial load SHALL complete within 3 seconds
**PERF-002**: Mobile app launch SHALL complete within 2 seconds
**PERF-003**: Habit check-off action SHALL respond within 100ms (local)
**PERF-004**: API responses SHALL complete within 500ms (95th percentile)
**PERF-005**: Database queries SHALL be optimized with appropriate indexes
**PERF-006**: The system SHALL support 10,000 concurrent users

### 6.2 Security Requirements

**SEC-001**: All API communications SHALL use HTTPS/TLS 1.3
**SEC-002**: Passwords SHALL be hashed using bcrypt with minimum 12 rounds
**SEC-003**: JWT tokens SHALL expire after 24 hours
**SEC-004**: Refresh tokens SHALL expire after 30 days
**SEC-005**: The system SHALL implement rate limiting on all endpoints
**SEC-006**: User input SHALL be validated and sanitized
**SEC-007**: Database queries SHALL use parameterized statements
**SEC-008**: Personal data SHALL be encrypted at rest

### 6.3 Availability Requirements

**AVAIL-001**: The system SHALL maintain 99.5% uptime
**AVAIL-002**: Planned maintenance SHALL not exceed 4 hours per month
**AVAIL-003**: The system SHALL implement graceful degradation during outages
**AVAIL-004**: Database backups SHALL be performed daily
**AVAIL-005**: Critical data loss SHALL not exceed 1 hour of transactions

## 7. System Architecture

### 7.1 Technology Stack

#### 7.1.1 Backend
- **Language**: Java 24+
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 14+
- **ORM**: Spring Data JPA
- **Authentication**: Spring Security + JWT
- **Testing**: JUnit 5, Mockito, TestContainers

#### 7.1.2 Frontend Web
- **Framework**: Angular 17+
- **Language**: TypeScript
- **Styling**: Angular Material + Custom CSS
- **State Management**: NgRx
- **Charts**: Chart.js or D3.js
- **Testing**: Jasmine, Karma, Cypress

#### 7.1.3 Mobile Applications
- **iOS**: Swift, UIKit/SwiftUI
- **Android**: Kotlin, Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Local Storage**: SQLite with Room (Android) / Core Data (iOS)
- **Networking**: Retrofit (Android) / URLSession (iOS)

#### 7.1.4 Apple Watch
- **Language**: Swift
- **Framework**: WatchKit
- **Connectivity**: Watch Connectivity Framework

#### 7.1.5 Infrastructure
- **Hosting**: Railway
- **Database**: PostgreSQL on Railway
- **File Storage**: Railway persistent volumes
- **Monitoring**: Railway metrics + custom logging

### 7.2 Deployment Architecture

```
[User Devices] 
    ↓ HTTPS
[Load Balancer] 
    ↓
[Spring Boot API Server] 
    ↓ 
[PostgreSQL Database]

[CDN] → [Static Web Assets (Angular)]
```

### 7.3 Data Flow

1. **User Action** (habit check-off, goal creation)
2. **Local Storage** (mobile) or **Direct API Call** (web)
3. **API Validation** and **Business Logic**
4. **Database Transaction**
5. **Response to Client**
6. **Background Sync** (mobile) or **Real-time Update** (web)

## 8. Error Handling

### 8.1 Client-Side Error Handling

**ERR-001**: Network errors SHALL display user-friendly messages
**ERR-002**: Validation errors SHALL highlight specific fields with clear messages
**ERR-003**: Offline state SHALL be clearly indicated to users
**ERR-004**: Failed sync operations SHALL be queued for retry
**ERR-005**: Unexpected errors SHALL be logged and reported to developers

### 8.2 Server-Side Error Handling

**ERR-006**: All API endpoints SHALL return consistent error response format:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid habit frequency",
    "details": {
      "field": "frequency",
      "constraint": "must be between 1 and 21"
    },
    "timestamp": "2025-01-15T10:30:00Z"
  }
}
```

**ERR-007**: Database errors SHALL be caught and logged with correlation IDs
**ERR-008**: Rate limit violations SHALL return HTTP 429 with retry-after header
**ERR-009**: Authentication errors SHALL return HTTP 401 with clear error codes
**ERR-010**: Authorization errors SHALL return HTTP 403 with minimal information

### 8.3 Data Integrity Error Handling

**ERR-011**: Orphaned data from deleted entities SHALL be handled gracefully
**ERR-012**: Sync conflicts SHALL be resolved using last-write-wins strategy
**ERR-013**: Invalid timestamps SHALL be rejected with clear error messages
**ERR-014**: Duplicate creation attempts SHALL return existing entity

## 9. Testing Requirements

### 9.1 Unit Testing

**TEST-001**: Backend business logic SHALL have minimum 80% code coverage
**TEST-002**: Frontend components SHALL have minimum 70% code coverage
**TEST-003**: All utility functions SHALL have 100% code coverage
**TEST-004**: Mock external dependencies in unit tests

### 9.2 Integration Testing

**TEST-005**: All API endpoints SHALL have integration tests
**TEST-006**: Database operations SHALL be tested with TestContainers
**TEST-007**: Authentication flows SHALL be tested end-to-end
**TEST-008**: Social features SHALL be tested with multiple user scenarios

### 9.3 End-to-End Testing

**TEST-009**: Critical user journeys SHALL be covered by E2E tests:
- User registration and onboarding
- Habit creation and tracking
- Goal setting and progress
- Social features and sharing
- Data sync across devices

**TEST-010**: E2E tests SHALL run against production-like environment
**TEST-011**: Performance tests SHALL validate response time requirements
**TEST-012**: Load tests SHALL verify concurrent user capacity

### 9.4 Mobile Testing

**TEST-013**: Mobile apps SHALL be tested on minimum iOS 14 and Android 8
**TEST-014**: Offline functionality SHALL be thoroughly tested
**TEST-015**: Cross-device sync SHALL be validated with test scenarios
**TEST-016**: Apple Watch app SHALL be tested with phone app integration

### 9.5 Manual Testing

**TEST-017**: UI/UX SHALL be manually tested on target devices
**TEST-018**: Accessibility SHALL be tested with screen readers
**TEST-019**: Edge cases SHALL be manually verified
**TEST-020**: Beta testing SHALL be conducted with 10-20 real users

## 10. Data Migration and Backup

### 10.1 Backup Strategy

**BACKUP-001**: Database SHALL be backed up daily with 30-day retention
**BACKUP-002**: Backups SHALL be tested monthly for restoration
**BACKUP-003**: User data export SHALL be available in CSV format
**BACKUP-004**: System configuration SHALL be version controlled

### 10.2 Data Migration

**MIGRATION-001**: Database schema changes SHALL use versioned migrations
**MIGRATION-002**: Migrations SHALL be backward compatible when possible
**MIGRATION-003**: Migration rollback procedures SHALL be documented
**MIGRATION-004**: Production migrations SHALL be tested in staging environment

## 11. Documentation Requirements

**DOC-001**: API documentation SHALL be maintained with OpenAPI 3.0 specification
**DOC-002**: Database schema SHALL be documented with entity relationships
**DOC-003**: Deployment procedures SHALL be documented step-by-step
**DOC-004**: User guides SHALL be created for all major features
**DOC-005**: Developer onboarding documentation SHALL be maintained

## 12. Acceptance Criteria

### 12.1 MVP Acceptance Criteria

The Epic Goals application SHALL be considered ready for initial release when:

1. All functional requirements marked as "SHALL" are implemented and tested
2. All four main tabs (Overview, Habits, Plan, Reflect) are fully functional
3. User authentication and account management work correctly
4. Habit tracking with weekly scoring is accurate
5. Goal management across timeframes is functional
6. Social features allow friend connections and leaderboards
7. Mobile apps sync properly with web application
8. Apple Watch companion app provides basic functionality
9. All critical user journeys pass E2E testing
10. Performance requirements are met under load testing
11. Security requirements are verified through penetration testing
12. Beta user feedback has been incorporated

---

*This specification serves as the comprehensive technical requirements for Epic Goals application development. All requirements should be implemented as specified to ensure consistent user experience across platforms.*