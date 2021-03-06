package org.neuroph.contrib.eval;

import org.neuroph.contrib.eval.classification.ConfusionMatrix;
import org.neuroph.contrib.eval.classification.ClassificationMetrics;
import org.neuroph.contrib.eval.classification.Utils;
import org.neuroph.core.data.DataSet;

public abstract class ClassificationEvaluator implements Evaluator<ClassificationMetrics[]> {

    ConfusionMatrix confusionMatrix;
    private double threshold;
    
    

    private ClassificationEvaluator(String[] labels) {
        confusionMatrix = new ConfusionMatrix(labels, labels.length);
    }

    @Override
    public ClassificationMetrics[] getResult() {
        return  ClassificationMetrics.createFromMatrix(confusionMatrix);
    }

    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    
    
    
    

//    public static ClassificationEvaluator createForDataSet(final DataSet dataSet) {
//        if (dataSet.getOutputSize() == 1) {
//            //TODO how can we handle different thresholds??? - use thresholds for both binary and multiclass
//            return new Binary(0.5);
//        } else {
//            return new MultiClass(dataSet);
//        }
//    }


    /**
     * Binary evaluator used for computation of metrics in case when data has only one output result (one output neuron)
     */
    public static class Binary extends ClassificationEvaluator {

        public static final String[] BINARY_CLASS_LABELS = new String[]{"False", "True"};
        public static final int TRUE = 1;
        public static final int FALSE = 0;

        

        public Binary(double threshold) {
            super(BINARY_CLASS_LABELS);
            setThreshold(threshold);

        }

        @Override
        public void processNetworkResult(double[] networkOutput, double[] desiredOutput) {
            int actualClass = classForValueOf(desiredOutput[0]);
            int predictedClass = classForValueOf(networkOutput[0]);

            confusionMatrix.incrementElement(actualClass, predictedClass);
        }

        private int classForValueOf(double classResult) {
            int classValue = FALSE;
            if (classResult >= getThreshold()) {
                classValue = TRUE;
            }
            return classValue;
        }

    }

    /**
     * Evaluator used for computation of metrics in case when data has
     * multiple classes - one vs many classification
     */
    public static class MultiClass extends ClassificationEvaluator {

        // TODO: use column labels here
        public MultiClass(String[] classLabels) {            
            super(classLabels);
            // dataSet.getColumnNames()
        }

        @Override
        public void processNetworkResult(double[] predictedOutput, double[] actualOutput) {
            // just get max index
            int actualClassIdx = Utils.maxIdx(actualOutput);
            int predictedClassIdx = Utils.maxIdx(predictedOutput);

            confusionMatrix.incrementElement(actualClassIdx, predictedClassIdx);
        }
    }

}
