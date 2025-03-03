package xyz.thomas.simi.depre;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ansj.dic.LearnTool;
import org.ansj.domain.Nature;
import org.ansj.library.AmbiguityLibrary;
import org.ansj.library.DicLibrary;
import org.ansj.library.StopLibrary;
import org.ansj.library.SynonymsLibrary;
import org.ansj.lucene7.AnsjAnalyzer;
import org.ansj.lucene7.AnsjAnalyzer.TYPE;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import xyz.thomas.simi.XSimilarity;

public class ansjAnaTest {
    // private static Analyzer analyzer = new AnsjAnalyzer(TYPE.nlp_ansj);
    private static Analyzer analyzer = null;
    public static void main(String[] args) throws Exception {
        String a11 = XSimilarity.text1.get("A11");
        String b12 = XSimilarity.text2.get("B12");
        
//        System.out.println(DicAnalysis.parse("孙大头和美猴王在测试ugc"));
//        System.out.println(NlpAnalysis.parse("孙大头和美猴王在测试ugc，他说他不是美猴王"));
//        
//        System.out.println(NlpAnalysis.parse(a11));
//        System.out.println(NlpAnalysis.parse(b12));
//        System.out.println(DicAnalysis.parse(b12));
        
        //构建一个新词学习的工具类。这个对象。保存了所有分词中出现的新词。出现次数越多。相对权重越大。
        LearnTool learnTool = new LearnTool() ;
        NlpAnalysis nlpas = new NlpAnalysis();
        nlpas.setLearnTool(learnTool);
        System.out.println(nlpas.parseStr("孙小头和美猴王在测试，孙悟空看起来打扫学七一菩提六十"));
//        // System.out.println(nlpas.parseStr("孙小头和美猴王在测试ugc，他说他不是美猴王"));
//        //进行词语分词。也就是nlp方式分词，这里可以分多篇文章
////        System.out.println(nlpas.parseStr("说过，社交软件也是打着沟通的平台，让无数寂寞男女有了肉体与精神的寄托。")); ;
////        System.out.println(nlpas.parseStr("其实可以打着这个需求点去运作的互联网公司不应只是社交类软件与可穿戴设备，还有携程网，去哪儿网等等，订房订酒店多好的寓意")); ;
////        System.out.println(nlpas.parseStr("张艺谋的卡宴，公文包马明哲的戏，和公文包拯一起看了红海行动"));;
//        //取得学习到的topn新词,返回前10个。这里如果设置为0则返回全部
        System.out.println(learnTool.getTopTree(10));
//        //只取得词性为Nature.NR的新词
        System.out.println(learnTool.getTopTree(10,Nature.NR));
        //System.out.println(DicAnalysis.parse("孙巨头和美猴王在测试"));
        // UserDefineLibrary.insertWord("ansj中文分词", "userDefine", 1000);
        // Forest frt1 = new Forest();
        // Library.insertWord(frt1, new Value("美猴王", "define", "1000"));
        DicLibrary.insert(DicLibrary.DEFAULT, "美猴王", "userDefine", 10);
        DicLibrary.insert(DicLibrary.DEFAULT, "弼马温", "userDefine", 1000);
        // SynonymsLibrary.insert("synonyms", new String[] {"孙悟空","美猴王","弼马温"});
        SynonymsLibrary.append("synonyms", new String[] {"孙悟空","美猴王","弼马温"});
        System.out.println(SynonymsLibrary.get().getWord("教师").getFrontWords());
        // System.out.println(DicAnalysis.parse("美猴王"));
        System.out.println(DicAnalysis.parse("孙巨头和美猴王大战弼马温"));
        System.out.println(NlpAnalysis.parse("孙巨头和美猴王大战弼马温"));
        
        Map<String, String> ansjargs = new HashMap<String, String>();
        ansjargs.put("type",AnsjAnalyzer.TYPE.nlp_ansj.name());
        ansjargs.put(StopLibrary.DEFAULT, "stop");//"library/stop.dic");
        ansjargs.put(DicLibrary.DEFAULT, "dic");// "library/default.dic");
        ansjargs.put(SynonymsLibrary.DEFAULT, "synonyms");// "library/synonyms.dic");
        // args.put(AmbiguityLibrary.DEFAULT, "歧义词典KEY");
//        ansjargs.put("isNameRecognition", "true");
//        ansjargs.put("isNumRecognition", "true");
//        ansjargs.put("isQuantifierRecognition", "true");
//        ansjargs.put("isRealName", "false");
        analyzer = new AnsjAnalyzer(ansjargs);
        //DicLibrary.insert(DicLibrary.DEFAULT, "美猴王", "userDefine", 65536);
        // StopLibrary.insertStopNatures("stop", "。","，");
        testinsdex(text1);
    }
    public static Map<String, String> text1 = new HashMap<String, String>(){
        private static final long serialVersionUID = 4866228539827332666L;
    {
        //put("A2","第二天，猴王吃过瓜果，喝下猴子们送行的酒，便坐上枯松编成的木筏，顺着大海，朝南赡部洲方向划去。");
        //put("A10","拜师后，孙悟空每天和弟子们待在一起，打扫庭院，养花修树，挑水捡柴，学经论道，不知不觉过去了七年时间。一天，菩提老祖对孙悟空说：“‘道’字门中有三百六十旁门，只要悉心学习都可以学成正果，不知道你想学哪一门呢？”");
        put("A11","拜师后，孙大头和美猴王看起来打扫学七一菩提六十,不得不说学习一门正果更加勤奋了,蚂蝗,还跟网友学习了网游学习了虚拟网络世界");
    }};
    private static void testinsdex(Map<String, String> texts) throws Exception {
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        for (Entry<String, String> entry : texts.entrySet()) {
            Document doc = new Document();
            doc.add(new StringField("id", entry.getKey(), Field.Store.YES));
            doc.add(new TextField("text", entry.getValue(), Field.Store.YES));
            iwriter.addDocument(doc);
        }
        iwriter.close();

        IndexReader ireader = DirectoryReader.open(directory);

        for (Entry<String, String> term : texts.entrySet()) {
            Map<CharSequence, Integer> tfIdf = new HashMap<>();
            System.out.println("----- " + term.getKey());
            Map<String, Integer> tf = termFrequency(term.getValue());
            for (Entry<String, Integer> wordTf : tf.entrySet()) {
                int docFreq = ireader.docFreq(new Term("text", wordTf.getKey()));
                int termFreq = tf.get(wordTf.getKey());
                int totalDocs = ireader.numDocs();
                double tfIdfValue = termFreq *( Math.log10((double) 10 / (docFreq+1)));
                System.out.println(String.format("%s:%s,%s->%.6f", wordTf.getKey(), termFreq, docFreq, tfIdfValue));
                tfIdf.put(wordTf.getKey(), (int) (tfIdfValue * 1000));
            }
            System.out.println(tfIdf);
        }
        System.out.println(ireader.docFreq(new Term("text", "孙悟空")));
        ireader.close();
    }
    private static Map<String, Integer> termFrequency(String text) {
        Map<String, Integer> tf = new HashMap<>();
        // String[] terms = text.split("\\s+");
        // System.out.println(text);
        for (String term : splitWords(text)) {
            tf.put(term, tf.getOrDefault(term, 0) + 1);
        }
        return tf;
    }

    public static List<String> splitWords(String input) {
        List<String> output = new LinkedList<>();
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("myfield", new StringReader(input));
//            OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                if (!"w".equals(type.type())) {
                    output.add(term.toString());
                    System.out.println("ts:"+term.toString()+"-"+type.type());
                } else {
                    System.out.println("ts:"+term.toString()+"-"+type.type());
                }
            }
            ts.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(ts);
        }
        return output;
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
