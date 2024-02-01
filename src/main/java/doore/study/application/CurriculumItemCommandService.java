package doore.study.application;

import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.CurriculumItemException;
import doore.study.exception.StudyException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CurriculumItemCommandService {

    private final CurriculumItemRepository curriculumItemRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final StudyRepository studyRepository;
    private final ParticipantRepository participantRepository;
    private final AtomicInteger index = new AtomicInteger(1);

    public void createCurriculum(CurriculumItemRequest request, Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);

        CurriculumItem curriculumItem = CurriculumItem.builder()
                .name(request.name())
                .itemOrder(index.getAndIncrement())
                .study(study)
                .build();
        curriculumItemRepository.save(curriculumItem);

        List<ParticipantCurriculumItem> participantCurriculumItems = participants.stream()
                .map(participant -> ParticipantCurriculumItem.builder()
                        .curriculumItem(curriculumItem)
                        .participantId(participant.getId())
                        .build())
                .toList();
        participantCurriculumItemRepository.saveAll(participantCurriculumItems);
    }

    public void deleteCurriculum(Long curriculumId, Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));

        int deletedOrder = curriculumItem.getItemOrder();
        curriculumItemRepository.delete(curriculumItem);

        List<CurriculumItem> subsequentItems = curriculumItemRepository.findByItemOrderGreaterThan(deletedOrder);
        for (CurriculumItem item : subsequentItems) {
            item.updateOrder(item.getItemOrder() - 1);
        }
    }

    public void updateCurriculum(Long curriculumId, Long studyId, CurriculumItemRequest request) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        curriculumItem.update(request.name());
    }

    public void checkCurriculum(Long curriculumId, Long studyId) {
        studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        ParticipantCurriculumItem participantCurriculumItem = participantCurriculumItemRepository.findById(
                curriculumItem.getId()).orElseThrow();
        if (participantCurriculumItem.getIsChecked().equals(false)) participantCurriculumItem.complete();
        else participantCurriculumItem.incomplete();
    }

}
