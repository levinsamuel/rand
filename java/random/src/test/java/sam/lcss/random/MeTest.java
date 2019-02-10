/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sam.lcss.random;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 *
 * @author Samuel
 */
public class MeTest {
    
    public MeTest() {
    }

    private static final String[] keys = {"big", "dog", "funny", "cat"};
    
    class KeyVal {
        String key;
        int val;

        public KeyVal() {
        
            this.key = keys[(int)(Math.random()*4)];
            this.val = (int)(Math.random()*30);
        }

        @Override
        public String toString() {
            return "key=" + key + ", val=" + val + '}';
        }

       
    }
    
    @Test
    public void testMapReduce() {
        
        KeyVal[] kvs = new KeyVal[10];
        Arrays.setAll(kvs, i->new KeyVal());
        System.out.println("input: " + Arrays.toString(kvs));
        
        Map<String, Integer> sums =
                Arrays.stream(kvs).collect(Collectors.groupingBy(
                        kv->kv.key,
                        Collectors.summingInt(kv->kv.val)
                ));
        System.out.println("sums: " + sums);
    }
    
}
