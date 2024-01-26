package doore.study.application;

import static doore.study.domain.StudyStatus.IN_PROGRESS;
import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;

import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.CurriculumItemRepository;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyRepository;
import doore.study.exception.CurriculumItemException;
import java.time.LocalDate;
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
    private final StudyRepository studyRepository; // 임시
    private final AtomicInteger index = new AtomicInteger(1);

    public void createCurriculum(CurriculumItemRequest request, Long studyId) {
//        Study study = studyRepository.findById(studyId).orElseThrow(); // 임시. 스터디 코드 완성 후 예외처리 할 부분
        //**********임시*********
        Study study = Study.builder()
                .name("123")
                .description("123")
                .startDate(LocalDate.now())
                .status(IN_PROGRESS)
                .isDeleted(false)
                .teamId(1L)
                .cropId(1L)
                .build();
        studyRepository.save(study);
        //************************
        CurriculumItem curriculumItem = CurriculumItem.builder()
                .name(request.name()).itemOrder(index.getAndIncrement()).isDeleted(false).study(study).build();
        curriculumItemRepository.save(curriculumItem);
    }

    public void deleteCurriculum(Long curriculumId, Long studyId) {
//        Study study = studyRepository.findById(studyId).orElseThrow();  // 임시. 스터디 코드 완성 후 예외처리 할 부분
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
//        Study study = studyRepository.findById(studyId).orElseThrow();  // 임시. 스터디 코드 완성 후 예외처리 할 부분
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        curriculumItem.update(request.name());
    }

    public void completeCurriculum(Long curriculumId, Long studyId) {
//        Study study = studyRepository.findById(studyId).orElseThrow();  // 임시. 스터디 코드 완성 후 예외처리 할 부분
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .isChecked(false)
                .isDeleted(false)
                .participantId(1L) // 임시. url을 수정하거나, 다른 정보로 받아오거나
                .curriculumItem(curriculumItem)
                .build();
        participantCurriculumItem.complete();
    }

    public void incompleteCurriculum(Long curriculumId, Long studyId) {
//        Study study = studyRepository.findById(studyId).orElseThrow();  // 임시. 스터디 코드 완성 후 예외처리 할 부분
        CurriculumItem curriculumItem = curriculumItemRepository.findById(curriculumId)
                .orElseThrow(() -> new CurriculumItemException(NOT_FOUND_CURRICULUM_ITEM));
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .isChecked(true)
                .isDeleted(false)
                .participantId(1L) // 임시. url을 수정하거나, 다른 정보로 받아오거나
                .curriculumItem(curriculumItem)
                .build();
        participantCurriculumItem.incomplete();
    }
}
