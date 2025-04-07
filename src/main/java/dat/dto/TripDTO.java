package dat.dto;


import dat.entities.Trip;
import dat.enums.TripCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TripDTO
{
    private String name;
    private Double price;
    private TripCategory category;
    private String startTime;
    private String endTime;
    private PositionDTO startPosition;
    private GuideDTO guide;

    public TripDTO(Trip entity)
    {
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.category = entity.getCategory();
        this.startTime = entity.getStartTime().toString();
        this.endTime = entity.getEndTime().toString();
        this.startPosition = new PositionDTO(entity.getStartPosition().getDescription(), entity.getStartPosition().getLatitude(), entity.getStartPosition().getLongitude());
        this.guide = entity.getGuide()==null ? null : new GuideDTO(entity.getGuide());
    }

}
