package doore.document.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import doore.helper.RepositorySliceTest;
import doore.member.domain.repository.MemberRepository;
import doore.team.domain.repository.TeamRepository;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentRepositoryTest extends RepositorySliceTest {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;
    final Long teamId = 1L;

    @Test
    @DisplayName("특정 팀의 오늘 올라온 학습자료의 개수를 정상적으로 카운트 할 수 있다.")
    public void getTodayUploadedDocumentFromTeam_특정_팀의_오늘_올라온_학습자료의_개수를_정상적으로_카운트_할_수_있다_성공() throws Exception {
        //given
        Calendar cal = Calendar.getInstance();

        //when
        List<Document> documents = documentRepository.findAll();
        int countedDocument = documentRepository.getTodayUploadedDocumentFromTeam(teamId);
        System.out.println("======"+ documents.get(0).getCreatedAt());
        assertNotNull(documents.get(0).getCreatedAt());
        //then
        assertEquals(4,documents.size());
        if (cal.get(Calendar.DAY_OF_WEEK) == 2) {
            assertEquals(2,countedDocument);
            return;
        }
        assertEquals(1,countedDocument);
    }

    @Test
    @DisplayName("특정 팀의 이번주에 올라온 학습자료의 개수를 정상적으로 카운트 할 수 있다.")
    public void getWeekUploadedDocumentFromTeam_특정_팀의_이번주에_올라온_학습자료의_개수를_정상적으로_카운트_할_수_있다_성공() throws Exception {
        //when
        List<Document> documents = documentRepository.findAll();
        int countedDocument = documentRepository.getTodayUploadedDocumentFromTeam(teamId);

        //then
        assertEquals(4,documents.size());
        assertEquals(2,countedDocument);
    }
}
