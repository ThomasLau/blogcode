package xyz.thomas.simi.depre;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.text.similarity.CosineSimilarity;

import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;

import xyz.thomas.simi.HanLpSimilarity;
import xyz.thomas.simi.SimHash;

public class test {

    public static void main(String[] args) {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        
        
        // testnew();
        // "拜师后，不得不说孙悟空看起来打扫学七一菩提六十,学习一门正果更加勤奋了,蚂蝗,还跟网友学习了网游学习了虚拟网络世界"
        // "美猴王拜了师傅以后，必须说看起来更加勤奋学习正果一门了,蚂蝗,还跟网友学习了虚拟网络世界学习了网游"
        testY();
        String text1 = "拜师后，不得不说";
        String text2 = "不得不说拜师后，";
        SimHash  hash1 = HanLpSimilarity.simHash(text1);
        SimHash hash2 = HanLpSimilarity.simHash(text2);
        System.out.println("海明距离:" + hash1.hammingDistance(hash2) + "###" + "文本相似度:" + hash2.getSemblance(hash1));
    }
    private static void testY() {
        System.out.println(CoreSynonymDictionary.similarity("时不我待", "机不可失"));
        System.out.println(CoreSynonymDictionary.similarity("机会", "时机"));
        System.out.println(CoreSynonymDictionary.similarity("孙悟空", "美猴王"));
        System.out.println(CoreSynonymDictionary.get("美猴王"));
        
        SimHash hash1 = HanLpSimilarity.simHash("拜师后，不得不说孙悟空看起来打扫学七一菩提六十,学习一门正果更加勤奋了,蚂蝗,还跟网友学习了网游学习了虚拟网络世界");
        SimHash hash2 = HanLpSimilarity.simHash("美猴王拜了师傅以后，必须说看起来更加勤奋学习正果一门了,蚂蝗,还跟网友学习了虚拟网络世界学习了网游");
        System.out.println("海明距离:" + hash1.hammingDistance(hash2) + "###" + "文本相似度:" + hash2.getSemblance(hash1));
        
        String text_orig = "那白丸授了法诀，把从别人手里抢来的的泥丸宫轻轻地上下左右那么一转。最后落在了下丹。";
        List<String> itemList = new LinkedList<String>();
        itemList.add("那白丸授了法诀，在泥丸宫轻轻一转，顺着经脉而走，最后落在了下丹。");
        itemList.add("那白丸授了法诀，把从别人手里抢来的的泥丸宫轻轻地上下左右那么一转，最后落在了下丹。");
        itemList.add("那黑丸授了法诀，把从别人手里抢来的的泥丸宫轻轻地上下左右那么一转，最后落在了下丹。");
        itemList.add("那白丸授了法诀，把从别人手里抢来的的泥丸宫轻轻地左右那么一转，最后落在了下丹。");
        SimHash hash_orig = HanLpSimilarity.simHash(text_orig);
        for (String itemStr : itemList) {
            System.out.println("orig:iter" + hash_orig.getSemblance(HanLpSimilarity.simHash(itemStr)));
        }
    }
    private static void testnew() {
        String text = "杏南一区";
        List<String> itemList = new LinkedList<String>();
        itemList.add("杏南一区");
        itemList.add("杏南二区");
        itemList.add("杏南三区");
        itemList.add("杏南四区");
        itemList.add("杏南五区");
        itemList.add("杏南六区");
        itemList.add("杏南七区");
        itemList.add("杏南八区");
        itemList.add("杏南九区");
        itemList.add("杏南十区");
        System.out.println("======================================");
        long startTime = System.currentTimeMillis();
        SimHash hash1 = HanLpSimilarity.simHash(text);
        List<Double> list = new LinkedList<Double>();
        for (String str : itemList) {
            SimHash hash2 = HanLpSimilarity.simHash(str);
            // 海明距离越小说明越相似
            System.out.println("海明距离:" + hash1.hammingDistance(hash2) + "###" + "文本相似度:" + hash2.getSemblance(hash1));
            list.add(hash2.getSemblance(hash1));
        }
        long endTime = System.currentTimeMillis();
        Double max = Collections.max(list);
        int index = list.indexOf(max);// 获取集合下标
        System.out.println("======================================");
        System.out.println("耗时:" + (endTime - startTime));
        System.out.println("相似度集合内容:" + list.toString());
        System.out.println("集合中最大值:" + max + "###" + "集合下标:" + index);
        System.out.println("对比内容:" + text + "###" + "相似度最高:" + itemList.get(index));
        
        
        System.out.println(CoreSynonymDictionary.similarity("时不我待", "机不可失"));
        System.out.println(CoreSynonymDictionary.similarity("机会", "时机"));
        System.out.println(CoreSynonymDictionary.similarity("孙悟空", "美猴王"));
        System.out.println(CoreSynonymDictionary.get("美猴王"));
    }
}
