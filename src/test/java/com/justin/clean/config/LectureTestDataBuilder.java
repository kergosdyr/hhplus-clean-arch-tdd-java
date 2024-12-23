package com.justin.clean.config;

import java.time.LocalDate;
import java.util.List;

import com.justin.clean.domain.Lecture;

public class LectureTestDataBuilder {
    public static final int ATTENDEE_DEFAULT = 0;
    public static final int MAX_ATTENDEE_COUNT = 30;
    public static final long DEFAULT_ID = 1L;
    public static final LocalDate DEFAULT_LECTURE_DATE = LocalDate.of(2024, 12, 31);

    private Long id = DEFAULT_ID;
    private LocalDate lectureDate = DEFAULT_LECTURE_DATE;
    private int attendeeCount = ATTENDEE_DEFAULT; // 기본값은 0으로 설정
    private int maxAttendeeCount = MAX_ATTENDEE_COUNT; // 최대 참석자 수 (가정)

    public LectureTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public LectureTestDataBuilder withLectureDate(LocalDate lectureDate) {
        this.lectureDate = lectureDate;
        return this;
    }

    public LectureTestDataBuilder withAttendeeCount(int attendeeCount) {
        this.attendeeCount = attendeeCount;
        return this;
    }

    public LectureTestDataBuilder asFullyBooked() {
        this.attendeeCount = maxAttendeeCount;
        return this;
    }

    public LectureTestDataBuilder asAlmostFullyBooked() {
        this.attendeeCount = maxAttendeeCount - 1; // 거의 최대치
        return this;
    }

    public LectureTestDataBuilder asEmptyBooked() {
        this.attendeeCount = 0; // 0명인 상태
        return this;
    }

    public Lecture build() {
        return Lecture.builder()
                .id(id)
                .lectureDate(lectureDate)
                .attendeeCount(attendeeCount)
                .build();
    }

    public static Lecture defaultVal() {
        return Lecture.builder()
            .id(DEFAULT_ID)
            .lectureDate(DEFAULT_LECTURE_DATE)
            .attendeeCount(ATTENDEE_DEFAULT)
            .build();
    }

    public static Lecture defaultWithId(long id) {
        return Lecture.builder()
            .id(id)
            .lectureDate(DEFAULT_LECTURE_DATE)
            .attendeeCount(ATTENDEE_DEFAULT)
            .build();
    }

    public static List<Lecture> defaultMultiple() {
        return List.of(defaultVal(), defaultWithId(2L));
    }

}
