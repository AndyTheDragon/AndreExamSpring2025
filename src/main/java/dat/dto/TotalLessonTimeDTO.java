package dat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TotalLessonTimeDTO
{
    private Integer instructorId;
    private Long totalTime;
}
