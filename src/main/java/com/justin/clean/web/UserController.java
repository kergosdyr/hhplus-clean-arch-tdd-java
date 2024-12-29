package com.justin.clean.web;

import com.justin.clean.app.LectureService;
import com.justin.clean.web.response.LectureResponse;
import com.justin.clean.web.support.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final LectureService lectureService;

    @GetMapping("/api/v1/users/{userId}/lectures")
    public ApiResponse<List<LectureResponse>> getRegisteredLecture(@PathVariable long userId) {

        var registeredLectures = lectureService.findAllRegisteredBy(userId);

        return ApiResponse.success(LectureResponse.fromEntities(registeredLectures));
    }
}
