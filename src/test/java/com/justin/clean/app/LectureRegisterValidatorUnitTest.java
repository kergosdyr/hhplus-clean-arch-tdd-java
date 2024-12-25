package com.justin.clean.app;

import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_LECTURE_ID;
import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_USER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureRegisterValidatorUnitTest {

    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    @InjectMocks
    private LectureRegisterValidator lectureRegisterValidator;

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 이미 등록된 경우 ApiException을 발생시킨다")
    void hasDuplicateRegisterByShouldThrowExceptionWhenAlreadyRegistered() {
        // given
        when(lectureRegisterRepository.existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .thenReturn(true);

        // when
        assertThatThrownBy(() -> lectureRegisterValidator.validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorType.DUPLICATE_REGISTER_ERROR.getMessage());

        // then
        verify(lectureRegisterRepository, times(1)).existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
    }

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 등록되지 않은 경우 아무런 예외를 발생시키지 않는다")
    void hasDuplicateRegisterByShouldThrowNothingWhenNotRegistered() {
        // given
        when(lectureRegisterRepository.existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .thenReturn(false);

        // when
        Assertions.assertThatCode(() -> lectureRegisterValidator.validate(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .doesNotThrowAnyException();

        // then
        verify(lectureRegisterRepository, times(1)).existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
    }
}
