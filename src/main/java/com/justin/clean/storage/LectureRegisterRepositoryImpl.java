package com.justin.clean.storage;

import com.justin.clean.app.LectureRegisterRepository;
import com.justin.clean.domain.LectureRegister;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class LectureRegisterRepositoryImpl implements LectureRegisterRepository {

    private final LectureRegisterJpaRepository lectureRegisterJpaRepository;

    @Override
    public LectureRegister save(LectureRegister lectureRegister) {
        return lectureRegisterJpaRepository.save(lectureRegister);
    }

    @Override
    public boolean existsById(long userId, long lectureId) {
        return lectureRegisterJpaRepository.existsByUserIdAndLectureId(userId, lectureId);
    }

    @Override
    public List<LectureRegister> findAllBy(long userId) {
        return lectureRegisterJpaRepository.findAllByUserId(userId);
    }
}
