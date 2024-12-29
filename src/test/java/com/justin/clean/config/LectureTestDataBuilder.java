package com.justin.clean.config;

import com.justin.clean.domain.Lecture;
import java.time.LocalDate;
import java.util.List;

public class LectureTestDataBuilder {
    public static final int ATTENDEE_DEFAULT = 0;
    public static final int MAX_ATTENDEE_COUNT = 30;
    public static final long DEFAULT_ID = 1L;
    public static final LocalDate DEFAULT_LECTURE_DATE = LocalDate.of(2024, 12, 31);

    private Long id = DEFAULT_ID;
    private LocalDate lectureDate = DEFAULT_LECTURE_DATE;
    private int attendeeCount = ATTENDEE_DEFAULT;
    private final int maxAttendeeCount = MAX_ATTENDEE_COUNT;

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
        this.attendeeCount = 0;
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

    public static Lecture defaultVal(long id) {
        return Lecture.builder()
                .id(id)
                .lectureDate(DEFAULT_LECTURE_DATE)
                .attendeeCount(ATTENDEE_DEFAULT)
                .build();
    }

    public static List<Lecture> dual() {
        return List.of(defaultVal(), defaultVal(2L));
    }

    public static List<Lecture> dualForJpa() {
        return List.of(defaultVal(0L), defaultVal(0L));
    }
}
