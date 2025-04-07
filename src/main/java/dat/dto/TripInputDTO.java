package dat.dto;


import dat.enums.TripCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TripInputDTO
{
    private String name;
    private Double price;
    private TripCategory category;
    private String startTime;
    private String endTime;
    private PositionDTO startPosition;
    private Integer guideId;
}
