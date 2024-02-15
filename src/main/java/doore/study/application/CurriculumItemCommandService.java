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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
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
        checkItemOrderDuplicate(curriculumItems);
        checkItemOrderRange(curriculumItems);
        createCurriculum(studyId, curriculumItems);
        updateCurriculum(curriculumItems);

        List<CurriculumItem> deletedCurriculumItems = request.deletedCurriculumItems();
        deleteCurriculum(deletedCurriculumItems);
        sortCurriculum();
    }

    public void checkCurriculum(Long participantId) {
        ParticipantCurriculumItem participantCurriculumItem = participantCurriculumItemRepository.findByParticipantId(
                participantId).orElseThrow(() -> new StudyException(NOT_FOUND_PARTICIPANT));

        participantCurriculumItem.checkCompletion();
    }

    private void checkItemOrderDuplicate(List<CurriculumItem> curriculumItems) {
        Set<Integer> uniqueItemOrders = new HashSet<>();

        curriculumItems.stream()
                .map(CurriculumItem::getItemOrder)
                .forEach(itemOrder -> {
                    if (!uniqueItemOrders.add(itemOrder)) {
                        throw new CurriculumItemException(INVALID_ITEM_ORDER);
                    }
                });
    }

    private void checkItemOrderRange(List<CurriculumItem> curriculumItems) {
        curriculumItems.stream()
                .mapToInt(CurriculumItem::getItemOrder)
                .forEach(itemOrder -> {
                    if (itemOrder < Integer.MIN_VALUE || itemOrder > Integer.MAX_VALUE) {
                        throw new CurriculumItemException(INVALID_ITEM_ORDER);
                    }
                });
    }

    private void createCurriculum(Long studyId, List<CurriculumItem> curriculumItems) {
        curriculumItems.stream()
                .filter(curriculumItem -> !isExistsCurriculumItem(curriculumItem.getId()))
                .forEach(curriculumItem -> createCurriculumItemAndAssignToParticipants(studyId, curriculumItem));
    }

    private boolean isExistsCurriculumItem(Long curriculumItemId) {
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

            existingItem.updateIfNameDifferent(requestItem);
            existingItem.updateIfItemOrderDifferent(requestItem);
        }
    }

    private void deleteCurriculum(List<CurriculumItem> deletedCurriculumItems) {
        deletedCurriculumItems.stream()
                .map(CurriculumItem::getId)
                .forEach(curriculumItemRepository::deleteById);
    }

    private void sortCurriculum() {
        List<CurriculumItem> sortedCurriculum = curriculumItemRepository.findAllByOrderByItemOrderAsc();

        IntStream.range(1, sortedCurriculum.size())
                .forEach(i -> sortedCurriculum.get(i).updateItemOrder(i + 1));
        curriculumItemRepository.saveAll(sortedCurriculum);
    }
}
