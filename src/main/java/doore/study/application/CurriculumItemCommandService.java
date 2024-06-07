package doore.study.application;

import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.CurriculumItemExceptionType.CANNOT_CREATE_CURRICULUM_ITEM;
import static doore.study.exception.CurriculumItemExceptionType.INVALID_ITEM_ORDER;
import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_PARTICIPANT;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.application.dto.request.CurriculumItemManageDetailRequest;
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
    private final StudyRoleRepository studyRoleRepository;
    private final GardenRepository gardenRepository;

    public void manageCurriculum(CurriculumItemManageRequest request, Long studyId, Long memberId) {
        validateExistStudyLeader(memberId);
        List<CurriculumItemManageDetailRequest> curriculumItems = request.curriculumItems();
        checkItemOrderDuplicate(curriculumItems);
        checkItemOrderRange(curriculumItems);
        createCurriculum(studyId, curriculumItems);
        updateCurriculum(curriculumItems);

        List<CurriculumItemManageDetailRequest> deletedCurriculumItems = request.deletedCurriculumItems();
        deleteCurriculum(deletedCurriculumItems);
        sortCurriculum();
    }

    public void checkCurriculum(Long curriculumId, Long participantId, Long memberId) {
        validateExistStudyLeaderAndStudyMember(memberId);
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new StudyException(NOT_FOUND_PARTICIPANT));
        ParticipantCurriculumItem participantCurriculumItem = participantCurriculumItemRepository.findByCurriculumItemIdAndParticipantId(
                curriculumItem.getId(), participant.getId()).orElseThrow();

        participantCurriculumItem.checkCompletion();
        if (participantCurriculumItem.getIsChecked()) {
            createGarden(participantCurriculumItem);
            return;
        }
        deleteGarden(participantCurriculumItem);
    }

    private void createGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Garden garden = GardenType.getSupplierOf(participantCurriculumItem.getClass().getSimpleName())
                .of(participantCurriculumItem);
        gardenRepository.save(garden);
    }

    private void deleteGarden(ParticipantCurriculumItem participantCurriculumItem) {
        Long contributionId = participantCurriculumItem.getId();
        GardenType gardenType = GardenType.getGardenTypeOf(participantCurriculumItem.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }

    private void checkItemOrderDuplicate(List<CurriculumItemManageDetailRequest> curriculumItems) {
        Set<Integer> uniqueItemOrders = new HashSet<>();

        curriculumItems.stream()
                .map(CurriculumItemManageDetailRequest::itemOrder)
                .forEach(itemOrder -> {
                    if (!uniqueItemOrders.add(itemOrder)) {
                        throw new CurriculumItemException(INVALID_ITEM_ORDER);
                    }
                });
    }

    private void checkItemOrderRange(List<CurriculumItemManageDetailRequest> curriculumItems) {
        curriculumItems.stream()
                .mapToInt(CurriculumItemManageDetailRequest::itemOrder)
                .forEach(itemOrder -> {
                    if (itemOrder < Integer.MIN_VALUE || itemOrder > Integer.MAX_VALUE) {
                        throw new CurriculumItemException(INVALID_ITEM_ORDER);
                    }
                });
    }

    private void createCurriculum(Long studyId, List<CurriculumItemManageDetailRequest> curriculumItems) {
        if (curriculumItemRepository.findAll().size() >= 99) {
            throw new CurriculumItemException(CANNOT_CREATE_CURRICULUM_ITEM);
        }
        curriculumItems.stream()
                .filter(curriculumItem -> !isExistsCurriculumItem(curriculumItem.id()))
                .forEach(curriculumItem -> createCurriculumItemAndAssignToParticipants(studyId, curriculumItem));
    }

    private boolean isExistsCurriculumItem(Long curriculumItemId) {
        return curriculumItemRepository.existsById(curriculumItemId);
    }

    public void createCurriculumItemAndAssignToParticipants(Long studyId,
                                                            CurriculumItemManageDetailRequest curriculumItemRequest) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        List<Participant> participants = participantRepository.findAllByStudyId(studyId);

        CurriculumItem createCurriculumItem = CurriculumItem.builder()
                .name(curriculumItemRequest.name())
                .itemOrder(curriculumItemRequest.itemOrder())
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


    private void updateCurriculum(List<CurriculumItemManageDetailRequest> curriculumItems) {
        for (CurriculumItemManageDetailRequest requestItem : curriculumItems) {
            CurriculumItem existingItem = curriculumItemRepository.findById(requestItem.id())
                    .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));

            existingItem.updateIfNameDifferent(requestItem.name());
            existingItem.updateIfItemOrderDifferent(requestItem.itemOrder());
        }
    }

    private void deleteCurriculum(List<CurriculumItemManageDetailRequest> deletedCurriculumItems) {
        deletedCurriculumItems.stream()
                .map(CurriculumItemManageDetailRequest::id)
                .forEach(curriculumItemId -> {
                    participantCurriculumItemRepository.deleteAllByCurriculumItemId(curriculumItemId);
                    curriculumItemRepository.deleteById(curriculumItemId);
                });
    }

    private void sortCurriculum() {
        List<CurriculumItem> sortedCurriculum = curriculumItemRepository.findAllByOrderByItemOrderAsc();

        IntStream.range(1, sortedCurriculum.size())
                .forEach(i -> sortedCurriculum.get(i).updateItemOrder(i + 1));
        curriculumItemRepository.saveAll(sortedCurriculum);
    }

    private void validateExistStudyLeader(Long memberId) {
        StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!studyRole.getStudyRoleType().equals(ROLE_스터디장)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private void validateExistStudyLeaderAndStudyMember(Long memberId) {
        StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType().equals(ROLE_스터디장) || studyRole.getStudyRoleType().equals(ROLE_스터디원))) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
