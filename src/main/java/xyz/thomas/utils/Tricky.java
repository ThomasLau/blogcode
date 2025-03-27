package xyz.thomas.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tricky {
    public static void main(String[] args) {
        // dangeroursRegex();
        dangeroursRegexV2();
    }
    
    public static void dangeroursRegexV2() {
        String regex2 = "(/[a-z.#-\\/]+)+/";
        // String sent = "/../../../../../../../../../../../../../../../../../../../../etc/./////.////.//e";
        String sent = "/../../../../../../../../../../../../../../../../../../../../etc/./////.////.//e./passwd\\u0000";
        Pattern ppPattern = Pattern.compile(regex2);
        System.out.println("patter");
        Matcher matcher = ppPattern.matcher(sent);
        System.out.println(matcher);
//        while (matcher.find()) {
//            System.out.println(matcher.groupCount());
//        }
        System.out.println(matcher.matches());
        boolean match = sent.matches(regex2);
        System.out.println(match);
    }
    public static void dangeroursRegex() {
        String regex = "^((https|http|ftp|rtsp|mms)?://)" // https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'{}().;?:@&=+$,%#-\\[\\]]+)+/?)$";
        boolean match = "https://act.foo.bar/nomandi/726843558/../../../../../../../../../../../../../../../../../../../../etc/./////.////.///.//./passwd\\u0000"
                .matches(regex);
        System.out.println(match);
    }
}
