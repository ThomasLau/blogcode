package xyz.thomas.simi;

import com.google.common.collect.ImmutableMap;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.dictionary.common.CommonSynonymDictionary.SynonymItem;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.utility.Predefine;

import xyz.thomas.utils.SimHash;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.logging.Level;

public class HanLpSimilarity {
    private static Logger log = LoggerFactory.getLogger(HanLpSimilarity.class);
    private static String cleanResume(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
        content = Jsoup.clean(content, Whitelist.none());
        content = StringUtils.lowerCase(content);
        String[] strings = { " ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&amp;nbsp;" };
        for (String s : strings) {
            content = content.replaceAll(s, "");
        }
        return content;
    }
    // 词性的权重: 对分词的一些特殊处理 : 比如: 根据词性添加权重 , 过滤掉标点符号 , 过滤超频词汇等;
    public static Map<String, Integer> weightOfNature = TFIDFByLucene.WeightsMap;//ImmutableMap.of("n", 4, "ns", 2, "nz", 2, "nr", 2, "nw", 2); // 名词的权重是2
    public static Map<String, String> stopNatures = ImmutableMap.of("w", "");;// 停用的词性 如一些标点符号之类的;
    public static SimHash simHash(String sentence) {
        sentence = cleanResume(sentence); // cleanResume 删除一些特殊字符
        Predefine.logger.setLevel(Level.INFO);
        List<Term> termList = NLPTokenizer.segment(sentence); // StandardTokenizer NLPTokenizer; // 对字符串进行分词
        List<WordInfo> wordlist = new ArrayList<>(termList.size());
        termList.forEach(term->{
            String word = term.word; // 分词字符串
            String nature = term.nature.toString(); // 分词属性;
            if (!stopNatures.containsKey(nature)) {
                if ("n".equals(nature)) {
                    SynonymItem synciters = CoreSynonymDictionary.get(word);
                    if (null != synciters && null !=synciters.synonymList && synciters.synonymList.size() >0) {
                        word = synciters.synonymList.get(0).realWord;
                        // System.out.println("sync:"+ term.word+" -- "+word);
                    }
                }
                wordlist.add(new WordInfo().setType(nature).setText(word));
            }
        });
        /* List<WordInfo> words = wordlist.stream().filter(wd->!stopNatures.containsKey(wd.getType()))
                .collect(Collectors.toList()); */
        log.debug("hanlp:{},{}", wordlist, termList);
        SimHash simhash = SimHash.of(wordlist, (wd, idx)->{
            return weightOfNature.getOrDefault(wd.getType(), 1);
        });
        // log.info("hanlp:{},{}", simhash, wordlist);
        return simhash;
    }
    
    public static long quickSimHash(String sentence) {
        sentence = cleanResume(sentence); // cleanResume 删除一些特殊字符
        Predefine.logger.setLevel(Level.INFO);
        List<Term> termList = NLPTokenizer.segment(sentence); // StandardTokenizer NLPTokenizer; // 对字符串进行分词
        List<WordInfo> wordlist = new ArrayList<>(termList.size());
        termList.forEach(term->{
            String word = term.word; // 分词字符串
            String nature = term.nature.toString(); // 分词属性;
            if (!stopNatures.containsKey(nature)) {
                if ("n".equals(nature)) {
                    SynonymItem synciters = CoreSynonymDictionary.get(word);
                    if (null != synciters && null !=synciters.synonymList && synciters.synonymList.size() >0) {
                        word = synciters.synonymList.get(0).realWord;
                        // System.out.println("sync:"+ term.word+" -- "+word);
                    }
                }
                wordlist.add(new WordInfo().setType(nature).setText(word));
            }
        });
        /* List<WordInfo> words = wordlist.stream().filter(wd->!stopNatures.containsKey(wd.getType()))
                .collect(Collectors.toList()); */
        log.debug("quick:{},{}", wordlist, termList);
        long simhash = QuickSimHash.computeSimHash(wordlist, (wd, idx)->{
            return weightOfNature.getOrDefault(wd.getType(), 1);
        });
        // log.info("hanlp:{},{}", simhash, wordlist);
        return simhash;
    }
}
