

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CountWordsV2 {
    public static void main(String[] args) throws IOException {
        long time1 = System.currentTimeMillis();//检测运行时间(part1)
        BufferedReader bufferedReader = new BufferedReader(new FileReader("./input.txt"));// StandardCharsets.UTF_8
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./output.txt"/*,StandardCharsets.UTF_8*/, true));

        HashMap<String, Integer> bigMap = new HashMap<>();//统计用于并计算idf
        HashMap<String, HashMap<String, Double>> IDMap = new HashMap<>();
        String string = null;
        int countLines = 0;//统计文件集文件数(这里是退化为wei_bo.txt的行数(单条微博"文件"数)
        //todo 从文件中读取每一行.
        while ((string = bufferedReader.readLine()) != null) {
            //System.out.println(string);
            /*使用hanlp包来做：*/
            countLines++;
            HashMap<String, HashMap<String, Double>> idMap = new HashMap<>();
            HashMap<String, Double> map = new HashMap<>();//用于统计词频(每条微博配一个)

            int splitIdx = string.indexOf("\t");//分隔符在字符串中的索引
            String IDString = string.substring(0, splitIdx);//每条微博的ID号
            //IDMap.put(IDString,null);无序;
            string = string.substring(splitIdx + 1);//每条微博ID号之后的内容(作为统计分析的对象)
            //todo 开始分析当前得微博
            List<Term> termList = HanLP.segment(string);//当断点打在这里时,调试的时候陷在里面了?
            int wordsNumber = 0; //= termList.size();
            //对每条微博中的词进行词频统计(string,Double);
            for (Term x : termList) {
                if (x.nature == Nature.w) continue;
                wordsNumber++;
                String str = x.toString();
                if (map.containsKey(str)) {
                    map.put(str, map.get(str) + 1);
                } else {
                    map.put(str, 1.0);
                }
            }//endFor(每一条微博的map生成了)
            /*生成TF键值对(t,tf)*/
            for (String x : map.keySet()) {
                map.put(x, map.get(x) / wordsNumber);
            }
            //list.add(map);//将当前得统计map加入到list中
            //todo 将每一条微博的ID号和其统计map配对.
            IDMap.put(IDString, map);
            //测试打印:
            // System.out.println(map);
            //添加记录到bigMap中:(统计某个词t相对于整个文件集的IDF的过程,要完整遍历所有微博,然后才能使用这个IDF(即要在这个while节输之后使用.)(key:词t,value:包含词t的文件(微博条数)

            //todo 计算文件集中含词t的文件数目.(t,number)(其中bigMap是整个while循环共同使用的)
            for (String x : map.keySet()) {
                if (bigMap.containsKey(x)) {
                    bigMap.put(x, bigMap.get(x) + 1);
                } else {
                    bigMap.put(x, 1);
                }
            }
        }


        /*将(t,tf)更新为(t,tf-idf);其中,tf-idf=tf*idf;idf=countLines/bigMap.get(x).log10 计算出每条微博中各个词t的TF-IDF值*/
        /*强制运行到此处*/
        //todo 计算每个词在其所在的微博中的tfidf值
        for (String ID : IDMap.keySet()) {
            HashMap<String, Double> mapTmp = new HashMap<>();
            mapTmp = IDMap.get(ID);
            for (String s : mapTmp.keySet()) {
                mapTmp.put(s, (mapTmp.get(s)) * (Math.log10((double) countLines / bigMap.get(s))));
            }
            //监视:
            //System.out.println(mapTmp);
        }
        for(String ID:IDMap.keySet()){
            System.out.println(ID+"="+IDMap.get(ID));
            bufferedWriter.write(ID+"="+IDMap.get(ID)+"\n");
        }

        bufferedWriter.flush();  //step2:刷新缓冲区
        //step3:释放资源:
        bufferedReader.close();
        bufferedWriter.close();
        System.out.println("操作完毕.");
        System.out.println("运行耗时" + (System.currentTimeMillis() - time1));

    }
}
