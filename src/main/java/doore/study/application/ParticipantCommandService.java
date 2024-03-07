package doore.study.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantCommandService {
    private final StudyRepository studyRepository;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    public void saveParticipant(Long studyId, Long memberId) {
        validateExistStudy(studyId);
        Member member = validateExistMember(memberId);
        Participant participant = Participant.builder()
                .studyId(studyId)
                .member(member)
                .build();
        participantRepository.save(participant);
    }

    public void deleteParticipant(Long studyId, Long memberId) {
        validateExistStudy(studyId);
        Member member = validateExistMember(memberId);
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }

    public void withdrawParticipant(Long studyId, HttpServletRequest request) {
        validateExistStudy(studyId);
        String memberId = request.getHeader("Authorization");
        if (memberId == null) {
            throw new MemberException(UNAUTHORIZED);
        }
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        participantRepository.deleteByStudyIdAndMember(studyId, member);
    }

    private void validateExistStudy(Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
    }

    private Member validateExistMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
