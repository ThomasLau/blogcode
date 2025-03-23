package xyz.thomas.simi;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.ansj.library.DicLibrary;
import org.ansj.library.StopLibrary;
import org.ansj.library.SynonymsLibrary;
import org.ansj.lucene7.AnsjAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.MMapDirectory;
// import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

//import org.apache.lucene.index.*;
import org.apache.lucene.document.*;
//import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.*;

public class TFIDFByLucene {
    private static Logger log = LoggerFactory.getLogger(TFIDFByLucene.class);
    private static Analyzer analyzer = null;

    /**
     * texts key:value key为id标识，value为内容
     * @param texts
     * @return
     * @throws Exception
     */
    public static Map<String, Map<CharSequence, Integer>> createTfIdf(Map<String, String> texts) throws Exception {
        Directory directory = indexToMemory(texts);
        Map<String, Map<CharSequence, Integer>> tfIdfMap = new TreeMap<>();
        try (IndexReader ireader = DirectoryReader.open(directory);) {
            for (int i = 0; i < ireader.maxDoc(); i++) {
                Document doc = ireader.document(i);
                String id = doc.get("id");
                String text = doc.get("text");
                tfIdfMap.put(id, calcTFIDF(ireader, text));
            }
            // ireader.close();
        } catch (Exception e) {
            log.error("err_createTfIdf", e);
            throw e;
        }
        directory.close();
        return tfIdfMap;
    }

    private static Map<CharSequence, Integer> calcTFIDF(IndexReader ireader, String sentence) throws Exception {
        Map<CharSequence, Integer> tfIdf = new HashMap<>();
        Map<WordInfo, Integer> tf = termFrequency(sentence, EXCLUDE_TYPE, WeightsMap);
        System.out.println(tf);
        for (WordInfo term : tf.keySet()) {
            int docFreq = ireader.docFreq(new Term("text", term.getText()));
            int termFreq = tf.get(term);
            int totalDocs = ireader.numDocs();
            // TF-IDF computation
            double tfIdfValue = termFreq * Math.log10((double) totalDocs / (docFreq + 1));
            // System.out.println(String.format("%s:%s,%s,%s->%.6f", term, termFreq, docFreq, totalDocs, tfIdfValue));
            tfIdf.put(term.getText(), (int) (tfIdfValue * 1000));
        }
        return tfIdf;
    }

    private static Directory indexToMemory(Map<String, String> texts) throws IOException {
        // Directory directory = new MMapDirectory(Paths.get("/tmp/", null));
        Directory directory = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter iwriter = new IndexWriter(directory, config);) {
            for (Entry<String, String> entry : texts.entrySet()) {
                Document doc = new Document();
                doc.add(new StringField("id", entry.getKey(), Field.Store.YES));
                doc.add(new TextField("text", entry.getValue(), Field.Store.YES));
                iwriter.addDocument(doc);
            }
            // iwriter.close();
        } catch (Exception e) {
            log.error("err_indexToMemory", e);
            throw e;
        }
        return directory;
    }

    public static Map<WordInfo, Integer> termFrequency(String sentence, Set<String> excludeType, Map<String, Integer> weights) {
        List<WordInfo> words = splitWords(sentence);
        if (null != excludeType) {
            words = words.stream().filter(s -> !excludeType.contains(s.getType())).collect(Collectors.toList());
        }
        Map<WordInfo, Integer> tf = new HashMap<>();
        for (WordInfo term : words) {
            int weight = 1;
            if (null != weights && !weights.isEmpty()) {
                weight = weights.getOrDefault(term.getType(), 1);
            }
            tf.put(term, tf.getOrDefault(term, 0) + weight);
        }
        return tf;
    }

    public static Set<String> nzrSet = ImmutableSet.of("ns", "nz", "nr", "nw");
    public static Set<String> EXCLUDE_TYPE = ImmutableSet.of("y", "u", "d", "w");// "u"
    //public static Set<String> EXCLUDE_TYPE = ImmutableSet.of("w", "u");
    //public static Map<String, Integer> WeightsMap = ImmutableMap.of();
    // public static Map<String, Integer> WeightsMap = ImmutableMap.of();//"ns", 2, "nz", 2, "nr", 2, "nw", 2);
    //public static Map<String, Integer> WeightsMap = ImmutableMap.of("ns", 20, "nz", 20, "nr", 20, "nw", 20);
    public static Map<String, Integer> WeightsMap = ImmutableMap.of("ns", 2, "nz", 2, "nr", 2, "nw", 2);
    public static List<WordInfo> splitWords(String sentence) {
        List<WordInfo> output = new LinkedList<>();
        try (TokenStream ts = analyzer.tokenStream("myfield", new StringReader(sentence))) {
//          OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
//                if (!"w".equals(type.type()) && !"u".equals(type.type())) {
                WordInfo wif = new WordInfo().setText(term.toString()).setType(type.type());
                output.add(wif);
                // log.debug("split:{}", wif);
            }
            ts.end();
        } catch (Exception e) {
            log.error("err_split", e);
        }
        return output;
    }

    public static void init(String aType, String btype) {
        if ("ansj".equals(aType)) {
            Map<String, String> ansjargs = new HashMap<String, String>();
            ansjargs.put(StopLibrary.DEFAULT, "stop");// "library/stop.dic");
            ansjargs.put(DicLibrary.DEFAULT, "dic");// "library/default.dic");
            ansjargs.put(SynonymsLibrary.DEFAULT, "synonyms");// "library/synonyms.dic");
//            ansjargs.put("isNameRecognition", "true");
//            ansjargs.put("isNumRecognition", "true");
//            ansjargs.put("isQuantifierRecognition", "true");
//            ansjargs.put("isRealName", "false");
            if ("query".equals(btype)) {
                ansjargs.put("type", AnsjAnalyzer.TYPE.query_ansj.name());
            } else {
                ansjargs.put("type", AnsjAnalyzer.TYPE.nlp_ansj.name());
            }
            analyzer = new AnsjAnalyzer(ansjargs);
        } else if ("luceneStandard".equals(aType)) {
            analyzer = new StandardAnalyzer();
        } else if ("ik".equals(aType)) {
            analyzer = new IKAnalyzer(true);
        }
    }

    public static void close(TokenStream ts) {
        if (ts != null) {
            try {
                ts.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
