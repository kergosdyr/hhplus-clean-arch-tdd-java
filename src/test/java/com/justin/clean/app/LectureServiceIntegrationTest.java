package com.justin.clean.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.justin.clean.config.ConcurrencyTestUtil;
import com.justin.clean.config.IntegrationTest;
import com.justin.clean.config.LectureRegisterTestDataBuilder;
import com.justin.clean.config.LectureTestDataBuilder;
import com.justin.clean.domain.Lecture;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LectureServiceIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("동일한 신청자가 동일한 강의에 대해서 한 번 이상의 수강신청을 한 경우 예외를 발생시킨다.")
    void shouldThrowExceptionWhenDuplicateRegistration() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal(0L);
        Lecture savedLecture = lectureJpaRepository.save(lecture);

        var lectureRegister = new LectureRegisterTestDataBuilder()
                .withLectureId(savedLecture.getId())
                .build();
        lectureRegisterJpaRepository.save(lectureRegister);

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.DUPLICATE_REGISTER_ERROR.getMessage());
    }

    @Test
    @DisplayName("특강은 정원이 초과되는 경우 예외를 반환한다")
    void shouldThrowExceptionWhenLectureIsFull() {
        // given
        var lecture = new LectureTestDataBuilder().asFullyBooked().build();

        Lecture savedLecture = lectureJpaRepository.save(lecture);

        var lectureRegister = new LectureRegisterTestDataBuilder()
                .withLectureId(savedLecture.getId())
                .build();

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("특강이 만료된 경우 등록 시 예외를 발생시킨다")
    void shouldThrowExceptionWhenLectureIsExpired() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal(0L);

        Lecture savedLecture = lectureJpaRepository.save(lecture);

        var lectureRegister = new LectureRegisterTestDataBuilder()
                .withLectureId(savedLecture.getId())
                .asExpired()
                .build();

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("특강이 성공적으로 등록시 특강의 참석자가 증가한다")
    void shouldRegisterLectureSuccessfullyAndIncreaseAttendeeCount() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal(0L);

        Lecture savedLecture = lectureJpaRepository.save(lecture);

        var lectureRegister = new LectureRegisterTestDataBuilder()
                .withLectureId(savedLecture.getId())
                .build();

        // when
        var savedRegister = lectureService.register(lectureRegister);
        var updatedLecture = lectureJpaRepository.findById(lecture.getId()).orElseThrow();

        // then
        assertThat(savedRegister).isNotNull();
        assertThat(updatedLecture.getAttendeeCount()).isEqualTo(1); // 참석자가 +1 증가
    }

    @Test
    @DisplayName("사용자가 등록한 특강을 모두 조회한다")
    void shouldFindAllRegisteredLecturesByUser() {
        // given
        var givenLectures = LectureTestDataBuilder.dualForJpa();

        List<Lecture> savedLectures = lectureJpaRepository.saveAll(givenLectures);

        var lectureRegisters = LectureRegisterTestDataBuilder.multiple(givenLectures);

        lectureRegisterJpaRepository.saveAll(lectureRegisters);

        // when
        var registeredLectures = lectureService.findAllRegisteredBy(LectureRegisterTestDataBuilder.DEFAULT_USER_ID);

        // then
        assertThat(registeredLectures).hasSize(2);
        assertThat(registeredLectures)
                .extracting("id")
                .containsExactlyInAnyOrder(
                        savedLectures.get(0).getId(), savedLectures.get(1).getId());
    }

    @Test
    @DisplayName("특정 날짜의 사용 가능한 강의를 조회한다")
    void shouldFindAllAvailableLecturesByDate() {
        // given
        List<Lecture> givenLectures = LectureTestDataBuilder.dualForJpa();

        List<Lecture> savedLectures = lectureJpaRepository.saveAll(givenLectures);

        // when
        var availableLectures = lectureService.findAllAvailableBy(LectureTestDataBuilder.DEFAULT_LECTURE_DATE);

        // then
        assertThat(availableLectures).hasSize(2);
        assertThat(availableLectures)
                .extracting("id")
                .containsExactlyInAnyOrder(
                        savedLectures.get(0).getId(), savedLectures.get(1).getId());
    }

    @Test
    @DisplayName("동시에 동일한 특강에 대해 40명이 신청했을 때 30명만 성공한다")
    void shouldAllowOnly30RegistrationsWhen40ConcurrentRequests() throws InterruptedException {
        // given
        var lecture = LectureTestDataBuilder.defaultVal(0L);
        Lecture savedLecture = lectureJpaRepository.save(lecture);

        AtomicLong userId = new AtomicLong(0);

        var result = ConcurrencyTestUtil.run(40, () -> {
            try {
                var lectureRegister = new LectureRegisterTestDataBuilder()
                        .withUserId(userId.getAndIncrement())
                        .withLectureId(savedLecture.getId())
                        .build();
                lectureService.register(lectureRegister);
                return true;
            } catch (ApiException e) {
                if (e.getErrorType() == ErrorType.REGISTER_OVER_ERROR) {
                    return false;
                }
                throw e;
            }
        });

        var updatedLecture = lectureJpaRepository.findById(savedLecture.getId()).orElseThrow();

        assertThat(result.success()).isEqualTo(30);
        assertThat(result.fail()).isEqualTo(10);
        assertThat(updatedLecture.getAttendeeCount()).isEqualTo(30);
    }

    @Test
    @DisplayName("동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공한다")
    void shouldAllowOnlyOneRegistrationForSameUser() throws InterruptedException {
        // given
        var lecture = LectureTestDataBuilder.defaultVal(0L);
        Lecture savedLecture = lectureJpaRepository.save(lecture);

        // when
        var result = ConcurrencyTestUtil.run(5, () -> {
            try {
                var lectureRegister = new LectureRegisterTestDataBuilder()
                        .withLectureId(savedLecture.getId())
                        .build();
                lectureService.register(lectureRegister);
                return true;
            } catch (ApiException e) {
                if (e.getErrorType() == ErrorType.DUPLICATE_REGISTER_ERROR) {
                    return false;
                }
                throw e;
            }
        });

        var updatedLecture = lectureJpaRepository.findById(lecture.getId()).orElseThrow();

        assertThat(result.success()).isEqualTo(1);
        assertThat(result.fail()).isEqualTo(4);
        assertThat(updatedLecture.getAttendeeCount()).isEqualTo(1);
    }
}
