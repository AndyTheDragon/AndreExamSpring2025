package dat.dto;


import dat.entities.SkiLesson;
import dat.enums.LessonLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SkiLessonDTO
{
    private Integer id;
    private String name;
    private Double price;
    private LessonLevel level;
    private String startTime;
    private String endTime;
    private PositionDTO location;
    private InstructorDTO instructor;

    public SkiLessonDTO(SkiLesson entity)
    {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.level = entity.getLevel();
        this.startTime = entity.getStartTime().toString();
        this.endTime = entity.getEndTime().toString();
        this.location = entity.getLocation()==null ? null : new PositionDTO(entity.getLocation().getDescription(),
                                                                            entity.getLocation().getLatitude(),
                                                                            entity.getLocation().getLongitude());
        this.instructor = entity.getInstructor()==null ? null : new InstructorDTO(entity.getInstructor());
    }

    public SkiLesson toEntity()
    {
        SkiLesson entity = new SkiLesson(
                this.id,
                this.name,
                this.price,
                this.level,
                LocalTime.parse(this.startTime),
                LocalTime.parse(this.endTime),
                this.location==null ? null : new SkiLesson.Position(this.location.getDescription(),
                                                                    this.location.getLatitude(),
                                                                    this.location.getLongitude()),
                null // instructor will be set later
        );
        return entity;
    }

}
