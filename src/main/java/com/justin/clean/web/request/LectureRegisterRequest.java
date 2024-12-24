package com.justin.clean.web.request;

import com.justin.clean.domain.LectureRegister;
import java.time.LocalDateTime;

public record LectureRegisterRequest(Long userId, LocalDateTime registeredAt) {

    public LectureRegister toEntity(long lectureId) {
        return new LectureRegister(lectureId, this.userId, this.registeredAt);
    }
}
