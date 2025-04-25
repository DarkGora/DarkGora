import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class TestHistory {
    private Long userId;
    private String testType;
    private int score;
    private LocalDateTime testDate;
}