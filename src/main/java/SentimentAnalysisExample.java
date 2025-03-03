
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

public class SentimentAnalysisExample {
    public static void main(String[] args) {
        String text = "这部电影太好看了，真是太精彩了！";
        double positiveScore = HanLP.sentiment(text);
        // SentimentAnalyzer
        if (positiveScore > 0) {
            System.out.println("这是一条积极的评论！");
        } else if (positiveScore < 0) {
            System.out.println("这是一条消极的评论！");
        } else {
            System.out.println("这是一条中性的评论！");
        }
    }
}