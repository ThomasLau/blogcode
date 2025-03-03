package xyz.thomas.simi;

import java.util.*;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import com.google.common.collect.ImmutableSet;

public class XSimilarity {
//  text1.put("B2", "林小婉努力地想睁开眼睛，却始终被那粘稠的黑暗缠绕着，好似梦魇时的感觉——意识似乎是清醒的，却怎么也醒不过来。");
//  text1.put("B3", "“二姐，二姐！呜呜呜……二姐你不要死呀！石头不饿了，石头不吃馒头了！二姐你醒醒啊……”林小婉感觉有个小小的重量，扑在自己身旁，拼命地摇晃着自己的胳膊。");
//  text1.put("B4", "二姐？不对吧？她明明是家里的老大，在初中时父母相继去世，作为大姐的她，辍学打工拉拔两个弟弟妹妹成人。被叫了二十多年的大姐，怎么突然之间变成二姐了？一定是认错人了！");
//  text1.put("B5", "“作孽哟！这孩子不就捡了块馒头吃吗？竟将人往死里打！小草这孩子身子本来就不结实，今儿被撞得头破血流的，不会没气了吧？");
//  text1.put("B29", "“妹妹，谁把你打成这样？我帮你去骂他！！”这个瘦瘦小小，看起来不足十岁的小萝莉，是她姐姐？林小婉睁大了眼睛，低头看了自己一眼——小小的手掌，小小的身子——她心中苦笑了下，难道上苍怜悯她前世少年持家的艰辛，让她重新找回残缺的童年？");
//  text1.put("B30", "不过老天爷，你好歹也选个条件好的家庭吧，看着一家子穿得破破烂烂、面黄肌瘦的，连别人扔的半块破馒头都捡起来吃，也忒惨了点儿吧？");
//  text1.put("B33", "身为长姐的她，自动辍学，用单薄幼嫩的肩膀，挑起了照顾弟弟妹妹的重任。那年，妹妹十二岁，弟弟刚满十岁。");
//  text1.put("B34", "十四年来，她不光要料理家中的三亩田地。为了凑齐弟弟妹妹的学费，她不得不四处去打工。怕老板嫌她年纪小，就谎报岁数说自己已经十七，只是看着显小而已。");
//  text1.put("B37", "妹妹懂事，心疼她起早贪黑的工作供她上学。高中毕业后，瞒着她未曾参加高考，偷偷跟着村里的女孩们一起，踏上了南下打工的路程。");
//  text1.put("B38", "为了这事，林晓婉狠狠地哭了一场，恨自己没本事，耽误了妹妹的前途。以妹妹的成绩，虽说考不了名牌学校，考上本科应该是没问题的。");
//  text1.put("B39", "弟弟从小成绩就在班里名列前茅，小学和初中时，还各跳了一级。十五岁还在高二的时候，就缠着班主任帮他在高三报名参加高考，志愿表上除了第一批录取的军事院校，其他都填了空白。");
//  text1.put("B40", "军校不但免学费，还发基本的津贴，成绩优异的毕业后分配到地方军队，还是副连级军官呢！她知道，弟弟这是为了减轻她的负担呀！");
//  text1.put("B41", "一转眼，十四年过去了。妹妹一边打工一边自学，取得了本科文凭，并且熬成了一个小白领，也收获了自己的爱情。");
//  text1.put("B42", "弟弟在军校里，表现一直很优异。十九岁毕业，分到了金陵军区，成为军区年纪最小的军官。后来，高大英俊，各方面都很优秀的弟弟，被长官看中，介绍给自己小孙女。两人一见钟情，情投意合。");
//  text1.put("B43", "结婚时，妹妹也带着她的白领老公和一岁多的儿子来参加婚礼。看着弟弟妹妹都有了幸福的家庭，她实在是太高兴了，所以就多喝了两杯……");
//  text1.put("B44", "酒席散后，她在下台阶时，不小心踩空，从楼梯上栽了下来……再醒来，就成了从小体弱的渔家小萝莉……");
//  text1.put("B1", "林小婉的意识昏昏沉沉的，鼻间嗅到海滩独有的腥咸，耳畔隐隐约约传来海浪拍打沙滩的“哗哗”声。一定是在做梦，一定是的！她的老家在内陆地带，这辈子就沾了在滨海城市读大学的妹妹的光，平生唯一那么一次看见过大海，怎么会听到海浪的声音？");
//  text2.put("L1", "林小婉的意识昏昏沉沉的，鼻间嗅到海滩独有的腥咸，耳畔隐隐约约传来海浪拍打沙滩的“哗哗”声。一定是在做梦，一定是的！她的老家在内陆地带，这辈子就沾了在滨海城市读大学的妹妹的光，平生唯一那么一次看见过大海，怎么会听到海浪的声音？");
//  text2.put("L2", "林小婉努力地想睁开眼睛，却始终被那粘稠的黑暗缠绕着，好似梦魇时的感觉——意识似乎是清醒的，却怎么也醒不过来。");
//  text2.put("L3", "“二姐，二姐！呜呜呜……二姐你不要死呀！石头不饿了，石头不吃馒头了！二姐你醒醒啊……”林小婉感觉有个小小的重量，扑在自己身旁，拼命地摇晃着自己的胳膊。");
//  text2.put("L4", "二姐？不对吧？她明明是家里的老大，在初中时父母相继去世，作为大姐的她，辍学打工拉拔两个弟弟妹妹成人。被叫了二十多年的大姐，怎么突然之间变成二姐了？一定是认错人了！");
//  text2.put("L5", "“作孽哟！这孩子不就捡了块馒头吃吗？竟将人往死里打！小草这孩子身子本来就不结实，今儿被撞得头破血流的，不会没气了吧？");
//  text2.put("L29", "不过老天爷，你好歹也选个条件好的家庭吧，看着一家子穿得破破烂烂、面黄肌瘦的，连别人扔的半块破馒头都捡起来吃，也忒惨了点儿吧？");
//  text2.put("L32", "身为长姐的她，自动辍学，用单薄幼嫩的肩膀，挑起了照顾弟弟妹妹的重任。那年，妹妹十二岁，弟弟刚满十岁。");
//  text2.put("L33", "十四年来，她不光要料理家中的三亩田地。为了凑齐弟弟妹妹的学费，她不得不四处去打工。怕老板嫌她年纪小，就谎报岁数说自己已经十七，只是看着显小而已。");
//  text2.put("L36", "妹妹懂事，心疼她起早贪黑的工作供她上学。高中毕业后，瞒着她未曾参加高考，偷偷跟着村里的女孩们一起，踏上了南下打工的路程。");
//  text2.put("L37", "为了这事，林晓婉狠狠地哭了一场，恨自己没本事，耽误了妹妹的前途。以妹妹的成绩，虽说考不了名牌学校，考上本科应该是没问题的。");
//  text2.put("L38", "弟弟从小成绩就在班里名列前茅，小学和初中时，还各跳了一级。十五岁还在高二的时候，就缠着班主任帮他在高三报名参加高考，志愿表上除了第一批录取的军事院校，其他都填了空白。");
//  text2.put("L39", "军校不但免学费，还发基本的津贴，成绩优异的毕业后分配到地方军队，还是副连级军官呢！她知道，弟弟这是为了减轻她的负担呀！");
//  text2.put("L40", "弟弟在军校里，表现一直很优异。十九岁毕业，分到了金陵军区，成为军区年纪最小的军官。后来，高大英俊，各方面都很优秀的弟弟，被长官看中，介绍给自己小孙女。两人一见钟情，情投意合。");
//  text2.put("L41", "结婚时，妹妹也带着她的白领老公和一岁多的儿子来参加婚礼。看着弟弟妹妹都有了幸福的家庭，她实在是太高兴了，所以就多喝了两杯……");
//  text2.put("L42", "酒席散后，她在下台阶时，不小心踩空，从楼梯上栽了下来……再醒来，就成了无人不喜欢的的渔家小萝莉");

