package doore.study.application;

import static doore.study.exception.CurriculumItemExceptionType.INVALID_ITEM_ORDER;
import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_PARTICIPANT;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.CurriculumItemException;
import doore.study.exception.StudyException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public void manageCurriculum(CurriculumItemManageRequest request, Long studyId) {
        List<CurriculumItem> curriculumItems = request.curriculumItems();
        validItemOrderDuplicateCheck(curriculumItems);
        validItemOrderRangeCheck(curriculumItems);
        createCurriculum(studyId, curriculumItems);
        updateCurriculum(curriculumItems);

        List<CurriculumItem> deletedCurriculumItems = request.deletedCurriculumItems();
        deleteCurriculum(deletedCurriculumItems);
        sortCurriculum();
    }

    public void checkCurriculum(Long curriculumId) {
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        ParticipantCurriculumItem participantCurriculumItem = participantCurriculumItemRepository.findById(
                curriculumItem.getId()).orElseThrow(() -> new StudyException(NOT_FOUND_PARTICIPANT));
        if (participantCurriculumItem.getIsChecked().equals(false)) {
            participantCurriculumItem.complete();
        } else {
            participantCurriculumItem.incomplete();
        }
    }

    private void validItemOrderDuplicateCheck(List<CurriculumItem> curriculumItems) {
        Set<Integer> uniqueItemOrders = new HashSet<>();

        for (CurriculumItem item : curriculumItems) {
            int itemOrder = item.getItemOrder();
            if (!uniqueItemOrders.add(itemOrder)) {
                throw new CurriculumItemException(INVALID_ITEM_ORDER);
            }
        }
    }

    private void validItemOrderRangeCheck(List<CurriculumItem> curriculumItems) {
        for (CurriculumItem item : curriculumItems) {
            int itemOrder = item.getItemOrder();
            if (itemOrder < Integer.MIN_VALUE || itemOrder > Integer.MAX_VALUE) {
                throw new CurriculumItemException(INVALID_ITEM_ORDER);
            }
        }
    }

    private void createCurriculum(Long studyId, List<CurriculumItem> curriculumItems) {
        curriculumItems.stream()
                .filter(curriculumItem -> !existsCurriculumItem(curriculumItem.getId()))
                .forEach(curriculumItem -> createCurriculumItemAndAssignToParticipants(studyId, curriculumItem));
    }

    private boolean existsCurriculumItem(Long curriculumItemId) {
        return curriculumItemRepository.existsById(curriculumItemId);
    }

    public void createCurriculumItemAndAssignToParticipants(Long studyId, CurriculumItem curriculumItem) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);

        CurriculumItem createCurriculumItem = CurriculumItem.builder()
                .name(curriculumItem.getName())
                .itemOrder(curriculumItem.getItemOrder())
                .study(study)
                .build();
        curriculumItemRepository.save(createCurriculumItem);

        List<ParticipantCurriculumItem> participantCurriculumItems = participants.stream()
                .map(participant -> ParticipantCurriculumItem.builder()
                        .curriculumItem(createCurriculumItem)
                        .participantId(participant.getId())
                        .build())
                .toList();
        participantCurriculumItemRepository.saveAll(participantCurriculumItems);
    }

    private void updateCurriculum(List<CurriculumItem> curriculumItems) {
        for (CurriculumItem requestItem : curriculumItems) {
            CurriculumItem existingItem = curriculumItemRepository.findById(requestItem.getId())
                    .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));

            if (!existingItem.getName().equals(requestItem.getName())) {
                existingItem.updateName(requestItem.getName());
            }

            if (!existingItem.getItemOrder().equals(requestItem.getItemOrder())) {
                existingItem.updateItemOrder(requestItem.getItemOrder());
            }
        }
    }

    private void deleteCurriculum(List<CurriculumItem> deletedCurriculumItems) {
        for (CurriculumItem requestItem : deletedCurriculumItems) {
            curriculumItemRepository.deleteById(requestItem.getId());
        }
    }

    private void sortCurriculum() {
        List<CurriculumItem> sortedCurriculum = curriculumItemRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(CurriculumItem::getItemOrder))
                .toList();

        for (int i = 0; i < sortedCurriculum.size(); i++) {
            CurriculumItem item = sortedCurriculum.get(i);
            item.updateItemOrder(i + 1);
        }
        curriculumItemRepository.saveAll(sortedCurriculum);
    }
}
