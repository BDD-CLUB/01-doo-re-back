package doore.study.application;

import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.study.domain.StudyStatus.UPCOMING;
import static doore.study.exception.StudyExceptionType.*;

import doore.member.exception.MemberException;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.domain.StudyStatus;
import doore.study.exception.StudyException;
import doore.team.domain.TeamRepository;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.team.exception.TeamException;
import doore.team.exception.TeamExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyCommandService {
    private final StudyRepository studyRepository;
    private final TeamRepository teamRepository;
    private final CurriculumItemRepository curriculumItemRepository;

    public void createStudy(final StudyCreateRequest request, final Long teamId) {
        validateExistTeam(teamId);
        checkEndDateValid(request.startDate(), request.endDate());
        Study study = studyRepository.save(toStudyWithoutCurriculum(request, teamId));
        List<CurriculumItem> curriculumItems = toCurriculumList(request, study);
        curriculumItemRepository.saveAll(curriculumItems);
    }

    private void checkEndDateValid(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new StudyException(INVALID_ENDDATE);
        }
    }

    public Study toStudyWithoutCurriculum(StudyCreateRequest request, Long teamId) {
        return Study.builder()
                .name(request.name())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(UPCOMING)
                .isDeleted(false)
                .teamId(teamId)
                .cropId(request.cropId())
                .build();
    }

    public List<CurriculumItem> toCurriculumList(StudyCreateRequest request, Study study) {
        return request.curriculumItems().stream()
                .map(curriculumItemsRequest -> extractCurriculumItemFromStudy(curriculumItemsRequest, study))
                .toList();
    }

    public CurriculumItem extractCurriculumItemFromStudy(CurriculumItemRequest request, Study study) {
        return CurriculumItem.builder()
                .name(request.name())
                .study(study)
                .build();
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
