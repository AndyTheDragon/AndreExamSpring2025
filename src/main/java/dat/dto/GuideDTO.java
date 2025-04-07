package dat.dto;


import dat.entities.Guide;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuideDTO
{
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Integer yearsOfExperience;

    public GuideDTO(Guide guide)
    {
        this.firstName = guide.getFirstName();
        this.lastName = guide.getLastName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }
}
