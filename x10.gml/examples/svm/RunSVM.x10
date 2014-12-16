/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Timer;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.regression.RegressionInputData;

/**
 * Test harness for Support Vector Machine using GML
 */
public class RunSVM {
    public static def main(args:Rail[String]):void {
        val opts = new OptionsParser(args, [
            Option("h","help","this information"),
            Option("l","libsvmInput","use libsvm input file format"),
            Option("v","verify","verify the parallel result against sequential computation"),
            Option("p","print","print matrix X, vector w on completion")
        ], [
            Option("f","inputFile","input file name"),
            Option("i","iterations","number of iterations, default = 30"),
            Option("t","trainingFraction","fraction of examples to be used as training data, default = 0.6"),
            Option("s","stepSize","initial step size, default = 1.0"),
            Option("r","regularization","regularization parameter, default = 0.1")
        ]);

        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts.wantsUsageOnly("Options:\n")) {
            return;
        }

        val inputFile = opts("f", "");
        if (inputFile.equals("")) {
            Console.ERR.println("svm: missing input file\ntry `svm -h ' for more information");
            System.setExitCode(1n);
            return;
        }
        val iterations = opts("i", 30n);
        val trainingFraction = opts("t", 0.6);
        val stepSize = opts("s", 1.0);
        val regularization = opts("r", 0.1);
        val libsvmInput = opts("l");
        val verify = opts("v");
        val print = opts("p");

        val addBias = true;
        val input = RegressionInputData.readFromFile(inputFile, libsvmInput, trainingFraction, addBias);

        Console.OUT.println("Training SVM with stepSize: " + stepSize
            + ", regularization: " + regularization
            + " for " + iterations + " iterations...");

        // examples have an additional feature appended for the intercept
        val X = new DenseMatrix(input.numTraining, input.numFeatures+1);
        X.init((i:Long, j:Long) => input.local().trainingExamples(i*(input.numFeatures+1)+j));
        val y = new Vector(X.M, input.local().trainingLabels.toRail());

        val seq = new SeqSVM(input.numFeatures+1).train(X, y, stepSize, regularization, iterations);

        Console.OUT.println("Attempting to predict " + input.numTest
            + " test examples...");

        var successful:Long = 0;
        for (i in 0..(input.numTest-1)) {
            val testExample = input.local().testExamples.moveSectionToRail(0, input.numFeatures);
            val test = new Vector(testExample);
            val raw = seq.predict(test);
            val predicted = (raw < 0.0 ? 0.0 : 1.0);
            val actual = input.local().testLabels(i);
            if (predicted == actual) successful++;
        }
        Console.OUT.printf("Success rate: %5.3f\n", successful as Double / input.numTest);
    }
}
