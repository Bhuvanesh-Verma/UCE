package org.texttechnologylab.models.negation;

import org.texttechnologylab.models.UIMAAnnotation;
import org.texttechnologylab.models.corpus.Document;

import javax.persistence.*;

@Entity
@Table(name="event")
public class Event extends UIMAAnnotation {
    @ManyToOne
    @JoinColumn(name = "negation_id")
    private CompleteNegation negation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    public Event(){
        super(-1, -1);
    }
    public Event(int begin, int end) {
        super(begin, end);
    }
    public Event(int begin, int end, String coveredText){
        super(begin, end);
        setCoveredText(coveredText);
    }

    public CompleteNegation getNegation() {
        return negation;
    }

    public void setNegation(CompleteNegation negation) {
        this.negation = negation;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
