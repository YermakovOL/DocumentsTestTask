package yermakov.oleksii;

import lombok.Builder;
import lombok.Data;

import java.sql.ClientInfoStatus;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    public static final HashMap<String, Document> documentStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (isNull(document.getId()) || document.getId().isBlank()) {
            document.setId(UUID.randomUUID().toString());
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStorage.values().stream()
                .filter(document -> {
                    return Optional.ofNullable(request.getTitlePrefixes())
                            .map(titlePrefixes -> titlePrefixes.stream().anyMatch(prefix -> document.getTitle().startsWith(prefix)))
                            .orElse(true);
                })
                .filter(document -> {
                    return Optional.ofNullable(request.getContainsContents())
                            .map(containsContents -> containsContents.stream().anyMatch(content -> document.getContent().contains(content)))
                            .orElse(true);
                })
                .filter(document -> {
                    return Optional.ofNullable(request.getAuthorIds())
                            .map(authorIds -> authorIds.contains(document.getAuthor().getId()))
                            .orElse(true);
                })
                .filter(document -> {
                    return Optional.ofNullable(request.getCreatedFrom())
                            .map(createdFrom -> document.getCreated().isAfter(createdFrom))
                            .orElse(true);
                })
                .filter(document -> {
                    return Optional.ofNullable(request.getCreatedTo())
                            .map(createdTo -> document.getCreated().isBefore(createdTo))
                            .orElse(true);
                })
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return documentStorage.values().stream()
                .filter(document -> document.getId().equals(id))
                .findFirst();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}