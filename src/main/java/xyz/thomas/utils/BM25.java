package xyz.thomas.utils;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import xyz.thomas.simi.WordInfo;

public class BM25 {
    private static final double bFact = 0.75;
    private static final double qFact = 1.5;

    public static <ID> Map<ID, Map<WordInfo, Double>> create(Map<ID, String> texts, WordAnalyzer<WordInfo> analyzer, Predicate<WordInfo> filter) {
        Map<ID, List<WordInfo>> wordsByDocs = new HashMap<>();
        for (Entry<ID, String> docEntry : texts.entrySet()) {
            List<WordInfo> words = analyzer.split(docEntry.getValue());
            words = words.stream().filter(filter).collect(Collectors.toList());
            wordsByDocs.put(docEntry.getKey(), words);
        }
        return bm25(wordsByDocs);
    }
    /**
     * ID stands for doc-id, T stands for term
     * @param <T>
     * @param doc
     * @return
     */
    public static <T> Map<T, Integer> tfCalc(Collection<T> doc) {
        return doc.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(term -> 1)));
    }

    public static <T, ID> Map<T, Set<ID>> docFrequencyCalc(Map<ID, ? extends Collection<T>> wordsByDocs) { // ? extends Iterable
        return wordsByDocs.entrySet().stream()
                .flatMap(entry -> StreamSupport.stream(entry.getValue().spliterator(), false)// entry.getValue().stream()
                .map(str -> new AbstractMap.SimpleEntry<T, ID>(str, entry.getKey())))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
    }
    
    public static <ID, T> Map<T, Double> idfCalc(Map<ID, ? extends Collection<T>> wordsByDocs) {
        Map<T, Set<ID>> docFreq = docFrequencyCalc(wordsByDocs);
        int docSize = wordsByDocs.size();
        Map<T, Double> idf = docFreq.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.log((docSize - entry.getValue().size() + 0.5) / (entry.getValue().size() + 0.5) + 1)
            ));
        return idf;
    }
    
    private static Double calcTermScore(Integer termFrequency, Double idfValue, int docLength, double avgDocLen) {
        if (idfValue == 0.0) {
            return 0.0;
        }
        double numerator = idfValue * (termFrequency * (bFact + 1));
        double denominator = termFrequency + bFact * (1 - qFact + qFact * (docLength / avgDocLen));
        return numerator / denominator;
    }

    public static <ID, T> Map<ID, Map<T, Double>> bm25(Map<ID, ? extends Collection<T>> wordsByDocs) {
        Map<T, Double> idf = idfCalc(wordsByDocs);
        Map<ID, Map<T, Integer> > tfmap = wordsByDocs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> tfCalc(entry.getValue())
            ));
        final double avgDocLen = wordsByDocs.values().stream()
                .collect(Collectors.summingInt(term -> term.size()))/wordsByDocs.values().size();
        Map<ID, Map<T, Double>> bmMap = wordsByDocs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().distinct().map(term-> new AbstractMap.SimpleEntry<T, Double>(
                        term, calcTermScore(
                        tfmap.get(entry.getKey()).getOrDefault(term, 0), 
                        idf.getOrDefault(term, 0.0),
                        entry.getValue().size(),
                        avgDocLen))
                  ).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue)))
                  );
        return bmMap;
    }
}
