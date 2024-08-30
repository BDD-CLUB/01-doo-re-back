package doore.study.application;

import static doore.study.exception.CurriculumItemExceptionType.CANNOT_CREATE_CURRICULUM_ITEM;
import static doore.study.exception.CurriculumItemExceptionType.INVALID_ITEM_ORDER;
import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_PARTICIPANT;

import doore.garden.application.convenience.GardenConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.member.domain.Participant;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.convenience.StudyAuthorization;
import doore.study.application.convenience.StudyConvenience;
import doore.study.application.dto.request.CurriculumItemManageDetailRequest;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
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
    private final ParticipantRepository participantRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final StudyConvenience studyConvenience;
    private final StudyAuthorization studyAuthorization;
    private final GardenConvenience gardenConvenience;
    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;

    public void manageCurriculum(final CurriculumItemManageRequest request, final Long studyId, final Long memberId) {
        studyRoleValidateAccessPermission.validateExistStudyLeader(studyId, memberId);
        final List<CurriculumItemManageDetailRequest> curriculumItems = request.curriculumItems();
        if (curriculumItems.size() >= 99) {
            throw new CurriculumItemException(CANNOT_CREATE_CURRICULUM_ITEM);
        }

        checkItemOrderDuplicate(curriculumItems);
        curriculumItems.forEach(item -> createOrUpdateCurriculum(studyId, item));

        final List<CurriculumItemManageDetailRequest> deletedCurriculumItems = request.deletedCurriculumItems();
        deleteCurriculum(deletedCurriculumItems);
        sortCurriculum();
    }

    public void checkCurriculum(final Long curriculumId, final Long participantId, final Long memberId) {
        final Study study = studyConvenience.findByCurriculumItemId(curriculumId);
        studyRoleValidateAccessPermission.validateExistParticipant(study.getId(), memberId);
        final CurriculumItem curriculumItem = getCurriculumItemOrThrow(curriculumId);
        final Participant participant = getParticipantOrThrow(participantId);
        final ParticipantCurriculumItem participantCurriculumItem = getParticipantCurriculumItemOrThrow(curriculumItem,
                participant);

        participantCurriculumItem.checkCompletion();
        handleGardenBasedOnCompletionStatus(participantCurriculumItem);
    }

    private void handleGardenBasedOnCompletionStatus(final ParticipantCurriculumItem participantCurriculumItem) {
        if (participantCurriculumItem.getIsChecked()) {
            gardenConvenience.createCurriculumGarden(participantCurriculumItem);
            return;
        }
        gardenConvenience.deleteCurriculumGarden(participantCurriculumItem);
    }

    private void checkItemOrderDuplicate(final List<CurriculumItemManageDetailRequest> curriculumItems) {
        final Set<Integer> uniqueItemOrders = new HashSet<>();

        curriculumItems.stream()
                .map(CurriculumItemManageDetailRequest::itemOrder)
                .forEach(itemOrder -> {
                    if (!uniqueItemOrders.add(itemOrder)) {
                        throw new CurriculumItemException(INVALID_ITEM_ORDER);
                    }
                });
    }

    private void createOrUpdateCurriculum(final Long studyId, final CurriculumItemManageDetailRequest curriculumItem) {
        if (curriculumItem.id() == null) {
            createCurriculum(studyId, curriculumItem); // 생성
            return;
        }
        updateCurriculum(curriculumItem); //수정
    }

    public void createCurriculum(final Long studyId, final CurriculumItemManageDetailRequest curriculumItemRequest) {
        final Study study = studyAuthorization.getStudyOrThrow(studyId);
        final CurriculumItem curriculumItem = CurriculumItem.builder()
                .name(curriculumItemRequest.name())
                .itemOrder(curriculumItemRequest.itemOrder())
                .study(study)
                .build();
        curriculumItemRepository.save(curriculumItem);

        final List<Participant> participants = participantRepository.findAllByStudyId(studyId);
        final List<ParticipantCurriculumItem> participantCurriculumItems = participants.stream()
                .map(participant -> createParticipantCurriculumItems(curriculumItem, participant)).toList();
        participantCurriculumItemRepository.saveAll(participantCurriculumItems);
    }

    private ParticipantCurriculumItem createParticipantCurriculumItems(final CurriculumItem curriculumItem,
                                                                       final Participant participant) {
        return ParticipantCurriculumItem.builder()
                .curriculumItem(curriculumItem)
                .participantId(participant.getId())
                .build();
    }

    private void updateCurriculum(final CurriculumItemManageDetailRequest curriculumItem) {
        final CurriculumItem existingItem = getCurriculumItemOrThrow(curriculumItem.id());
        if (existingItem.changed(curriculumItem.name(), curriculumItem.itemOrder())) {
            existingItem.update(curriculumItem.name(), curriculumItem.itemOrder());
        }
    }

    private void deleteCurriculum(final List<CurriculumItemManageDetailRequest> deletedCurriculumItems) {
        deletedCurriculumItems.stream()
                .map(CurriculumItemManageDetailRequest::id)
                .forEach(curriculumItemId -> {
                    participantCurriculumItemRepository.deleteAllByCurriculumItemId(curriculumItemId);
                    curriculumItemRepository.deleteById(curriculumItemId);
                });
    }

    private void sortCurriculum() {
        final List<CurriculumItem> sortedCurriculum = curriculumItemRepository.findAllByOrderByItemOrderAsc();

        IntStream.range(0, sortedCurriculum.size())
                .forEach(i -> sortedCurriculum.get(i).updateItemOrder(i + 1));
        curriculumItemRepository.saveAll(sortedCurriculum);
    }

    private CurriculumItem getCurriculumItemOrThrow(final Long curriculumId) {
        return curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
    }

    private Participant getParticipantOrThrow(final Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new StudyException(NOT_FOUND_PARTICIPANT));
    }

    private ParticipantCurriculumItem getParticipantCurriculumItemOrThrow(final CurriculumItem curriculumItem,
                                                                          final Participant participant) {
        return participantCurriculumItemRepository.findByCurriculumItemIdAndParticipantId(
                curriculumItem.getId(), participant.getId()).orElseThrow();
    }
}
