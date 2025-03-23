package xyz.thomas.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class Userful {
    public static void main(String[] args) {
        System.out.println(new BigInteger("1000003"));
        String text = "ceshi123456jdhd";
        System.out.println(splitByPattern(text, 6));
        System.out.println(splitByGuava(text, 6));
        System.out.println(splitByRegex(text, 6));

        for (int j = 0; j < 10; j++) {
            long start = System.nanoTime();
            for (int i = 1; i < 20_0000_0000; i++) {
                hammingDistance(i, i - 1);
            }
            long end1 = System.nanoTime();
            for (int i = 1; i < 20_0000_0000; i++) {
                hamming(i, i - 1);
            }
            long end2 = System.nanoTime();
            for (int i = 1; i < 20_0000_0000; i++) {
                hammingV2(i, i - 1);
            }
            long end3 = System.nanoTime();
            System.out.println("cost: " + (end1 - start) + "---" + (end2 - end1) + "---" + (end3 - end2));
        }
    }
    
    
//    public static int hammingDistance(long hash1, long hash2) {
//        long bits = hash1 ^ hash2;
//        int count = 0;
////        while (bits != 0) {
////            bits &= bits - 1;
////            ++count;
////        }
//        return count;
//    }
//    private static void func() {
//        
//    }

    public static int hammingDistance(long hash1, long hash2) {
        long bits = hash1 ^ hash2;
        int count = 0;
        for (int i = 0; i < 64; i++) {
            bits &= bits - 1;
            // count++;
            if (count != 0) break;
        }
        return count;
    }

    public static int hammingV2(long hash1, long hash2) {
        long bits = hash1 ^ hash2;
        return Long.bitCount(bits);
    }

    private static int hamming(long s1, long s2) {
        int dis = 0;
        for (int i = 0; i < 64; i++) {
            if ((s1 >> i & 1) != (s2 >> i & 1))
                dis++;
        }
        return dis;
    }

    public static List<String> splitByRegex(String text, int n) {
        String[] results = text.split("(?<=\\G.{" + n + "})");
        return Arrays.asList(results);
    }

    public static List<String> splitByGuava(String text, int n) {
        Iterable<String> parts = Splitter.fixedLength(n).split(text);
        return ImmutableList.copyOf(parts);
    }

//    public static List<String> usingPattern(String text, int n) {
//        return Pattern.compile(".{1," + n + "}")
//            .matcher(text).
//            .results()
//            .map(MatchResult::group)
//            .collect(Collectors.toList());
//    }

    public static List<String> splitByPattern(String text, int n) {
        Matcher matcher = Pattern.compile(".{1," + n + "}").matcher(text);
        List<String> result = new ArrayList<>(matcher.groupCount());
        while (matcher.find()) {
            result.add(matcher.group());

        }
        return result;
    }

    public static double editDistanceOrig(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) {
            return 0.D;
        }
        int l1 = s1.length();
        int l2 = s2.length();

        int[][] dp = new int[l1 + 1][l2 + 1];

        for (int i = 1; i < l1 + 1; i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j < l2 + 1; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i < l1 + 1; i++) {
            for (int j = 1; j < l2 + 1; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                }
                dp[i][j] = Math.min(dp[i][j], dp[i][j - 1] + 1);
                dp[i][j] = Math.min(dp[i][j], dp[i - 1][j] + 1);
            }
        }
        return 1 - (double) dp[l1][l2] / Math.max(l1, l2);
    }

    public static double calcSimilarity(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) {
            return 0d;
        }
        return apacheLevenshtein(s1, s2);
    }

    public static double apacheLevenshtein(String s1, String s2) {
        return 1.0d - LevenshteinDistance.getDefaultInstance().apply(s1, s2) * 1.0d / Math.max(s1.length(), s2.length());
    }
    
    public static int apacheMaxCommonSubequence(String s1, String s2) {
        return new LongestCommonSubsequence().apply(s1, s2);
    }
}
