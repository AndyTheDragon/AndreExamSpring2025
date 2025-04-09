package dat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LessonLevel
{
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    @JsonCreator
    public static LessonLevel fromString(String key) {
        return key == null ? null : LessonLevel.valueOf(key.toUpperCase());
    }
}
