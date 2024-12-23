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

    private Lecture lecture = LectureTestDataBuilder.defaultVal();


    public LectureRegisterTestDataBuilder withLecture(Lecture lecture) {
        this.lecture = lecture;
        return this;
    }

    public LectureRegister build() {
        return LectureRegister.builder()
                .id(DEFAULT_ID)
                .userId(DEFAULT_USER_ID)
                .lectureId(DEFAULT_LECTURE_ID)
                .registeredAt(DEFAULT_REGISTERED_AT)
                .lecture(lecture)
                .build();
    }

    public static LectureRegister defaultVal() {
        return LectureRegister.builder()
            .id(DEFAULT_ID)
            .userId(DEFAULT_USER_ID)
            .lectureId(DEFAULT_LECTURE_ID)
            .registeredAt(DEFAULT_REGISTERED_AT)
            .lecture(LectureTestDataBuilder.defaultVal())
            .lectureId(LectureTestDataBuilder.DEFAULT_ID)
            .build();
    }

    public static LectureRegister defaultExpired() {
        return LectureRegister.builder()
            .id(DEFAULT_ID)
            .userId(DEFAULT_USER_ID)
            .lectureId(DEFAULT_LECTURE_ID)
            .registeredAt(LectureTestDataBuilder.DEFAULT_LECTURE_DATE.plusDays(1).atStartOfDay())
            .lecture(LectureTestDataBuilder.defaultVal())
            .lectureId(LectureTestDataBuilder.DEFAULT_ID)
            .build();
    }





    public static List<LectureRegister> multiple(Lecture... lectures) {
        return Stream.of(lectures)
                .map(lecture -> new LectureRegisterTestDataBuilder()
                        .withLecture(lecture)
                        .build())
                .toList();
    }


    public static List<LectureRegister> multiple(Iterable<Lecture> lectures) {
        return StreamSupport.stream(lectures.spliterator(), false)
            .map(lecture -> new LectureRegisterTestDataBuilder()
                .withLecture(lecture)
                .build())
            .toList();
    }

}
