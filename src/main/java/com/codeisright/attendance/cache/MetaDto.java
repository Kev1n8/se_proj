package com.codeisright.attendance.cache;

import com.codeisright.attendance.data.AttendanceMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetaDto {
    private final String id;

    private final int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private final LocalDateTime start;

    private final LocalDateTime deadline;

    private final Long latitude;

    private final Long longitude;

    private final String classId;

    public MetaDto(AttendanceMeta meta) {
        if (meta == null) {
            this.id = "";
            this.requirement = 0;
            this.start = LocalDateTime.now();
            this.deadline = LocalDateTime.now();
            this.latitude = 0L;
            this.longitude = 0L;
            this.classId = "";
            return;
        }
        this.id = meta.getId();
        this.requirement = meta.getRequirement();
        this.start = meta.getStart();
        this.deadline = meta.getDeadline();
        this.latitude = meta.getLatitude();
        this.longitude = meta.getLongitude();
        this.classId = meta.getAclass().getId();
    }

    public static Page<MetaDto> Convert(Page<AttendanceMeta> metas) {
        List<MetaDto> dtos = new ArrayList<>();
        for (AttendanceMeta meta : metas) {
            dtos.add(new MetaDto(meta));
        }
        return new PageImpl<>(dtos, metas.getPageable(), metas.getTotalElements());
    }

    public int getRequirement() {
        return requirement;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Long getLatitude() {
        return latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public String getClassId() {
        return classId;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> toMap(){
        return Map.of(
                "id", id,
                "requirement", String.valueOf(requirement),
                "start", start.toString(),
                "deadline", deadline.toString(),
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude),
                "classId", classId
        );
    }
}
