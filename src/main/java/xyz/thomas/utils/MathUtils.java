package xyz.thomas.utils;

import java.io.IOException;

public class MathUtils {
    /**
     * 威尔逊得分算法
     * @return
     */
    private static float caclWeight(int likeCount, int dislikeCount) {
        int likeRank = likeCount;
        int disLikeRank = dislikeCount * 3;
        float n = likeRank + disLikeRank;
        float p = (likeRank + disLikeRank) != 0 ? (likeRank / n) : 0;
        int a = 3;
        float userInteractPoint = n == 0F ? 0.09F
                : ((Double) ((p + (a * a) / (2 * n) - a / (2 * n) * Math.sqrt(4 * n * p * (1 - p) + a * a)) / (1 + a * a / n))).floatValue();
        return userInteractPoint;
    }

    private static final long ONE_DAYS_MILLIS = 24 * 60 * 60 * 1000L;

    private static final long TWO_DAYS_MILLIS = 2 * 24 * 60 * 60 * 1000L;

    private static final long SEVEN_DAYS_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    private static int WEIGHT_FACTOR = 10000;

    // 起点权重得分
    private static int calculateInteractsPoint(float interactPoint, float messageLengthPoint, float fansLevelPoint, float timePoint, float n,
            long timeStampSecond) {
        long nowTimeDifference = getNowTimeDifference(timeStampSecond);
        // 发布时间在两天之内
        if (nowTimeDifference < TWO_DAYS_MILLIS) {
            return ((Double) ((0.9 + messageLengthPoint * 0.1 + fansLevelPoint * 0.1 + timePoint * 0.15) * WEIGHT_FACTOR)).intValue();
        } else {
            if (n == 0) {
                return ((Double) ((0.1 + messageLengthPoint * 0.1 + fansLevelPoint * 0.1 + timePoint * 0.1) * WEIGHT_FACTOR)).intValue();
            } else if (n < 20) {
                return ((Double) ((interactPoint + messageLengthPoint * 0.1 + fansLevelPoint * 0.1 + timePoint * 0.15) * WEIGHT_FACTOR)).intValue();
            } else {
                return ((Double) ((interactPoint + messageLengthPoint * 0.1 + fansLevelPoint * 0.1 + timePoint * 0.1) * WEIGHT_FACTOR)).intValue();
            }
        }

    }

    private static long getNowTimeDifference(long timeStampSecond) {
        return System.currentTimeMillis() - timeStampSecond;
    }

    // 时间得分
    private static float calculateTimePoint(long timeStampSecond) {
        long nowTimeDifference = getNowTimeDifference(timeStampSecond);
        // 24小时之内
        if (nowTimeDifference < ONE_DAYS_MILLIS) {
            return 1;
        } else if (nowTimeDifference >= ONE_DAYS_MILLIS && nowTimeDifference < TWO_DAYS_MILLIS) {
            return 0.9F;
        } else if (nowTimeDifference >= TWO_DAYS_MILLIS && nowTimeDifference < SEVEN_DAYS_MILLIS) {
            return 0.8F;
        } else if (nowTimeDifference >= SEVEN_DAYS_MILLIS) {
            return 0F;
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 500; i++) {
            float interact1 = caclWeight(i, 0);
            float interact2 = caclWeight(i, 3);
            System.out.println(i + "\t" + interact1 + "\t" + interact2 + "\t" + (interact2 - interact1));
        }

        long time = System.currentTimeMillis() - SEVEN_DAYS_MILLIS - 1000;
        for (int i = 0; i < 500; i++) {
            float interact = caclWeight(i, 0);
            System.out.println(i + "\t" + calculateInteractsPoint(interact, 0.3f, 0, calculateTimePoint(time), i + 5 * 3, time));
        }
        // 10800
        time = System.currentTimeMillis() - SEVEN_DAYS_MILLIS - 1000;
        int i = 281;
        float interact = caclWeight(i, 0);
        System.out.println(i + "\t" + calculateInteractsPoint(interact, 0.3f, 0, calculateTimePoint(time), i + 0 * 3, time));
        System.out.println(caclWeight(281, 0));
    }
}
