package com.justin.clean.storage;

import com.justin.clean.domain.LectureRegister;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRegisterJpaRepository extends JpaRepository<LectureRegister, Long> {

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    @EntityGraph(attributePaths = {"lecture", "lecture.presenter"})
    List<LectureRegister> findAllByUserId(long userId);
}
