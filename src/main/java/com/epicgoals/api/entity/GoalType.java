// ABOUT_ME: Enum defining the three types of goals supported in Epic Goals
// ABOUT_ME: Each type has different data structures and progress calculation methods
package com.epicgoals.api.entity;

public enum GoalType {
    QUANTIFIABLE("Quantifiable"),
    LEVEL_BASED("Level-based"),
    QUALITATIVE("Qualitative");
    
    private final String displayName;
    
    GoalType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}