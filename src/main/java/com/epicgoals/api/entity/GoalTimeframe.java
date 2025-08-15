// ABOUT_ME: Enum defining the five timeframes for goal setting in Epic Goals
// ABOUT_ME: Represents the structured timeframes from long-term vision to immediate action
package com.epicgoals.api.entity;

public enum GoalTimeframe {
    TEN_YEAR("10-Year"),
    FIVE_YEAR("5-Year"), 
    TWELVE_WEEK("12-Week"),
    FOUR_WEEK("4-Week"),
    ONE_WEEK("1-Week");
    
    private final String displayName;
    
    GoalTimeframe(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}