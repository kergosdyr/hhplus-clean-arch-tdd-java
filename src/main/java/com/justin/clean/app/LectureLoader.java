package com.justin.clean.app;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureLoader {

    private final LectureRepository lectureRepository;

    private final LectureRegisterRepository lectureRegisterRepository;

    public Lecture load(long id) {
        return lectureRepository.findById(id).orElseThrow(() -> new ApiException(ErrorType.DEFAULT_ERROR));
    }

    public List<Lecture> loadRegistered(long userId) {
        return lectureRegisterRepository.findAllBy(userId).stream()
                .map(LectureRegister::getLecture)
                .toList();
    }

    public List<Lecture> loadAllAvailableBy(LocalDate lectureDate) {
        return lectureRepository.findAllAvailableByLectureDate(lectureDate);
    }
}
