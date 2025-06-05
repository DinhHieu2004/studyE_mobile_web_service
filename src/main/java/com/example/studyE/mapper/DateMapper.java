package com.example.studyE.mapper;


import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateMapper {

    @Named("longToLocalDateTime")
    public static LocalDateTime longToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
