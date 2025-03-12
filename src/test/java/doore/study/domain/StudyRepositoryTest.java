package doore.study.domain;

import doore.helper.RepositorySliceTest;
import doore.study.domain.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyRepositoryTest extends RepositorySliceTest {

    @Autowired
    private StudyRepository studyRepository;


}
