package com.justin.clean.app;

import com.justin.clean.domain.LectureRegister;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface LectureRegisterRepository {

    LectureRegister save(LectureRegister lectureRegister);

    boolean existsById(long userId, long lectureId);

    List<LectureRegister> findAllBy(long userId);
}
