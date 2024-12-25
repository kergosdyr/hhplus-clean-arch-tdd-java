package com.justin.clean.app;

import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_LECTURE_ID;
import static com.justin.clean.config.LectureRegisterTestDataBuilder.DEFAULT_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureRegisterLoaderUnitTest {

    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    @InjectMocks
    private LectureRegisterLoader lectureRegisterLoader;

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 이미 등록된 경우 true를 반환한다")
    void hasDuplicateRegisterByShouldReturnTrueWhenAlreadyRegistered() {
        // given
        when(lectureRegisterRepository.existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .thenReturn(true);

        // when
        boolean result = lectureRegisterLoader.hasDuplicateRegisterBy(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);

        // then
        assertThat(result).isTrue();
        verify(lectureRegisterRepository, times(1)).existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
    }

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 등록되지 않은 경우 false를 반환한다")
    void hasDuplicateRegisterByShouldReturnFalseWhenNotRegistered() {
        // given
        when(lectureRegisterRepository.existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID))
                .thenReturn(false);

        // when
        boolean result = lectureRegisterLoader.hasDuplicateRegisterBy(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);

        // then
        assertThat(result).isFalse();
        verify(lectureRegisterRepository, times(1)).existsById(DEFAULT_USER_ID, DEFAULT_LECTURE_ID);
    }
}
