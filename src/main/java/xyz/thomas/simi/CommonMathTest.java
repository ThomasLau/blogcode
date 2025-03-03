package xyz.thomas.simi;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class CommonMathTest {
    private static final int FINGERPRINT_LENGTH = 64; // 指纹长度
    // 生成指纹

    private static RealVector generateFingerprint(String input) {
        // 这里省略了具体的特征提取逻辑，实际应用时应根据需求实现
        return new ArrayRealVector(FINGERPRINT_LENGTH);
    }

    // 计算相似度
    private static double calculateSimilarity(RealVector v1, RealVector v2) {
        int hammingDistance = 0;
        for (int i = 0; i < FINGERPRINT_LENGTH; i++) {
            if (v1.getEntry(i) != v2.getEntry(i)) {
                hammingDistance++;
            }
        }
        return 1 - ((double) hammingDistance / FINGERPRINT_LENGTH);
    }

    public static void main(String[] args) {
        String str1 = "这是一段测试文本秋裤v和企鹅v表情额";
        String str2 = "这是另一段类似的测试文本";
        RealVector fingerprint1 = generateFingerprint(str1);
        RealVector fingerprint2 = generateFingerprint(str2);
        double similarity = calculateSimilarity(fingerprint1, fingerprint2);
        System.out.println("字符串相似度: " + similarity);

    }

}
