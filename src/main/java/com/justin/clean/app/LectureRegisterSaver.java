package com.justin.clean.app;

import com.justin.clean.domain.LectureRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureRegisterSaver {

    private final LectureRegisterRepository lectureRegisterRepository;

    public LectureRegister save(LectureRegister lectureRegister) {
        return lectureRegisterRepository.save(lectureRegister);
    }
}
