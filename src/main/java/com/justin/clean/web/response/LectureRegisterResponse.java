package com.justin.clean.web.response;

import com.justin.clean.domain.LectureRegister;
import java.time.LocalDateTime;

public record LectureRegisterResponse(long lectureId, LocalDateTime registeredAt) {
    public static LectureRegisterResponse fromEntity(LectureRegister lectureRegister) {
        return new LectureRegisterResponse(lectureRegister.getLectureId(), lectureRegister.getRegisteredAt());
    }
}
