package dat.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dat.dto.TripInputDTO;
import dat.enums.LessonLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SkiLesson
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String name;
    @Setter
    private Double price;
    @Setter
    private LessonLevel level;
    @Setter
    private LocalTime startTime;
    @Setter
    private LocalTime endTime;
    @Embedded
    @ToString.Exclude
    private Position location;
    @Setter
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonManagedReference
    private Instructor instructor;

    public SkiLesson(String name, Double price, LessonLevel level, LocalTime startTime, LocalTime endTime, Position location)
    {
        this.name = name;
        this.price = price;
        this.level = level;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public SkiLesson(TripInputDTO trip)
    {
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.level = trip.getCategory();
        this.startTime = LocalTime.parse(trip.getStartTime());
        this.endTime = LocalTime.parse(trip.getEndTime());
        this.location = new Position(trip.getStartPosition().getDescription(), trip.getStartPosition().getLatitude(), trip.getStartPosition().getLongitude());
    }


    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Position {
        private String description;
        private double latitude;
        private double longitude;
    }
}
