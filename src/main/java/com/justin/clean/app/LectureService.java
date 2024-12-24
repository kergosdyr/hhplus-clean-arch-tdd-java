package com.justin.clean.app;

import com.justin.clean.domain.Lecture;
import com.justin.clean.domain.LectureRegister;
import com.justin.clean.error.ApiException;
import com.justin.clean.error.ErrorType;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRegisterSaver lectureRegisterSaver;

    private final LectureRegisterLoader lectureRegisterLoader;

    private final LectureLoader lectureLoader;

    @Transactional
    public LectureRegister register(LectureRegister lectureRegister) {

        Lecture lecture = lectureLoader.load(lectureRegister.getLectureId());

        if (lectureRegisterLoader.hasDuplicateRegisterBy(lectureRegister.getUserId(), lectureRegister.getLectureId())) {
            throw new ApiException(ErrorType.DUPLICATE_REGISTER_ERROR);
        }

        if (lecture.isAttendNotAvailable() || lecture.isLectureExpired(lectureRegister.getRegisteredAt())) {
            throw new ApiException(ErrorType.REGISTER_OVER_ERROR);
        }

        var completedRegister = lectureRegisterSaver.save(lectureRegister);
        lecture.increaseAttendee();

        return completedRegister;
    }

    @Transactional(readOnly = true)
    public List<Lecture> findAllRegisteredBy(long userId) {
        return lectureLoader.loadRegistered(userId);
    }

    @Transactional(readOnly = true)
    public List<Lecture> findAllAvailableBy(LocalDate lectureDate) {
        return lectureLoader.loadAllAvailableBy(lectureDate);
    }
}
