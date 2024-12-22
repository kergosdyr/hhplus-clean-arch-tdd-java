package com.justin.clean.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LectureServiceUnitTest {

    @Mock
    private LectureRegisterSaver lectureRegisterSaver;

    @Mock
    private LectureLoader lectureLoader;

    @InjectMocks
    private LectureService lectureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("등록 시 강의가 만료된 경우 ApiException을 던진다")
    void registerShouldThrowExceptionWhenLectureFullOrExpired() {
        // given
        long userId = 1L;
        long lectureId = 2L;
        LocalDateTime registeredAt = LocalDateTime.of(2024, 12, 20, 10, 0);
        LectureRegister lectureRegister = LectureRegister.builder()
                .userId(userId)
                .lectureId(lectureId)
                .registeredAt(registeredAt)
                .build();

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .attendeeCount(30)
                .lectureDate(LocalDate.of(2024, 12, 19))
                .build();

        when(lectureLoader.load(lectureId)).thenReturn(lecture);

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());

        verify(lectureLoader, times(1)).load(lectureId);
        verifyNoInteractions(lectureRegisterSaver);
    }

    @Test
    @DisplayName("등록이 성공적으로 완료되면 저장된 LectureRegister를 반환하고 참석자가 +1 증가한다")
    void registerShouldSaveAndIncreaseAttendeeCount() {
        // given
        long userId = 1L;
        long lectureId = 2L;
        LocalDateTime registeredAt = LocalDateTime.of(2024, 12, 20, 10, 0);
        LectureRegister lectureRegister = LectureRegister.builder()
                .userId(userId)
                .lectureId(lectureId)
                .registeredAt(registeredAt)
                .build();

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .attendeeCount(29)
                .lectureDate(LocalDate.of(2024, 12, 21))
                .build();

        LectureRegister savedRegister = LectureRegister.builder()
                .id(100L)
                .userId(userId)
                .lectureId(lectureId)
                .registeredAt(registeredAt)
                .build();

        when(lectureLoader.load(lectureId)).thenReturn(lecture);
        when(lectureRegisterSaver.save(lectureRegister)).thenReturn(savedRegister);

        // when
        LectureRegister result = lectureService.register(lectureRegister);

        // then
        assertThat(result).isEqualTo(savedRegister);
        assertThat(lecture.getAttendeeCount()).isEqualTo(30); // 참석자가 +1 증가했는지 확인
        verify(lectureLoader, times(1)).load(lectureId);
        verify(lectureRegisterSaver, times(1)).save(lectureRegister);
    }

    @Test
    @DisplayName("사용자가 등록한 모든 강의를 반환한다")
    void findAllRegisteredByShouldReturnAllRegisteredLectures() {
        // given
        long userId = 1L;
        Lecture lecture1 = Lecture.builder()
                .id(1L)
                .attendeeCount(10)
                .lectureDate(LocalDate.of(2024, 12, 21))
                .build();
        Lecture lecture2 = Lecture.builder()
                .id(2L)
                .attendeeCount(15)
                .lectureDate(LocalDate.of(2024, 12, 22))
                .build();

        when(lectureLoader.loadRegistered(userId)).thenReturn(List.of(lecture1, lecture2));

        // when
        List<Lecture> result = lectureService.findAllRegisteredBy(userId);

        // then
        assertThat(result).containsExactly(lecture1, lecture2);
        verify(lectureLoader, times(1)).loadRegistered(userId);
    }

    @Test
    @DisplayName("특정 날짜의 모든 사용 가능한 강의를 반환한다")
    void findAllAvailableByShouldReturnAvailableLecturesForDate() {
        // given
        LocalDate lectureDate = LocalDate.of(2024, 12, 20);
        Lecture lecture1 = Lecture.builder()
                .id(1L)
                .attendeeCount(10)
                .lectureDate(lectureDate)
                .build();
        Lecture lecture2 = Lecture.builder()
                .id(2L)
                .attendeeCount(20)
                .lectureDate(lectureDate)
                .build();

        when(lectureLoader.loadAllAvailableBy(lectureDate)).thenReturn(List.of(lecture1, lecture2));

        // when
        List<Lecture> result = lectureService.findAllAvailableBy(lectureDate);

        // then
        assertThat(result).containsExactly(lecture1, lecture2);
        verify(lectureLoader, times(1)).loadAllAvailableBy(lectureDate);
    }
}
