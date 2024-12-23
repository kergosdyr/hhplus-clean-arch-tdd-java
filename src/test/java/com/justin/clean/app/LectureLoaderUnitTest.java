package com.justin.clean.app;

import static com.justin.clean.config.LectureTestDataBuilder.DEFAULT_ID;
import static com.justin.clean.config.LectureTestDataBuilder.DEFAULT_LECTURE_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.justin.clean.config.LectureRegisterTestDataBuilder;
import com.justin.clean.config.LectureTestDataBuilder;
import com.justin.clean.domain.Lecture;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LectureLoaderUnitTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    @InjectMocks
    private LectureLoader lectureLoader;

    @BeforeEach
    public void LectureLoaderTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("강의 ID로 강의를 로드할 때 강의가 존재하지 않으면 ApiException이 발생한다")
    void loadShouldThrowApiExceptionWhenLectureNotFound() {
        // given
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> lectureLoader.load(1L))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.DEFAULT_ERROR.getMessage());
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("강의 ID로 강의를 로드할 때 강의가 존재하면 강의를 반환한다")
    void loadShouldReturnLectureWhenFound() {
        // given
        Lecture lecture = LectureTestDataBuilder.defaultVal();
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        // when
        Lecture result = lectureLoader.load(1L);

        // then
        assertThat(result).isEqualTo(lecture);
        verify(lectureRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("사용자가 등록한 강의를 로드할 때 모든 등록 강의가 반환된다")
    void loadRegisteredShouldReturnAllRegisteredLecturesForUser() {
        // given
        List<Lecture> givenLectures = LectureTestDataBuilder.defaultMultiple();


        when(lectureRegisterRepository.findAllBy(DEFAULT_ID)).thenReturn(LectureRegisterTestDataBuilder.multiple(givenLectures));

        // when
        List<Lecture> lectures = lectureLoader.loadRegistered(DEFAULT_ID);

        // then
        assertThat(lectures).containsAll(givenLectures);
        verify(lectureRegisterRepository, times(1)).findAllBy(1L);
    }

    @Test
    @DisplayName("특정 날짜 기준으로 사용 가능한 강의를 로드할 때 강의 목록이 반환된다")
    void loadAllAvailableByShouldReturnAvailableLecturesForDate() {
        // given
		List<Lecture> givenLectures = LectureTestDataBuilder.defaultMultiple();

        when(lectureRepository.findAllAvailableByLectureDate(DEFAULT_LECTURE_DATE)).thenReturn(givenLectures);

        // when
        List<Lecture> lectures = lectureLoader.loadAllAvailableBy(DEFAULT_LECTURE_DATE);

        // then
        assertThat(lectures).containsAll(givenLectures);
        verify(lectureRepository, times(1)).findAllAvailableByLectureDate(DEFAULT_LECTURE_DATE);
    }
}
