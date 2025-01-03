package com.justin.clean.domain;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LectureUnitTest {

    @Test
    @DisplayName("참석자가 30명 이상인 경우 추가 참석 가능 여부는 false 이다")
    void isAttendAvailableShouldBeFalseWhenAttendeeCountIs30OrMore() {
        Lecture lecture = Lecture.builder().attendeeCount(30).build();

        assertThat(lecture.isAttendAvailable()).isEqualTo(false);
    }

    @Test
    @DisplayName("참석자가 30명 미만인 경우 추가 참석 가능 여부는 true 이다")
    void isAttendAvailableShouldBeTrueWhenAttendeeCountIsLessThan30() {
        Lecture lecture = Lecture.builder().attendeeCount(29).build();

        assertThat(lecture.isAttendAvailable()).isEqualTo(true);
    }

    @Test
    @DisplayName("참석자가 30명 이상인 경우 추가 참석 불가능 여부는 true 이다")
    void isAttendNotAvailableShouldBeTrueWhenAttendeeCountIs30OrMore() {
        Lecture lecture = Lecture.builder().attendeeCount(30).build();

        assertThat(lecture.isAttendNotAvailable()).isEqualTo(true);
    }

    @Test
    @DisplayName("참석자가 30명 미만인 경우 추가 참석 불가능 여부는 false 이다")
    void isAttendNotAvailableShouldBeFalseWhenAttendeeCountIsLessThan30() {
        Lecture lecture = Lecture.builder().attendeeCount(29).build();

        assertThat(lecture.isAttendNotAvailable()).isEqualTo(false);
    }

    @Test
    @DisplayName("강의 날짜가 등록 요청시간 날짜보다 미래인 경우 강의 Expired 여부는 false 이다")
    void isLectureExpiredShouldBeFalseWhenLectureDateIsInFuture() {
        Lecture lecture =
                Lecture.builder().lectureDate(LocalDate.of(2024, 12, 31)).build();

        assertThat(lecture.isLectureExpired(of(2024, 12, 30, 1, 1))).isEqualTo(false);
    }

    @Test
    @DisplayName("강의 날짜가 등록 요청시간 날짜보다 같거나 과거인 경우 강의 Expired 여부는 true 이다")
    void isLectureExpiredShouldBeTrueWhenLectureDateIsTodayOrPast() {
        Lecture lecture =
                Lecture.builder().lectureDate(LocalDate.of(2024, 12, 31)).build();

        assertThat(lecture.isLectureExpired(of(2024, 12, 31, 1, 1))).isEqualTo(true);

        assertThat(lecture.isLectureExpired(of(2025, 1, 1, 1, 1))).isEqualTo(true);
    }

    @Test
    @DisplayName("강의 참석자를 증분하는 경우 하나만 증분된다")
    void increaseAttendeeShouldIncrementAttendeeCountByOne() {
        // given
        Lecture lecture = Lecture.builder().attendeeCount(29).build();

        // when
        lecture.increaseAttendee();

        // then
        assertThat(lecture.getAttendeeCount()).isEqualTo(30);
    }
}
