package dat.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Instructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstructorDTO
{
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer yearsOfExperience;

    public InstructorDTO(Instructor instructor)
    {
        this.id = instructor.getId();
        this.firstName = instructor.getFirstName();
        this.lastName = instructor.getLastName();
        this.email = instructor.getEmail();
        this.phone = instructor.getPhone();
        this.yearsOfExperience = instructor.getYearsOfExperience();
    }
}
