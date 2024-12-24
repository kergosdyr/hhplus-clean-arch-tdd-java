package com.justin.clean.app;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureRegisterLoader {

    private final LectureRegisterRepository lectureRegisterRepository;

    public boolean hasDuplicateRegisterBy(long userId, long lectureId) {
        return lectureRegisterRepository.existsById(userId, lectureId);
    }
}
