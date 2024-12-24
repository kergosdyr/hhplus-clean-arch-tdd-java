package com.justin.clean.web;

import com.justin.clean.app.LectureService;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.web.request.LectureRegisterRequest;
import com.justin.clean.web.response.LectureRegisterResponse;
import com.justin.clean.web.response.LectureResponse;
import com.justin.clean.web.support.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @GetMapping("/api/v1/lectures")
    public ApiResponse<List<LectureResponse>> getLectureAvailable(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var lectures = lectureService.findAllAvailableBy(date);
        return ApiResponse.success(LectureResponse.fromEntities(lectures));
    }

    @PostMapping("/api/v1/lectures/{lectureId}/registrations")
    public ApiResponse<LectureRegisterResponse> registerLecture(
            @PathVariable long lectureId, @RequestBody LectureRegisterRequest request) {
        LectureRegister lectureRegister = lectureService.register(request.toEntity(lectureId));
        return ApiResponse.success(LectureRegisterResponse.fromEntity(lectureRegister));
    }
}
