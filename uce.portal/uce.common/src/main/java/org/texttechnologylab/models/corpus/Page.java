package org.texttechnologylab.models.corpus;

import org.texttechnologylab.models.UIMAAnnotation;
import org.texttechnologylab.utils.StringUtils;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "page")
public class Page extends UIMAAnnotation {
    private int pageNumber;
    private String pageId;
    @Column(name = "document_id", insertable = false, updatable = false)
    private long document_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_Id")
    private List<Paragraph> paragraphs;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_Id")
    private List<Block> blocks;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_Id")
    private List<Line> lines;

    @OneToOne(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "page_id")
    private PageTopicDistribution pageTopicDistribution;

    public Page(int begin, int end, int pageNumber, String pageId) {
        super(begin, end);
        this.pageNumber = pageNumber;
        this.pageId = pageId;
    }

    public Page() {
        super(-1, -1);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public long getDocumentId() {
        return this.document_id;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public PageTopicDistribution getPageTopicDistribution() {
        return pageTopicDistribution;
    }

    public void setPageTopicDistribution(PageTopicDistribution pageTopicDistributions) {
        this.pageTopicDistribution = pageTopicDistributions;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Paragraph> getParagraphs() {
        try {
            return paragraphs;
        } catch (Exception ex) {
            // I have no idea why, but sometimes, the lazy load of empty paragraphs throws an error.
            // There is nothing wrong with the document or page - it just throws an error here.
            // It's not a problem if the paragraphs are empty! So we just catch the error and return empty...
            // It's also not worth logging to the DB or file logger.
            System.err.println("Opened a document with unloadable lazy paragraphs.");
            return new ArrayList<>();
        }
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public String getPageId() {
        return pageId;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
