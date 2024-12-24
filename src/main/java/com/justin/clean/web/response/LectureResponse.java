package com.justin.clean.web.response;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.Presenter;
import java.util.List;
import java.util.stream.Collectors;

public record LectureResponse(Long lectureId, String title, PresenterResponse presenter) {

    public static LectureResponse fromEntity(Lecture lecture) {

        Presenter presenterEntity = lecture.getPresenter();

        return new LectureResponse(
                lecture.getId(),
                lecture.getTitle(),
                new PresenterResponse(presenterEntity.getName(), presenterEntity.getAge(), presenterEntity.getEmail()));
    }

    public static List<LectureResponse> fromEntities(List<Lecture> lectures) {
        return lectures.stream().map(LectureResponse::fromEntity).collect(Collectors.toList());
    }
}
