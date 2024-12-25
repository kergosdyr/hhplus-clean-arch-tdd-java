package com.justin.clean.app;

import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureRegisterValidator {

    private final LectureRegisterRepository lectureRegisterRepository;

    public void validate(long userId, long lectureId) {
        if (lectureRegisterRepository.existsById(userId, lectureId)) {
            throw new ApiException(ErrorType.DUPLICATE_REGISTER_ERROR);
        }
    }
}
