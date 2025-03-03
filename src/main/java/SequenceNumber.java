import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceNumber {

    private static ThreadLocal<Integer> seqNum = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return 0;
        }
    };

    public int getNextNum() {
        seqNum.set(seqNum.get() + 1);
        return seqNum.get();
    }

    public static void main(String[] args) {
        SequenceNumber sn = new SequenceNumber();
        TestClient t1 = new TestClient(sn);
        TestClient t2 = new TestClient(sn);
        TestClient t3 = new TestClient(sn);

        System.out.println("sn: --" + sn);
        t1.start();
        t2.start();
        t3.start();

        t1.print();
        t2.print();
        t3.print();
        
        int primint = 123;
        Map<String, String> nullmap = null;
        Map<String, String> map = new HashMap<>();
        String str = "hello"; //null;
        Integer integer = new Integer(12);
        Set<String> set = new HashSet<>();
        Set<String> nullset = null;
        AtomicInteger ati = new AtomicInteger(42);
        
        System.out.println(String.format("outer1\t: pi:%s, i:%s, str:%s,ati:%s,map:%s,\t\tnullmap:%s,\t\tset:%s,\t\tnullset:%s", primint, integer, str, ati, map, nullmap, set, nullset));
        configMap(primint, integer, str, ati, map, nullmap, set, nullset);
        System.out.println(String.format("outer2\t: pi:%s, i:%s, str:%s,ati:%s,map:%s,\t\tnullmap:%s,\t\tset:%s,\tnullset:%s", primint, integer, str, ati, map, nullmap, set, nullset));

    }

    public static void configMap(int primint, Integer integer, String str, AtomicInteger ati, Map<String, String> map, 
            Map<String, String> nullmap,  Set<String> set, Set<String> nullset) {
        primint = 456;
        integer = 34;
        str = "valur";
        // ati = new AtomicInteger(54);
        ati.incrementAndGet();
        
        map=new HashMap<>();
        map.put("23", "ff");
        
        nullmap=new HashMap<>();
        nullmap.put("null.23", "ff");
        
        set.add("element1");
        nullset = new HashSet<>();

        System.out.println(String.format("inner\t: pi:%s, i:%s, str:%s,ati:%s,map:%s,\t\tnullmap:%s,\tset:%s,\tnullset:%s", primint, integer, str, ati,  map, nullmap, set, nullset));
    }

    private static class TestClient extends Thread {
        private SequenceNumber sn;

        public TestClient(SequenceNumber sn) {
            this.sn = sn;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " --> " + sn.getNextNum() + "--" + sn);
            }
        }

        public void print() {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " p--> " + sn.getNextNum() + "--" + sn);
            }
        }
    }

}