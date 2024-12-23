package com.justin.clean.storage;

import com.justin.clean.domain.Lecture;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {


    @Query("select l from Lecture l where l.id = ?1")
    Optional<Lecture> findByIdWithLock(long id);

    @EntityGraph(attributePaths = {"presenter"})
    List<Lecture> findAllByLectureDateAndAttendeeCountIsLessThan(LocalDate lectureDate, int attendeeCount);
}
