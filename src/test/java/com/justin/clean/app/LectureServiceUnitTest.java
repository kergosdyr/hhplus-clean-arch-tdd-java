package com.justin.clean.app;

import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_LECTURE_ID;
import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_USER_ID;
import static com.justin.clean.config.LectureTestDataBuilder.DEFAULT_LECTURE_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.justin.clean.config.LectureRegisterTestDataBuilder;
import com.justin.clean.config.LectureTestDataBuilder;
import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureServiceUnitTest {

    @Mock
    private LectureRegisterSaver lectureRegisterSaver;

    @Mock
    private LectureRegisterValidator lectureRegisterValidator;

    @Mock
    private LectureLoader lectureLoader;

    @InjectMocks
    private LectureService lectureService;

    @Test
    @DisplayName("등록 시 이미 등록된 경우 ApiException을 던진다")
    void registerShouldThrowExceptionWhenAlreadyRegistered() {
        // given
        LectureRegister lectureRegister = LectureRegisterTestDataBuilder.defaultVal();

        doThrow(new ApiException(ErrorType.DUPLICATE_REGISTER_ERROR))
                .when(lectureRegisterValidator)
                .validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.DUPLICATE_REGISTER_ERROR.getMessage());

        verify(lectureRegisterValidator, times(1)).validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
    }

    @Test
    @DisplayName("등록 시 강의가 만석이거나 만료된 경우 ApiException을 던진다")
    void registerShouldThrowExceptionWhenLectureFullOrExpired() {
        // given
        var lectureRegister = new LectureRegisterTestDataBuilder().asExpired().build();
        var lecture = LectureTestDataBuilder.defaultVal();

        doThrow(new ApiException(ErrorType.REGISTER_OVER_ERROR))
                .when(lectureRegisterValidator)
                .validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);

        when(lectureLoader.load(DEFAULT_LECTURE_ID)).thenReturn(lecture);

        // when & then
        assertThatThrownBy(() -> lectureService.register(lectureRegister))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorType.REGISTER_OVER_ERROR.getMessage());

        verify(lectureRegisterValidator, times(1)).validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
        verify(lectureLoader, times(1)).load(DEFAULT_LECTURE_ID);
        verifyNoInteractions(lectureRegisterSaver);
    }

    @Test
    @DisplayName("등록이 성공적으로 완료되면 저장된 LectureRegister를 반환하고 참석자가 +1 증가한다")
    void registerShouldSaveAndIncreaseAttendeeCount() {
        // given
        var lectureRegister = LectureRegisterTestDataBuilder.defaultVal();
        var lecture = LectureTestDataBuilder.defaultVal();

        when(lectureLoader.load(DEFAULT_LECTURE_ID)).thenReturn(lecture);
        when(lectureRegisterSaver.save(lectureRegister)).thenReturn(lectureRegister);

        // when
        LectureRegister result = lectureService.register(lectureRegister);

        // then
        assertThat(result).isEqualTo(lectureRegister);
        assertThat(lecture.getAttendeeCount()).isEqualTo(LectureTestDataBuilder.ATTENDEE_DEFAULT + 1);
        verify(lectureRegisterValidator, times(1)).validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
        verify(lectureLoader, times(1)).load(DEFAULT_LECTURE_ID);
        verify(lectureRegisterSaver, times(1)).save(lectureRegister);
    }

    @Test
    @DisplayName("사용자가 등록한 모든 강의를 반환한다")
    void findAllRegisteredByShouldReturnAllRegisteredLectures() {
        // given
        var givenLectures = LectureTestDataBuilder.dual();

        when(lectureLoader.loadRegistered(DEFAULT_USER_ID)).thenReturn(givenLectures);

        // when
        var result = lectureService.findAllRegisteredBy(DEFAULT_USER_ID);

        // then
        assertThat(result).containsAll(givenLectures);
        verify(lectureLoader, times(1)).loadRegistered(DEFAULT_USER_ID);
    }

    @Test
    @DisplayName("특정 날짜의 모든 사용 가능한 강의를 반환한다")
    void findAllAvailableByShouldReturnAvailableLecturesForDate() {
        // given
        var givenLectures = LectureTestDataBuilder.dual();

        when(lectureLoader.loadAllAvailableBy(DEFAULT_LECTURE_DATE)).thenReturn(givenLectures);

        // when
        List<Lecture> result = lectureService.findAllAvailableBy(DEFAULT_LECTURE_DATE);

        // then
        assertThat(result).containsAll(givenLectures);
        verify(lectureLoader, times(1)).loadAllAvailableBy(DEFAULT_LECTURE_DATE);
    }
}
