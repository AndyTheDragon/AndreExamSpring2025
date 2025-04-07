package dat.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dat.dto.TripDTO;
import dat.dto.TripInputDTO;
import dat.enums.TripCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trip
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String name;
    @Setter
    private Double price;
    @Setter
    private TripCategory category;
    @Setter
    private LocalTime startTime;
    @Setter
    private LocalTime endTime;
    @Embedded
    @ToString.Exclude
    private Position startPosition;
    @Setter
    @ManyToOne
    @JoinColumn(name = "guide_id")
    @JsonManagedReference
    private Guide guide;

    public Trip(String name, Double price, TripCategory category, LocalTime startTime, LocalTime endTime, Position startPosition)
    {
        this.name = name;
        this.price = price;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPosition = startPosition;
    }

    public Trip(TripInputDTO trip)
    {
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        this.startTime = LocalTime.parse(trip.getStartTime());
        this.endTime = LocalTime.parse(trip.getEndTime());
        this.startPosition = new Position(trip.getStartPosition().getDescription(), trip.getStartPosition().getLatitude(), trip.getStartPosition().getLongitude());
    }


    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class Position {
        private String description;
        private double latitude;
        private double longitude;
    }
}
