package xyz.thomas.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import xyz.thomas.simi.WordInfo;

public class TFIDF {

    public static <ID> Map<ID, Map<WordInfo, Double>> create(Map<ID, String> texts, WordAnalyzer<WordInfo> analyzer,
            Predicate<WordInfo> filter) {
        Map<ID, List<WordInfo>> wordsByDocs = texts.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                docEntry -> analyzer.split(docEntry.getValue()).stream().filter(filter).collect(Collectors.toList())
            ));
        return tfIdf(wordsByDocs, false, false);
    }

    public static <T> Map<T, Integer> termCnt(Collection<T> doc) {
        return doc.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(term -> 1)));
    }

    public static <T> Map<T, Double> tf(Collection<T> doc) {
        Map<T, Integer> termCount = termCnt(doc);
        double size = doc.size();
        return termCount.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue() / size
            ));
    }
    public static <ID, T> Map<ID, Map<T, Double>> tfIdf(Map<ID, ? extends Collection<T>> wordsByDocs, boolean smooth, boolean addOne) {
        Map<ID, Map<T, Double>> tfMap = new HashMap<>();
        List<Map<T, Double>> tfDocs = new LinkedList<>();
        for (Entry<ID, ? extends Collection<T>> iter : wordsByDocs.entrySet()) {
            Map<T, Double> docTf = tf(iter.getValue());
            tfDocs.add(docTf);
            tfMap.put(iter.getKey(), docTf);
        }
        Map<T, Double> idfMap = idf(tfDocs, smooth, addOne);
        for (Entry<ID, Map<T, Double>> entry : tfMap.entrySet()) {
            Map<T, Double> tf = entry.getValue();
            for (Entry<T, Double> tfentry : tf.entrySet()) {
                Double idfd = idfMap.get(tfentry.getKey());
                tfentry.setValue(tfentry.getValue() * idfd);
            }
        }
        return tfMap;
    }

    public static <ID, T> Map<T, Double> idf(List<Map<T, Double>> tfOfDocs, boolean smooth, boolean addOne) {
        Map<T, Integer> df = new HashMap<T, Integer>(); // 出现的文档数量
        int d = smooth ? 1 : 0;
        int a = addOne ? 1 : 0;
        int n = d;
        for (Map<T, Double> docWords : tfOfDocs) {
            n += 1;
            for (T word : docWords.keySet()) {
                Integer dcnt = df.get(word);
                if (dcnt == null)
                    dcnt = d;
                df.put(word, dcnt + 1);
            }
        }
        Map<T, Double> idf = new HashMap<T, Double>();
        for (Map.Entry<T, Integer> e : df.entrySet()) {
            T T = e.getKey();
            double f = e.getValue();
            idf.put(T, Math.log(n / f) + a);
        }
        return idf;
    }
}
