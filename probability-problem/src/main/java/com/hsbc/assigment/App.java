package com.hsbc.assigment;

import java.util.ArrayList;
import java.util.List;

import com.hsbc.assigment.ProbabilisticRandomGen.NumAndProbability;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        
        List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities = new ArrayList<>();
        numAndProbabilities.add(new NumAndProbability(2, (float)0.2));
        numAndProbabilities.add(new NumAndProbability(4, (float)0.5));
        numAndProbabilities.add(new NumAndProbability(6, (float)0.3));
        //EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(numsToGenerate, discreteProbabilities);

        ProbabilisticRandomGen distribution = new ProbabilisticRandomGenImpl(numAndProbabilities);

        for(int i=0; i< 10;i++) {
            System.out.println( " sample drawable : " +distribution.nextFromSample());
        }

    }

}
