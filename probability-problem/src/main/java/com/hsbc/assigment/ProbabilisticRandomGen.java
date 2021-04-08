package com.hsbc.assigment;

public interface ProbabilisticRandomGen {
    int nextFromSample();

    class NumAndProbability {
        private final int number;
        private final float probabilityOfSample;

        public NumAndProbability(int number, float probabilityOfSample) {
            this.number = number;
            this.probabilityOfSample = probabilityOfSample;
        }

        public int getNumber() {
            return number;
        }

        public float getProbabilityOfSample() {
            return probabilityOfSample;
        }

        @Override
        public String toString() {
            return "NumAndProbability [number=" + number + ", probabilityOfSample=" + probabilityOfSample + "]";
        }

        
    }

}
