package com.justin.clean.config;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

public class LectureRegisterTestDataBuilder {

    public static final long DEFAULT_ID = 1L;
    public static final long DEFAULT_USER_ID = 1L;
    public static final long DEFAULT_LECTURE_ID = 1L;
    public static final LocalDateTime DEFAULT_REGISTERED_AT = LocalDateTime.of(2024, 12, 30, 10, 0);

    private Long id = DEFAULT_ID;
    private Long userId = DEFAULT_USER_ID;
    private Long lectureId = DEFAULT_LECTURE_ID;
    private LocalDateTime registeredAt = DEFAULT_REGISTERED_AT;
    private Lecture lecture = null;

    public LectureRegisterTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public LectureRegisterTestDataBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public LectureRegisterTestDataBuilder withLectureId(Long lectureId) {
        this.lectureId = lectureId;
        return this;
    }

    public LectureRegisterTestDataBuilder withRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public LectureRegisterTestDataBuilder withLecture(Lecture lecture) {
        this.lecture = lecture;
        return this;
    }

    public LectureRegisterTestDataBuilder asExpired() {
        this.registeredAt =
                LectureTestDataBuilder.DEFAULT_LECTURE_DATE.plusDays(1).atStartOfDay();
        return this;
    }

    public LectureRegister build() {
        return LectureRegister.builder()
                .id(this.id)
                .userId(this.userId)
                .lectureId(this.lectureId)
                .registeredAt(this.registeredAt)
                .lecture(this.lecture)
                .build();
    }

    public static LectureRegister defaultVal() {
        return LectureRegister.builder()
                .id(DEFAULT_ID)
                .userId(DEFAULT_USER_ID)
                .lectureId(DEFAULT_LECTURE_ID)
                .registeredAt(DEFAULT_REGISTERED_AT)
                .lecture(LectureTestDataBuilder.defaultVal())
                .build();
    }

    public static List<LectureRegister> multiple(Iterable<Lecture> lectures) {
        return StreamSupport.stream(lectures.spliterator(), false)
                .map(lecture -> new LectureRegisterTestDataBuilder()
                        .withLectureId(lecture.getId())
                        .withLecture(lecture)
                        .build())
                .toList();
    }
}
