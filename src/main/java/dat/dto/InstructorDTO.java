package dat.dto;


import dat.entities.Instructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
        this.firstName = instructor.getFirstName();
        this.lastName = instructor.getLastName();
        this.email = instructor.getEmail();
        this.phone = instructor.getPhone();
        this.yearsOfExperience = instructor.getYearsOfExperience();
    }
}
