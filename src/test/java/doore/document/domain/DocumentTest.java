package doore.document.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.document.DocumentFixture;
import doore.document.domain.repository.DocumentRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentTest {

    @Autowired
    DocumentRepository documentRepository;

    @Test
    @DisplayName("[성공] 학습자료가 정상적으로 수정된다.")
    public void 학습자료가_정상적으로_수정된다_성공() {
        //given
        Document document = new DocumentFixture()
                .buildDocument();

        //when
        String newName = "수정된 자료명";
        String newDescription = "수정된 설명";
        DocumentAccessType newAccessType = DocumentAccessType.ALL;
        document.update(newName, newDescription, newAccessType);

        //then
        assertAll(
                () -> assertEquals(newName, document.getName()),
                () -> assertEquals(newDescription, document.getDescription()),
                () -> assertEquals(newAccessType, document.getAccessType())
        );
    }

    @Test
    @DisplayName("[성공] 학습자료의 파일이 정상적으로 수정된다.")
    public void 학습자료의_파일이_정상적으로_수정된다_성공() {
        //given
        String url = "blog link";
        Document document = new DocumentFixture()
                .buildLinkDocument(List.of(url));

        //when
        String newUrl = "new link";
        File newFile = File.builder().url(newUrl).document(document).build();
        document.updateFiles(List.of(newFile));

        //then
         assertEquals(newFile, document.getFiles().get(0));
    }
}
