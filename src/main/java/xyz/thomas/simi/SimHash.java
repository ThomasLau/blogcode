package xyz.thomas.simi;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimHash {
    private static Logger log = LoggerFactory.getLogger(SimHash.class);
    private static final int DEFAULT_BITS = 64; // 分词后的hash数;
    private BigInteger strSimHash;// 字符产的hash值
    private int hashbits = 64; // 分词后的hash数;

    public static SimHash of(List<WordInfo> tokens) {
        return of(tokens, DEFAULT_BITS);
    }

    public static SimHash of(List<WordInfo> tokens, int hashbits) {
        return of(tokens, hashbits, null);
    }

    public static SimHash of(List<WordInfo> tokens, BiFunction<WordInfo, Integer, Integer> weightFunc) {
        return of(tokens, DEFAULT_BITS, weightFunc);
    }

    public static SimHash of(List<WordInfo> tokens, int hashbits, BiFunction<WordInfo, Integer, Integer> weightFunc) {
        SimHash shash = new SimHash();
        shash.hashbits = hashbits;
        shash.strSimHash = simHash(tokens, hashbits, weightFunc);
        return shash;
    }

    // set strSimHash
    private static BigInteger simHash(List<WordInfo> tokens, int hashbits, BiFunction<WordInfo, Integer, Integer> weightFunc) {
        int[] v = new int[hashbits];
        int overCount = 5; // 设定超频词汇的界限 ;
        Map<String, Integer> wordCount = new HashMap<String, Integer>();
        for (int idx = 0; idx < tokens.size(); idx++) {
            WordInfo wordInfo = tokens.get(idx);
            String word = wordInfo.getText();
            if (wordCount.containsKey(word)) {
                int count = wordCount.get(word);
                if (count > overCount) {
                    continue;
                }
                wordCount.put(word, count + 1);
            } else {
                wordCount.put(word, 1);
            }
            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = hash(word, hashbits);
            for (int i = 0; i < hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1; // 添加权重
                if (null != weightFunc) {
                    weight = weightFunc.apply(wordInfo, idx);
                }
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        // log.debug("hanlp:" + fingerprint + "-" + wordCount);
        return fingerprint;
    }

    private static BigInteger hash(String source, int hashbits) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            // 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
            while (source.length() < 3) {
                source = source + source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    public int hammingDistance(SimHash other) {
        if (this.hashbits != other.hashbits) {
            throw new IllegalArgumentException("should both keep the same hashbits");
        }
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    public double getSemblance(SimHash s2) {
        double i = (double) this.hammingDistance(s2);
        return 1 - i / this.hashbits;
    }

    @Override
    public String toString() {
        return "SimHash(" + strSimHash + ", " + hashbits + ")";
    }

}
