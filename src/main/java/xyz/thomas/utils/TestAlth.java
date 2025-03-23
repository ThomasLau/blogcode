package xyz.thomas.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import xyz.thomas.simi.TFIDFByLucene;
import xyz.thomas.simi.WordInfo;
import xyz.thomas.simi.XSimilarity;

public class TestAlth {
    public static void main(String[] args) throws Exception {
        TFIDFByLucene.init("ansj", "nlp_ansj");
        
        althSimCalc(1, XSimilarity.text1, XSimilarity.text2, TFIDFByLucene.EXCLUDE_TYPE, TFIDFByLucene.WeightsMap);
        System.out.println(" --------- ");
        althSimCalc(2, XSimilarity.text1, XSimilarity.text2, TFIDFByLucene.EXCLUDE_TYPE, TFIDFByLucene.WeightsMap);
    }
    private static WordAnalyzer<WordInfo> ANALYZER = new WordAnalyzer<WordInfo>() {
        @Override
        public List<WordInfo> split(String text) {
            return TFIDFByLucene.splitWords(text);
        }
    };
    private static Predicate<WordInfo> WRDFILTER = new Predicate<WordInfo>() {
        @Override
        public boolean test(WordInfo wd) {
            return !TFIDFByLucene.EXCLUDE_TYPE.contains(wd.getType());
        }
        
    };
    public static void althSimCalc(int type, Map<String, String> text1, Map<String, String> text2, 
            Set<String> excludeType, Map<String, Integer> weights) throws Exception {
        Map<String, Map<WordInfo, Double>> tfIdf1 = null;
        Map<String, Map<WordInfo, Double>> tfIdf2 = null;
        String prefx = "";
        if (type == 1) {
            tfIdf1 = TFIDF.create(text1, ANALYZER, WRDFILTER);
            tfIdf2 = TFIDF.create(text2, ANALYZER, WRDFILTER);
            prefx = "TFIDF";
        }else if (type == 2) {
            tfIdf1 = BM25.create(text1, ANALYZER, WRDFILTER);
            tfIdf2 = BM25.create(text2, ANALYZER, WRDFILTER);
            prefx = "BM25 ";
        }
        if (null != weights && !weights.isEmpty()) {
            tfIdf1.values().forEach(ent -> reWeight(ent, weights));
            tfIdf2.values().forEach(ent -> reWeight(ent, weights));
        }
        for (String key1 : tfIdf1.keySet()) {
            String bestMatch = "-1";
            double bestScore = -1.0;
            for (String key2 : tfIdf2.keySet()) {
                if (XSimilarity.detailSet.contains(key1)) {
                    Map<WordInfo, Double> trmap1 = new TreeMap<>();
                    trmap1.putAll(tfIdf1.get(key1));
                    Map<WordInfo, Double> trmap2 = new TreeMap<>();
                    trmap2.putAll(tfIdf2.get(key2));
                    System.out.println(String.format("%s-%s:\n%s\n%s\n----", key1, key2, trmap1, trmap2));
                }
                double score = Cosine.cosineSimilarity(tfIdf1.get(key1), tfIdf2.get(key2));
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = key2;
                }
            }
            System.out.println(String.format("%s: %s -> %s with similarity: %.4f", prefx, key1, bestMatch, bestScore));
        }
    }
    private static void reWeight(Map<WordInfo, Double> words, Map<String, Integer> weights) {
        words.entrySet().forEach(ent->{
            Integer weit = weights.get(ent.getKey().getType());
            if (null != weit) {
                ent.setValue(ent.getValue() * weit);
            }
        });
    }
}
