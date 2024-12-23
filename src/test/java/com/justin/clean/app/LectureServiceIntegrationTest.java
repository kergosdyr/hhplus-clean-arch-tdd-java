package com.justin.clean.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.justin.clean.config.IntegrationTest;
import com.justin.clean.config.LectureRegisterTestDataBuilder;
import com.justin.clean.config.LectureTestDataBuilder;
import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LectureServiceIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("동일한 신청자가 동일한 강의에 대해서 한 번 이상의 수강신청을 한 경우 예외를 발생시킨다.")
    void shouldThrowExceptionWhenDuplicateRegistration() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal();
        lectureJpaRepository.save(lecture);

        var lectureRegister = LectureRegisterTestDataBuilder.defaultVal();
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

        lectureJpaRepository.save(lecture);

        var lectureRegister = LectureRegisterTestDataBuilder.defaultVal();

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("특강이 만료된 경우 등록 시 예외를 발생시킨다")
    void shouldThrowExceptionWhenLectureIsExpired() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal();

        lectureJpaRepository.save(lecture);

        var lectureRegister = LectureRegisterTestDataBuilder.defaultExpired();

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("특강이 성공적으로 등록시 특강의 참석자가 증가한다")
    void shouldRegisterLectureSuccessfullyAndIncreaseAttendeeCount() {
        // given
        var lecture = LectureTestDataBuilder.defaultVal();

        lectureJpaRepository.save(lecture);

        var lectureRegister = LectureRegisterTestDataBuilder.defaultVal();

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
        var givenLectures = LectureTestDataBuilder.defaultMultiple();

        lectureJpaRepository.saveAll(givenLectures);

        var lectureRegisters = LectureRegisterTestDataBuilder.multiple(givenLectures);

        lectureRegisterJpaRepository.saveAll(lectureRegisters);

        // when
        var registeredLectures = lectureService.findAllRegisteredBy(LectureRegisterTestDataBuilder.DEFAULT_USER_ID);

        // then
        assertThat(registeredLectures).hasSize(2);
        assertThat(registeredLectures).extracting("id").containsExactlyInAnyOrder(givenLectures.get(0).getId(), givenLectures.get(1).getId());
    }

    @Test
    @DisplayName("특정 날짜의 사용 가능한 강의를 조회한다")
    void shouldFindAllAvailableLecturesByDate() {
        // given
        List<Lecture> givenLectures = LectureTestDataBuilder.defaultMultiple();

        lectureJpaRepository.saveAll(givenLectures);

        // when
        var availableLectures = lectureService.findAllAvailableBy(LectureTestDataBuilder.DEFAULT_LECTURE_DATE);

        // then
        assertThat(availableLectures).hasSize(2);
        assertThat(availableLectures).extracting("id").containsExactlyInAnyOrder(givenLectures.get(0).getId(), givenLectures.get(1).getId());
    }

    @Test
    @DisplayName("동시에 동일한 특강에 대해 40명이 신청했을 때 30명만 성공한다")
    void shouldAllowOnly30RegistrationsWhen40ConcurrentRequests() throws InterruptedException {
        // given
        var lecture = LectureTestDataBuilder.defaultVal();
        Lecture savedLecture = lectureJpaRepository.save(lecture);

        int totalRequests = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < totalRequests; i++) {
            final long userId = i + 1;
            futures.add(executorService.submit(() -> {
                try {
                    latch.await();
                    var lectureRegister = LectureRegister.builder()
                            .userId(userId)
                            .lectureId(savedLecture.getId())
                            .registeredAt(LocalDateTime.of(2024, 12, 19, 10, 0))
                            .build();
                    lectureService.register(lectureRegister);

                    return true;
                } catch (ApiException e) {
                    if (e.getErrorType() == ErrorType.REGISTER_OVER_ERROR) {
                        return false;
                    }
                    throw e;
                }
            }));
        }

        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // then
        long successCount = futures.stream()
                .filter(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        long failureCount = futures.stream()
                .filter(future -> {
                    try {
                        return !future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        var updatedLecture = lectureJpaRepository.findById(savedLecture.getId()).orElseThrow();
        assertThat(successCount).isEqualTo(30);
        assertThat(failureCount).isEqualTo(10);
        assertThat(updatedLecture.getAttendeeCount()).isEqualTo(30);
    }

    @Test
    @DisplayName("동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공한다")
    void shouldAllowOnlyOneRegistrationForSameUser() throws InterruptedException {
        // given
        var lecture = Lecture.builder()
                .lectureDate(LocalDate.of(2024, 12, 20))
                .attendeeCount(0)
                .build();
        lectureJpaRepository.save(lecture);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < 5; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    latch.await();
                    var lectureRegister = LectureRegister.builder()
                            .userId(1L)
                            .lectureId(lecture.getId())
                            .registeredAt(LocalDateTime.of(2024, 12, 19, 10, 0))
                            .build();
                    lectureService.register(lectureRegister);
                    return true;
                } catch (ApiException e) {
                    if (e.getErrorType() == ErrorType.DUPLICATE_REGISTER_ERROR) {
                        return false;
                    }
                    throw e;
                }
            }));
        }

        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // then
        long successCount = futures.stream()
                .filter(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        long failureCount = futures.stream()
                .filter(f -> {
                    try {
                        return !f.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        var updatedLecture = lectureJpaRepository.findById(lecture.getId()).orElseThrow();

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(4);
        assertThat(updatedLecture.getAttendeeCount()).isEqualTo(1);
    }
}
