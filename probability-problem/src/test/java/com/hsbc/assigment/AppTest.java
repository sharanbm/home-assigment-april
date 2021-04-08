package com.hsbc.assigment;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    ProbabilisticRandomGen probabilisticRandomGen;
    List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities;

    @Before
    public void setup() {
        numAndProbabilities = new ArrayList<>();
        numAndProbabilities.add(new ProbabilisticRandomGen.NumAndProbability(2, (float) 0.2));
        numAndProbabilities.add(new ProbabilisticRandomGen.NumAndProbability(4, (float) 0.5));
        numAndProbabilities.add(new ProbabilisticRandomGen.NumAndProbability(6, (float) 0.3));
    }

    @Test
    public void test_next_sample_from_distribution(){
        ProbabilisticRandomGen distribution = new ProbabilisticRandomGenImpl(numAndProbabilities);

        for(int i=0; i< 10;i++) {
            System.out.println( " sample drawable : " +distribution.nextFromSample());
        }
    }

    
}