    public static Map<String, String> text1 = new TreeMap<String, String>() {{
//        put("A1","听了这话，美猴王高兴地说：“好，那我明天就出发，一定要找到这三种人中的一个，学个长生不老之道，省得冥王索我性命。”众猴听了，准备了丰盛的瓜果，给猴王送行。");
////        put("A2","第二天，猴王吃过瓜果，喝下猴子们送行的酒，便坐上枯松编成的木筏，顺着大海，朝南赡部洲方向划去。");
////        put("A3","在集市上，猴王努力地模仿人类的举止，同时寻访仙人。但在南赡部洲待了八九年的时间，猴王始终没有见到仙人的影子。");
////        put("A4","猴王来到樵夫跟前，大声说：“老神仙，弟子有礼了！”随即向樵夫行礼。樵夫见状，慌忙回答说：“罪过罪过，我一介贫夫，怎么敢称神仙呢？”");
////        put("A5","樵夫于是回答说：“实不相瞒，我唱的这些都是一位神仙教给我的。神仙见我终日劳苦，烦心事很多，就把这些歌谣教给我，让我在烦躁的时候唱给自己听");
////        put("A6","樵夫回答说：“他住得离这儿并不远。这附近有一座山叫灵台方寸山，上面有一座斜月三星洞，里面有一个叫菩提祖师的神仙，就是你要找的人。你顺着这条路往南走个七八里地，就到了。”");
////        put("A7","猴王告别樵夫，按照他说的路走出去七八里，果然来到了一座洞府跟前。只见这座洞府映照在霞光里，两旁长满松柏，寂静无人，偶尔有鹤鸣从里面传出。猴王来到门前，");
//            put("A8", "过了一会儿，一个相貌清秀的童子打开府门走出来，对着空无一人的石阶喊了一声：“是什么人在门外胡闹？”，猴王见状，从树上跳下来说：“我是个学仙的弟子，特来拜师学艺。”童子说：“师父叫我来开门，说外面来了一个求师的，想必就是你吧，请跟我来。”猴王便跟着童子来到了洞府里面。");
////        put("A9","见到菩提老祖，美猴王跪在他跟前说：“师父，弟子有礼了！”菩提老祖说：“先别急着拜师，把你的来历告诉我再拜师也不迟。”于是，猴王便将自己的来历告诉了菩提老祖。菩提老祖又问他有没有父母和姓名，猴王说没有。菩提老祖想了想，赐给他“孙悟空”一名。猴王非常喜欢这个名字，高兴地说：“好好好，以后我就叫孙悟空了！”");
////        put("A10","拜师后，孙悟空每天和弟子们待在一起，打扫庭院，养花修树，挑水捡柴，学经论道，不知不觉过去了七年时间。一天，菩提老祖对孙悟空说：“‘道’字门中有三百六十旁门，只要悉心学习都可以学成正果，不知道你想学哪一门呢？”");
//            put("A11", "拜师后，不得不说美猴王看起来跟着孙菩提学习了一门正果更加勤奋了,蚂蝗,还跟网友学习了网游学习了虚拟网络世界");
//            put("A12", "陆阳凝聚溯源道果，为的就是现在这一刻，以庆功宴威名汇聚众人，与幕后黑手撕破脸皮，依靠大师姐的合道状态斩杀幕后黑手，再借由溯源道果让大师姐跌落状态，一切都按照计划进行");
//            put("A13", "撒网师傅说战友孙悟空不得不说看起来跟孙蚂蝗拜以后，自学更加勤奋了，一提虚拟网络网游，就更加勤奋，世风流行自学");
//            put("A14", "贾基听了非常高兴，不禁念诗一首:『富士见之女，于西行妖满开之时，即幽明境分开之时，为其魂魄，安息于白玉楼中，将西行妖之花封印作");
 //           put("A15", "非UGC，仅用于QQ文插图的点赞功能,subtype=1");
        put("A9", "见到菩提老祖，美猴王跪在他跟前说：“师父，弟子有礼了！”菩提老祖说：“先别急着拜师，把你的来历告诉我再拜师也不迟。”于是，猴王便将自己的来历告诉了菩提老祖。菩提老祖又问他有没有父母和姓名，猴王说没有。菩提老祖想了想，赐给他“孙悟空”一名。猴王非常喜欢这个名字，高兴地说：“好好好，以后我就叫孙悟空了！”");
        put("A10", "拜师后，孙悟空每天和弟子们待在一起，打扫庭院，养花修树，挑水捡柴，学经论道，不知不觉过去了七年时间。一天，菩提老祖对孙悟空说：“‘道’字门中有三百六十旁门，只要悉心学习都可以学成正果，不知道你想学哪一门呢？”");
        put("A11", "拜师后，孙悟空跟孙菩提学习打扫,看起来不得不说学习一门正果更加勤奋了,蚂蝗,还跟网友学习网游学习虚拟网络世界");
        put("A12", "赵敏说道，是成昆把张无忌打了一顿");
        }
    };
    public static Map<String, String> text2 = new TreeMap<String, String>() {{
//        put("B1","听了这话，美猴王高兴地说：“好，那我明天就出发，一定要找到这三种人中的一个，学个长生不老之道，省得冥王索我性命。”众猴听了，准备了丰盛的瓜果，给猴王送行。");
////        put("B2","第二天，猴王吃过瓜果，喝下猴子们送行的酒，便坐上枯松编成的木筏，顺着大海，朝南赡部洲方向划去。");
////        put("B3","在集市上，猴王努力地模仿人类的举止，同时寻访仙人。但在南赡部洲待了八九年的时间，猴王始终没有见到仙人的影子。");
////        put("B4","猴王来到樵夫跟前，大声说：“老神仙，弟子有礼了！”随即向樵夫行礼。樵夫见状，慌忙回答说：“罪过罪过，我一介贫夫，怎么敢称神仙呢？”");
////        put("B5","樵夫于是回答说：“实不相瞒，我唱的这些都是一位神仙教给我的。神仙见我终日劳苦，烦心事很多，就把这些歌谣教给我，让我在烦躁的时候唱给自己听");
////        put("B6","樵夫回答说：“他住得离这儿并不远。这附近有一座山叫灵台方寸山，上面有一座斜月三星洞，里面有一个叫菩提祖师的神仙，就是你要找的人。你顺着这条路往南走个七八里地，就到了。”");
////        put("B7","猴王告别樵夫，按照他说的路走出去七八里，果然来到了一座洞府跟前。只见这座洞府映照在霞光里，两旁长满松柏，寂静无人，偶尔有鹤鸣从里面传出。猴王来到门前，");
//            put("B8", "过了一会儿,对着空无一人的石阶喊了一声：“是什么人在门外胡闹？”，猴王见状，从树上跳下来说：“我是个学仙的弟子，特来拜师学艺。”童子说：“师父叫我来开门，说外面来了一个求师的，想必就是你吧，请跟我来。”猴王便跟着童子来到了洞府里面。");
////        put("B9","见到菩提老祖，美猴王跪在他跟前说：“师父，弟子有礼了！”菩提老祖说：“先别急着拜师，把你的来历告诉我再拜师也不迟。”于是，猴王便将自己的来历告诉了菩提老祖。菩提老祖又问他有没有父母和姓名，猴王说没有。");
////        put("B10","菩提老祖想了想，赐给他“孙悟空”一名。猴王非常喜欢这个名字，高兴地说：“好好好，以后我就叫孙悟空了！”");
//            put("B11", "拜师后，孙悟空每天和弟子们待在一起，打扫庭院，养花修树，挑水捡柴，学经论道，不知不觉过去了七年时间。一天，菩提老祖对孙悟空说：“‘道’字门中有三百六十旁门，只要悉心学习都可以学成正果，不知道你想学哪一门呢？”");
//            put("B12", "美猴王拜了师傅孙菩提以后，不得不说看起来更加勤奋自学了正果一门了,蚂蝗,还跟网友自学了虚拟网络世界自学了网游");
//            put("B13", "与幕后黑手撕破脸皮，依靠大师姐的合道状态斩杀幕后黑手，再借由溯源道果让大师姐跌落状态，一切都按照计划进行");
//            put("B14", "接下来，贾基直接念了一首诗:『富士见之女，于西行妖满开之时，即幽明境分开之时，为其魂魄，安息于白玉楼中，将西行妖之花封印作");
//            put("B15", "非UGC，仅用于起点文插图的点赞功能,subtype=1");
        put("B9", "见到菩提老祖，美猴王跪在他跟前说：“师父，弟子有礼了！”菩提老祖说：“先别急着拜师，把你的来历告诉我再拜师也不迟。”于是，猴王便将自己的来历告诉了菩提老祖。菩提老祖又问他有没有父母和姓名，猴王说没有。");
        put("B10", "菩提老祖想了想，赐给他“孙悟空”一名。猴王非常喜欢这个名字，高兴地说：“好好好，以后我就叫孙悟空了！”");
        put("B11", "拜师后，孙悟空每天和弟子们待在一起，打扫庭院，养花修树，挑水捡柴，学经论道，不知不觉过去了七年时间。一天，菩提老祖对孙悟空说：“‘道’字门中有三百六十旁门，只要悉心学习都可以学成正果，不知道你想学哪一门呢？”");
        put("B12", "美猴王拜了师傅孙菩提以后，必须说看起来更加勤奋学习正果一门了,蚂蝗,还跟网友自学虚拟网络世界自学网游");
        put("B13", "是张无忌把成昆打了一顿，赵敏说道");
        }
    };

