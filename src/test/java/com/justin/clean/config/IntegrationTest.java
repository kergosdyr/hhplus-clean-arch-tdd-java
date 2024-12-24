package com.justin.clean.config;

import com.justin.clean.app.LectureLoader;
import com.justin.clean.app.LectureRegisterLoader;
import com.justin.clean.app.LectureRegisterRepository;
import com.justin.clean.app.LectureRegisterSaver;
import com.justin.clean.app.LectureRepository;
import com.justin.clean.app.LectureService;
import com.justin.clean.storage.LectureJpaRepository;
import com.justin.clean.storage.LectureRegisterJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    protected LectureService lectureService;

    @Autowired
    protected LectureLoader lectureLoader;

    @Autowired
    protected LectureRegisterLoader lectureRegisterLoader;

    @Autowired
    protected LectureRegisterSaver lectureRegisterSaver;

    @Autowired
    protected LectureRegisterRepository lectureRegisterRepository;

    @Autowired
    protected LectureRepository lectureRepository;

    @Autowired
    protected LectureJpaRepository lectureJpaRepository;

    @Autowired
    protected LectureRegisterJpaRepository lectureRegisterJpaRepository;

    @BeforeEach
    void setUp() {
        lectureJpaRepository.deleteAllInBatch();
        lectureRegisterJpaRepository.deleteAllInBatch();
    }
}
