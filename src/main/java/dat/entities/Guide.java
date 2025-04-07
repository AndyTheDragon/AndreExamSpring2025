package dat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dat.dto.GuideDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Guide
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer yearsOfExperience;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "guide")
    @JsonBackReference
    @ToString.Exclude
    private Set<Trip> trips = new HashSet<>();

    public Guide(GuideDTO guide)
    {
        this.firstName = guide.getFirstName();
        this.lastName = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }


    public void addTrip(Trip trip)
    {
        if (trip != null)
        {
            trips.add(trip);
            trip.setGuide(this);
        }
    }

    public void removeTrip(Trip trip)
    {
        if (trip != null)
        {
            trips.remove(trip);
            trip.setGuide(null);
        }
    }


}
