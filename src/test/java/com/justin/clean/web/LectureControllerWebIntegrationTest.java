package com.justin.clean.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.justin.clean.config.WebIntegrationTest;
import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.domain.Presenter;
import com.justin.clean.web.request.LectureRegisterRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class LectureControllerWebIntegrationTest extends WebIntegrationTest {

    @Test
    @DisplayName("사용 가능한 강의 목록을 반환한다")
    void shouldReturnAvailableLectures() throws Exception {
        // given
        LocalDate lectureDate = LocalDate.of(2024, 12, 20);
        Presenter presenter = Presenter.builder()
                .id(1L)
                .name("테스트")
                .age(30)
                .email("test@email.com")
                .build();

        Lecture lecture1 = Lecture.builder()
                .id(1L)
                .title("테스트강의1")
                .lectureDate(lectureDate)
                .attendeeCount(10)
                .presenter(presenter)
                .presenterId(1L)
                .build();
        Lecture lecture2 = Lecture.builder()
                .id(2L)
                .title("테스트강의2")
                .lectureDate(lectureDate)
                .attendeeCount(20)
                .presenter(presenter)
                .presenterId(1L)
                .build();

        when(lectureService.findAllAvailableBy(lectureDate)).thenReturn(List.of(lecture1, lecture2));

        // when & then
        mockMvc.perform(get("/api/v1/lectures")
                        .param("date", lectureDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].lectureId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("테스트강의1"))
                .andExpect(jsonPath("$.data[0].presenter.name").value("테스트"))
                .andExpect(jsonPath("$.data[1].lectureId").value(2L))
                .andExpect(jsonPath("$.data[1].title").value("테스트강의2"))
                .andExpect(jsonPath("$.data[1].presenter.name").value("테스트"));

        verify(lectureService, times(1)).findAllAvailableBy(lectureDate);
    }

    @Test
    @DisplayName("강의에 성공적으로 등록하고 등록 정보를 반환한다")
    void shouldRegisterLectureAndReturnRegistrationInfo() throws Exception {
        // given
        LocalDateTime registeredAt = LocalDateTime.of(2024, 12, 19, 10, 0, 0);
        LectureRegisterRequest request = new LectureRegisterRequest(1L, registeredAt);

        LectureRegister lectureRegister = LectureRegister.builder()
                .id(100L)
                .userId(1L)
                .lectureId(1L)
                .registeredAt(registeredAt)
                .build();

        when(lectureService.register(any())).thenReturn(lectureRegister);

        // when & then
        mockMvc.perform(post("/api/v1/lectures/{lectureId}/registrations", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.lectureId").value(1L))
                .andExpect(jsonPath("$.data.registeredAt").value("2024-12-19T10:00:00"));

        verify(lectureService, times(1)).register(any());
    }
}
