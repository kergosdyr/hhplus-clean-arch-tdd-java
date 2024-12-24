package com.justin.clean.storage;

import com.justin.clean.app.LectureRepository;
import com.justin.clean.domain.Lecture;
import com.justin.clean.enums.Attendee;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class LectureRepositoryImpl implements LectureRepository {

    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public Optional<Lecture> findById(Long id) {
        return lectureJpaRepository.findByIdWithLock(id);
    }

    @Override
    public List<Lecture> findAllAvailableByLectureDate(LocalDate lectureDate) {
        return lectureJpaRepository.findAllByLectureDateAndAttendeeCountIsLessThan(
                lectureDate, Attendee.MAX.getCount());
    }
}
