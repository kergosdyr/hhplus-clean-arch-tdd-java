package com.justin.clean.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LectureRegisterLoaderUnitTest {

    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    @InjectMocks
    private LectureRegisterLoader lectureRegisterLoader;

    @BeforeEach
    public void LectureRegisterLoaderTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 이미 등록된 경우 true를 반환한다")
    void hasDuplicateRegisterByShouldReturnTrueWhenAlreadyRegistered() {
        // given
        when(lectureRegisterRepository.existsById(1L, 2L)).thenReturn(true);

        // when
        boolean result = lectureRegisterLoader.hasDuplicateRegisterBy(1L, 2L);

        // then
        assertThat(result).isTrue();
        verify(lectureRegisterRepository, times(1)).existsById(1L, 2L);
    }

    @Test
    @DisplayName("사용자와 강의 ID로 등록 가능 여부를 확인할 때 등록되지 않은 경우 false를 반환한다")
    void hasDuplicateRegisterByShouldReturnFalseWhenNotRegistered() {
        // given
        when(lectureRegisterRepository.existsById(1L, 2L)).thenReturn(false);

        // when
        boolean result = lectureRegisterLoader.hasDuplicateRegisterBy(1L, 2L);

        // then
        assertThat(result).isFalse();
        verify(lectureRegisterRepository, times(1)).existsById(1L, 2L);
    }
}
