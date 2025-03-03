package xyz.thomas.simi;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import st.ata.util.FPGenerator;

public class QuickSimHash {
    public static final int HASH_SIZE = 64;
    public static final long HASH_RANGE = 2 ^ HASH_SIZE;
    // public static MurmurHash hasher = new MurmurHash();

    // byte gram
    private static final int FIXED_BGRAM_LENGTH = 8;
    // character gram
    private static final int FIXED_CGRAM_LENGTH = 4;

    public static long computeOptimizedSimHashForString(String s) {
        return computeOptimizedSimHashForString(CharBuffer.wrap(s));
    }

    public static long computeOptimizedSimHashForString(CharBuffer s) {
        LongOpenHashSet shingles = new LongOpenHashSet(Math.min(s.length(), 100000));
        int length = s.length();
        for (int i = 0; i < length - FIXED_CGRAM_LENGTH + 1; i++) {
            // extract an ngram
            long shingle = s.charAt(i);
            shingle <<= 16;
            shingle |= s.charAt(i + 1);
            shingle <<= 16;
            shingle |= s.charAt(i + 2);
            shingle <<= 16;
            shingle |= s.charAt(i + 3);

            shingles.add(shingle);
        }

        int v[] = new int[HASH_SIZE];
        byte longAsBytes[] = new byte[8];

        for (long shingle : shingles) {

            longAsBytes[0] = (byte) (shingle >> 56);
            longAsBytes[1] = (byte) (shingle >> 48);
            longAsBytes[2] = (byte) (shingle >> 40);
            longAsBytes[3] = (byte) (shingle >> 32);
            longAsBytes[4] = (byte) (shingle >> 24);
            longAsBytes[5] = (byte) (shingle >> 16);
            longAsBytes[6] = (byte) (shingle >> 8);
            longAsBytes[7] = (byte) (shingle);

            long longHash = FPGenerator.std64.fp(longAsBytes, 0, 8);
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }

        long simhash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simhash |= (1L << i);
            }
        }
        return simhash;
    }

    public static long computeOptimizedSimHashForBytes(byte[] data, int offset, int length) {
        LongOpenHashSet shingles = new LongOpenHashSet(Math.min(length / FIXED_BGRAM_LENGTH, 100000));
        for (int i = offset; i < length - FIXED_BGRAM_LENGTH + 1; i++) {
            int pos = i;
            // extract an ngram
            long shingle = data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos++];
            shingle <<= 8;
            shingle |= data[pos];

            shingles.add(shingle);
        }

        int v[] = new int[HASH_SIZE];
        byte longAsBytes[] = new byte[8];

        for (long shingle : shingles) {

            longAsBytes[0] = (byte) (shingle >> 56);
            longAsBytes[1] = (byte) (shingle >> 48);
            longAsBytes[2] = (byte) (shingle >> 40);
            longAsBytes[3] = (byte) (shingle >> 32);
            longAsBytes[4] = (byte) (shingle >> 24);
            longAsBytes[5] = (byte) (shingle >> 16);
            longAsBytes[6] = (byte) (shingle >> 8);
            longAsBytes[7] = (byte) (shingle);

            long longHash = FPGenerator.std64.fp(longAsBytes, 0, 8);
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }

        long simhash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simhash |= (1L << i);
            }
        }
        return simhash;
    }

    public static long computeSimHashFromString(List<String> shingles) {

        int v[] = new int[HASH_SIZE];
        // compute a set of shingles
        for (String shingle : shingles) {
            byte[] bytes = shingle.getBytes();
            long longHash = FPGenerator.std64.fp(bytes, 0, bytes.length);
            // long hash1 = hasher.hash(bytes, bytes.length, 0);
            // long hash2 = hasher.hash(bytes, bytes.length, (int)hash1);
            // long longHash = (hash1 << 32) | hash2;
            for (int i = 0; i < HASH_SIZE; ++i) {
                boolean bitSet = ((longHash >> i) & 1L) == 1L;
                v[i] += (bitSet) ? 1 : -1;
            }
        }
        long simhash = 0;
        for (int i = 0; i < HASH_SIZE; ++i) {
            if (v[i] > 0) {
                simhash |= (1L << i);
            }
        }

        return simhash;
    }

    public static int hammingDistance(long hash1, long hash2) {
        long bits = hash1 ^ hash2;
        int count = 0;
        while (bits != 0) {
            bits &= bits - 1;
            ++count;
        }
        return count;
    }

    public static long rotate(long hashValue) {
        return (hashValue << 1) | (hashValue >>> -1);
    }

    public static void main(String[] args) {
        long simhash1 = computeSimHashFromString(shingles(10));// Shingle.shingles(string1));
        long simhash2 = computeSimHashFromString(shingles(12));// Shingle.shingles(string2));
        int hammingDistance = hammingDistance(simhash1, simhash2);
        System.out.println("hammingdistance Doc (A) to Doc(B) OldWay:" + hammingDistance);
//                long simhash3 = computeOptimizedSimHashForBytes(data1, 0, data1.length);
//                long simhash4 = computeOptimizedSimHashForBytes(data2, 0, data2.length);
//                int hammingDistance2 = hammingDistance(simhash3, simhash4);
//                System.out.println("hammingdistance Doc (A) to Doc(B) NewWay:" + hammingDistance2);
    }

    private static List<String> shingles(int loop) {
        if (loop == 10) {
            return Arrays.asList("着", "一", "蚂蝗", "了", "看起来", "，", "后", "美", "提", "网", "不得不", "正果", "虚拟", "还", "猴王", "孙", "跟", "拜师", "派别", "更加", "菩", ",", "勤奋",
                    "学习", "世风", "撒网", "说", "游", "战友");
        } else if (loop == 12) {
            return Arrays.asList("以后", "一", "蚂蝗", "了", "看起来", "，", "美", "提", "网", "不得不", "虚拟", "还", "猴王", "孙", "拜", "果", "跟", "更加", "派别", "正", "菩", "自学", ",",
                    "勤奋", "世风", "撒网", "说", "师傅", "游", "战友");
        }
        return Collections.emptyList();
    }

}
