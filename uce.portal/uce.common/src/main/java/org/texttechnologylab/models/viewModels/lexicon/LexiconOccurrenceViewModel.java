package org.texttechnologylab.models.viewModels.lexicon;

import org.texttechnologylab.models.UIMAAnnotation;
import org.texttechnologylab.models.corpus.Page;
import org.texttechnologylab.utils.StringUtils;

public class LexiconOccurrenceViewModel {

    private final UIMAAnnotation uimaAnnotation;
    private final Page page;
    private long corpusId;
    private final long documentId;
    private final String occurrenceSnippetHtml;

    public LexiconOccurrenceViewModel(UIMAAnnotation uimaAnnotation,
                                      Page page){
        this.uimaAnnotation = uimaAnnotation;
        this.page = page;
        this.documentId = uimaAnnotation.getDocumentId();
        // Calculate the snippetHTML for the UI
        this.occurrenceSnippetHtml = StringUtils.buildContextSnippet(page.getCoveredText(),
                uimaAnnotation.getBegin() - page.getBegin(), uimaAnnotation.getEnd() - page.getBegin(), 100);
    }

    public UIMAAnnotation getUimaAnnotation() {
        return uimaAnnotation;
    }

    public Page getPage() {
        return page;
    }

    public long getCorpusId() {
        return corpusId;
    }

    public long getDocumentId() {
        return documentId;
    }

    public String getOccurrenceSnippetHtml() {
        return occurrenceSnippetHtml;
    }
}
