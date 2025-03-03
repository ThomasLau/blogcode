package xyz.thomas.simi.depre;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;


public class TextLevinSimilarity {

    public static double calcSimilarity(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) {
            return 0d;
        }
        return apacheLevenshtein(s1, s2);
    }

    public static double apacheLevenshtein(String s1, String s2) {
        return 1.0d - LevenshteinDistance.getDefaultInstance().apply(s1, s2) * 1.0d / Math.max(s1.length(), s2.length());
    }
    
    public static int apacheMaxCommonSubStrLength(String s1, String s2) {
        return new LongestCommonSubsequence().apply(s1, s2);
    }
    public static void main(String[] args) {
        System.out.println(apacheMaxCommonSubStrLength("1234567890好的sdhdcns", "12shdqj890好的shs"));
        String s1 = "九，像是能担起这天下的人吗？先帝在世那会子，哀家是不得宠，日子过的不好，可这江山如今是哀家的儿子的。"
                + "难不成，他辛苦一生，最后倒是要换个无能的太子来继承？";
        String s2 = "呵呵。”李太后笑了笑：“哀家是皇帝生母不假，可立太子这事，哀家能管多少？你看老九，像是能担起这天下的人吗？"
                + "先帝在世那会子，哀家是不得宠，日子过的不好，可这江山如今是哀家的儿子的。难不成，他辛苦一生，最后倒是要换个无能的太子来继承";
        int length = apacheMaxCommonSubStrLength(s1, s2);
        System.out.println(String.format("%s,%s,%s", length, s1.length(), s2.length()));
        s1 = "123456789是的Chinaxxcd";s2 = "123edrfd78956是的dhChinxaxcd";
        System.out.println(new LongestCommonSubsequence().longestCommonSubsequence(s1, s2));
    }

    // bak in case
    @SuppressWarnings("unused")
    private static double levenshtein(String s1, String s2) {
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
}
