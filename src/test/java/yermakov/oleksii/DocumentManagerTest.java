package yermakov.oleksii;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
        DocumentManager.documentStorage.clear();
    }

    @Test
    public void testSaveDocumentWithGeneratedId() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals(savedDocument, DocumentManager.documentStorage.get(savedDocument.getId()));
    }

    @Test
    public void testSaveDocumentWithExistingId() {
        String id = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(id)
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertEquals(id, savedDocument.getId());
        assertEquals(savedDocument, DocumentManager.documentStorage.get(id));
    }

    @Test
    public void testFindByIdDocumentExists() {
        String id = UUID.randomUUID().toString();
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(id)
                .title("Test Title")
                .content("Test Content")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now())
                .build();

        documentManager.save(document);

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(id);

        assertTrue(foundDocument.isPresent());
        assertEquals(document, foundDocument.get());
    }

    @Test
    public void testFindByIdDocumentNotExists() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(UUID.randomUUID().toString());

        assertFalse(foundDocument.isPresent());
    }

    @Test
    public void testSearchByTitlePrefix() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Title 1")
                .content("Content 1")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Example Title 2")
                .content("Content 2")
                .author(DocumentManager.Author.builder().id("2").name("Author 2").build())
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Test"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(1, result.size());
        assertEquals(document1, result.get(0));
    }

    @Test
    public void testSearchByAuthorId() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Title 1")
                .content("Content 1")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Example Title 2")
                .content("Content 2")
                .author(DocumentManager.Author.builder().id("2").name("Author 2").build())
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("2"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(1, result.size());
        assertEquals(document2, result.get(0));
    }

    @Test
    public void testSearchByCreatedDateRange() {
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Title 1")
                .content("Content 1")
                .author(DocumentManager.Author.builder().id("1").name("Author 1").build())
                .created(Instant.now().minusSeconds(3600))
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id(UUID.randomUUID().toString())
                .title("Example Title 2")
                .content("Content 2")
                .author(DocumentManager.Author.builder().id("2").name("Author 2").build())
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .createdFrom(Instant.now().minusSeconds(1800))
                .createdTo(Instant.now().plusSeconds(1800))
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(1, result.size());
        assertEquals(document2, result.get(0));
    }
}