    public static void main(String[] args) throws Exception {

        lcsSimCalc(text1, text2);
        levenshteinSimCalc(text1, text2);
        tfidfSimCalc(text1, text2);
        hanlpSimCalc(text1, text2);
        tfidfSimCalcV2(text1, text2);
    }

    public static void dloopBestMatchFunc(String tag, Map<String, String> text1, Map<String, String> text2, BiFunction<String, String, Double> func)
            throws Exception {
        for (String key1 : text1.keySet()) {
            String bestMatch = "-1";
            double bestScore = -1.0;
            for (String key2 : text2.keySet()) {
                double score = func.apply(text1.get(key1), text2.get(key2));
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = key2;
                }
            }
            System.out.println(String.format("%s:%s -> %s similarity: %.4f", tag, key1, bestMatch, bestScore));
        }
        System.out.println("------------");
    }

    static Set<String> detailSet = ImmutableSet.of("A11", "A14", "A15");

    public static void tfidfSimCalc(Map<String, String> text1, Map<String, String> text2) throws Exception {
        TFIDFByLucene.init("ansj", "nlp_ansj");
        Map<String, Map<CharSequence, Integer>> tfIdf1 = TFIDFByLucene.createTfIdf(text1);
        Map<String, Map<CharSequence, Integer>> tfIdf2 = TFIDFByLucene.createTfIdf(text2);
        for (String key1 : tfIdf1.keySet()) {
            String bestMatch = "-1";
            double bestScore = -1.0;
            for (String key2 : tfIdf2.keySet()) {
                if (detailSet.contains(key1)) {
                    Map<CharSequence, Integer> trmap1 = new TreeMap<>();
                    trmap1.putAll(tfIdf1.get(key1));
                    Map<CharSequence, Integer> trmap2 = new TreeMap<>();
                    trmap2.putAll(tfIdf2.get(key2));
                    System.out.println(String.format("%s-%s:\n%s\n%s\n----", key1, key2, trmap1, trmap2));
                }
                CosineSimilarity cosineSimilarity = new CosineSimilarity();
                double score = cosineSimilarity.cosineSimilarity(tfIdf1.get(key1), tfIdf2.get(key2));
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = key2;
                }
            }
            System.out.println(String.format("tfidf: %s -> %s with similarity: %.4f", key1, bestMatch, bestScore));
            // System.out.println(key1 + " -> " + bestMatch + " with similarity: " + bestScore);
        }
        System.out.println("------------");
    }
    public static void tfidfSimCalcV2(Map<String, String> text1, Map<String, String> text2) throws Exception {
        TFIDFByLucene.init("ansj", "nlp_ansj");
        dloopBestMatchFunc("simhash", text1, text2, (line1, line2) -> {
            SimHash hs1 = SimHash.of(TFIDFByLucene.splitWords(line1), (wd, idx)->{
                return TFIDFByLucene.WeightsMap.getOrDefault(wd.getType(), 1);
            });
            SimHash hs2 = SimHash.of(TFIDFByLucene.splitWords(line2), (wd, idx)->{
                return TFIDFByLucene.WeightsMap.getOrDefault(wd.getType(), 1);
            });
            double score = hs1.getSemblance(hs2);
            return score;
        });
    }
    public static void hanlpSimCalc(Map<String, String> text1, Map<String, String> text2) throws Exception {
        dloopBestMatchFunc("hanlp", text1, text2, (line1, line2) -> {
            SimHash hs1 = HanLpSimilarity.simHash(line1);
            SimHash hs2 = HanLpSimilarity.simHash(line2);
            double score = hs1.getSemblance(hs2);
            return score;
        });
    }

    public static void lcsSimCalc(Map<String, String> text1, Map<String, String> text2) throws Exception {
        dloopBestMatchFunc("lcs", text1, text2, (line1, line2) -> {
            return lcsSimilarity(line1, line2);
        });
    }

    public static void levenshteinSimCalc(Map<String, String> text1, Map<String, String> text2) throws Exception {
        dloopBestMatchFunc("lev", text1, text2, (line1, line2) -> {
            return levenshteinSimilarity(line1, line2);
        });
    }

    public static double levenshteinSimilarity(String s1, String s2) {
        if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2)) {
            return 0d;
        }
        return apacheLevenshtein(s1, s2);
    }

    public static double lcsSimilarity(String s1, String s2) {
        int lcsth = new LongestCommonSubsequence().apply(s1, s2);
        if (lcsth > 8) {
            return lcsth * 1d / Math.min(s1.length(), s2.length());
        }
        return lcsth * 1d / Math.max(s1.length(), s2.length());
    }

    public static double apacheLevenshtein(String s1, String s2) {
        return 1.0d - LevenshteinDistance.getDefaultInstance().apply(s1, s2) * 1.0d / Math.max(s1.length(), s2.length());
    }
}