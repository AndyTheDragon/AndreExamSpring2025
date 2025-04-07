package dat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dat.dto.InstructorDTO;
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
public class Instructor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer yearsOfExperience;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "instructor")
    @JsonBackReference
    @ToString.Exclude
    private Set<SkiLesson> skiLessons = new HashSet<>();

    public Instructor(InstructorDTO guide)
    {
        this.firstName = guide.getFirstName();
        this.lastName = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }


    public void addLesson(SkiLesson skiLesson)
    {
        if (skiLesson != null)
        {
            skiLessons.add(skiLesson);
            skiLesson.setInstructor(this);
        }
    }

    public void removeLesson(SkiLesson skiLesson)
    {
        if (skiLesson != null)
        {
            skiLessons.remove(skiLesson);
            skiLesson.setInstructor(null);
        }
    }


}
