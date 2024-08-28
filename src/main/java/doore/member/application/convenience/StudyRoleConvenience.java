package doore.member.application.convenience;

import static doore.member.domain.StudyRoleType.*;

import doore.member.domain.StudyRole;
import doore.member.domain.repository.StudyRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoleConvenience {
    private final StudyRoleRepository studyRoleRepository;

    public void assignStudyLeaderRole(final Long studyId, final Long memberId) {
        studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .studyId(studyId)
                .memberId(memberId)
                .build());
    }

    public Long findStudyLeaderId(final Long studyId) {
        return studyRoleRepository.findLeaderIdByStudyId(studyId);
    }
}
