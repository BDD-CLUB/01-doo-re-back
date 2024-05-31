package doore.study.application;

import static doore.study.exception.StudyExceptionType.*;

import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.domain.StudyStatus;
import doore.study.exception.StudyException;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import doore.team.exception.TeamExceptionType;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommandService {
    private final StudyRepository studyRepository;
    private final TeamRepository teamRepository;

    public void createStudy(final StudyCreateRequest request, final Long teamId) {
        validateExistTeam(teamId);
        checkEndDateValid(request.startDate(), request.endDate());
        studyRepository.save(request.toStudy(teamId));
    }

    private void checkEndDateValid(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new StudyException(INVALID_ENDDATE);
        }
    }

    public void deleteStudy(Long studyId) {
        validateExistStudy(studyId);
        studyRepository.deleteById(studyId);
    }

    public void updateStudy(StudyUpdateRequest request, Long studyId) {
        Study study = validateExistStudy(studyId);
        study.update(request.name(), request.description(), request.startDate(), request.endDate(), request.status());
    }

    public void terminateStudy(Long studyId) {
        Study study = validateExistStudy(studyId);
        study.terminate();
    }

    public void changeStudyStatus(String status, Long studyId) {
        Study study = validateExistStudy(studyId);
        try {
            StudyStatus changedStatus = StudyStatus.valueOf(status);
            study.changeStatus(changedStatus);
        } catch (IllegalArgumentException e) {
            throw new StudyException(NOT_FOUND_STATUS);
        }
    }

    private void validateExistTeam(Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
    }

    private Study validateExistStudy(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }
}
