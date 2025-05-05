package org.texttechnologylab.models.viewModels.wiki;

import org.texttechnologylab.models.WikiModel;
import org.texttechnologylab.models.corpus.UCEMetadataValueType;
import org.texttechnologylab.models.topic.TopicWord;
import org.texttechnologylab.models.viewModels.CorpusViewModel;
import org.texttechnologylab.models.viewModels.JsonViewModel;
import org.texttechnologylab.utils.JsonBeautifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorpusWikiPageViewModel extends AnnotationWikiPageViewModel{

    private int documentsCount;
    private List<TopicWord> normalizedTopicWords;

    private Map<String, Double> topicDistributions;

    public Map<String, Double> getTopicDistributions() {
        return topicDistributions;
    }

    public void setTopicDistributions(Map<String, Double> topicDistributions) {
        this.topicDistributions = topicDistributions;
    }

    public List<TopicWord> getNormalizedTopicWords() {
        return normalizedTopicWords;
    }

    public void setNormalizedTopicWords(List<TopicWord> normalizedTopicWords) {
        this.normalizedTopicWords = normalizedTopicWords;
    }

    public int getDocumentsCount() {
        return documentsCount;
    }

    public void setDocumentsCount(int documentsCount) {
        this.documentsCount = documentsCount;
    }

    public List<JsonViewModel> getCorpusConfigJsonAsIterable() {
        var beautifier = new JsonBeautifier();
        return beautifier.parseJsonToViewModel(getCorpus().getCorpus().getCorpusJsonConfig());
    }

    public List<Map<String, Object>> getWordCloudData() {
        List<Map<String, Object>> terms = new ArrayList<>();

        if (normalizedTopicWords != null && !normalizedTopicWords.isEmpty()) {
            for (TopicWord topicWord : normalizedTopicWords) {
                Map<String, Object> term = new HashMap<>();
                term.put("term", topicWord.getWord());
                term.put("weight", topicWord.getProbability());
                terms.add(term);
            }
        }

        return terms;
    }
    public List<Map<String, Object>> getTopicDistributionData() {
        List<Map<String, Object>> topicDistData = new ArrayList<>();

        if (topicDistributions != null && !topicDistributions.isEmpty()) {
            for (Map.Entry<String, Double> entry : topicDistributions.entrySet()) {
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("topicLabel", entry.getKey());
                dataPoint.put("weight", entry.getValue());
                topicDistData.add(dataPoint);
            }
        }

        return topicDistData;
    }
}
