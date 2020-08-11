package guru.springframework.msscbeerservice.web.mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class DateMapper {
    public OffsetDateTime asOffsetDateTime(Timestamp ts){
        if (ts != null) {
            LocalDateTime ldt = ts.toLocalDateTime();
            return OffsetDateTime.of(ldt.getYear(), ldt.getMonthValue(),
                    ldt.getDayOfMonth(), ldt.getHour(), ldt.getMinute(),
                    ldt.getSecond(), ldt.getNano(), ZoneOffset.UTC);
        } else {
            return null;
        }
    }

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime){
        if (offsetDateTime != null) {
            return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        } else {
            return null;
        }
    }
}
