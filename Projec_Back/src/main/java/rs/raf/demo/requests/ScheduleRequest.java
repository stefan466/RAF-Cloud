package rs.raf.demo.requests;

import lombok.Data;

@Data
public class ScheduleRequest {
    private Long id;
    private String date;
    private String time;
    private String action;
}
