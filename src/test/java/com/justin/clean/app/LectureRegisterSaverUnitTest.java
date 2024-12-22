package com.justin.clean.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.justin.clean.domain.LectureRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LectureRegisterSaverUnitTest {
    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    @InjectMocks
    private LectureRegisterSaver lectureRegisterSaver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("강의 등록정보를 저장하면 저장된 엔티티를 반환한다")
    void saveShouldReturnSavedEntity() {
        // given
        var lectureRegister = LectureRegister.builder().userId(1L).lectureId(2L).build();

        var savedLectureRegister =
                LectureRegister.builder().id(100L).userId(1L).lectureId(2L).build();

        when(lectureRegisterRepository.save(lectureRegister)).thenReturn(savedLectureRegister);

        // when
        var result = lectureRegisterSaver.save(lectureRegister);

        // then
        assertThat(result).isEqualTo(savedLectureRegister);
        verify(lectureRegisterRepository, times(1)).save(lectureRegister);
    }
}