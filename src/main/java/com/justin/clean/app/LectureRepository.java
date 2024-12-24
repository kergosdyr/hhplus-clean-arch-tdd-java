package com.justin.clean.app;

import com.justin.clean.domain.Lecture;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public interface LectureRepository {

    Optional<Lecture> findById(Long id);

    List<Lecture> findAllAvailableByLectureDate(LocalDate lectureDate);
}
