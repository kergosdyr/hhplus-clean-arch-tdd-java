package com.justin.clean.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;

public class LectureRegisterTestDataBuilder {

    public static final long DEFAULT_ID = 1L;
    public static final long DEFAULT_USER_ID = 1L;
    public static final long DEFAULT_LECTURE_ID = 1L;
    public static final LocalDateTime DEFAULT_REGISTERED_AT = LocalDateTime.of(2024, 12, 30, 10, 0);

    private Long id = DEFAULT_ID;
    private Long userId = DEFAULT_USER_ID;
    private Long lectureId = DEFAULT_LECTURE_ID;
    private LocalDateTime registeredAt = DEFAULT_REGISTERED_AT;
    private Lecture lecture = LectureTestDataBuilder.defaultVal();

    /**
     * 체이닝 메서드 예시
     */
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

    public LectureRegister build() {
        return LectureRegister.builder()
            .id(this.id)
            .userId(this.userId)
            .lectureId(this.lectureId)
            .registeredAt(this.registeredAt)
            .lecture(this.lecture)
            .build();
    }

    /**
     * 기본값 빌더
     */
    public static LectureRegister defaultVal() {
        return LectureRegister.builder()
            .id(DEFAULT_ID)
            .userId(DEFAULT_USER_ID)
            .lectureId(DEFAULT_LECTURE_ID)
            .registeredAt(DEFAULT_REGISTERED_AT)
            .lecture(LectureTestDataBuilder.defaultVal())
            .build();
    }

    /**
     * 기본값 + 만료된 Register
     */
    public static LectureRegister defaultExpired() {
        return LectureRegister.builder()
            .id(DEFAULT_ID)
            .userId(DEFAULT_USER_ID)
            .lectureId(DEFAULT_LECTURE_ID)
            .registeredAt(LectureTestDataBuilder.DEFAULT_LECTURE_DATE.plusDays(1).atStartOfDay())
            .lecture(LectureTestDataBuilder.defaultVal())
            .build();
    }

    /**
     * 여러 Lecture 객체에 대해서 각각 LectureRegister 생성
     */
    public static List<LectureRegister> multiple(Lecture... lectures) {
        return Stream.of(lectures)
            .map(lecture -> new LectureRegisterTestDataBuilder()
                .withLecture(lecture)
                .build())
            .toList();
    }

    /**
     * 여러 Lecture 객체(Iterable)에 대해서 각각 LectureRegister 생성
     */
    public static List<LectureRegister> multiple(Iterable<Lecture> lectures) {
        return StreamSupport.stream(lectures.spliterator(), false)
            .map(lecture -> new LectureRegisterTestDataBuilder()
                .withLecture(lecture)
                .build())
            .toList();
    }
}
