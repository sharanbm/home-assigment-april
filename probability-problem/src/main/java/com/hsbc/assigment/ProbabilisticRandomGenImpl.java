package com.hsbc.assigment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbabilisticRandomGenImpl implements ProbabilisticRandomGen {

    List<Float> listOfProbabilities = new ArrayList<>();
    List<Integer> drawables = new ArrayList<>();
    float sumOfAllProbabilities;
    Random random = new Random();

    /**
     *
     * @param numAndProbabilities
     *
     * ProbabilisticRandomGenImpl stores the distribution defined by NumAndProbability.
     *
     * The same distribution is then used whenever nextFromSample is invoked to draw the next possible sample.
     *
     * */
    public ProbabilisticRandomGenImpl(List<NumAndProbability> numAndProbabilities){
        for(NumAndProbability numAndProbability : numAndProbabilities){
            sumOfAllProbabilities += numAndProbability.getProbabilityOfSample();
            drawables.add(numAndProbability.getNumber());
            listOfProbabilities.add(numAndProbability.getProbabilityOfSample());
        }
    }

    /***
     *
     * @return
     *
     * next from sample method uses Probability function based on the distribution given as input.
     */

    @Override
    public int nextFromSample() {       
        double prob = random.nextDouble()*sumOfAllProbabilities;
        int i;
        for(i=0;prob>0;i++){
            prob-= listOfProbabilities.get(i);
        }
        return drawables.get(i-1);
    } 
}
