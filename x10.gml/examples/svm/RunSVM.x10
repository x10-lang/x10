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

import x10.io.File;
import x10.io.FileReader;
import x10.io.EOFException;
import x10.util.ArrayList;
import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

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
            Option("i","inputFile","input file name"),
            Option("n","iterations","number of iterations, default = 30"),
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

        val inputFile = opts("i", "");
        if (inputFile.equals("")) {
            Console.ERR.println("svm: missing input file\ntry `svm -h ' for more information");
            System.setExitCode(1n);
            return;
        }
        val iterations = opts("n", 30n);
        val trainingFraction = opts("t", 0.6);
        val stepSize = opts("s", 1.0);
        val regularization = opts("r", 0.1);
        val libsvmInput = opts("l");
        val verify = opts("v");
        val print = opts("p");

        val file = new FileReader(new File(inputFile));
        var line:String = file.readLine();
        val firstPoint = line.substring(line.indexOf(" ")+1n).split(" ");
        val numFeatures = firstPoint.size;

        val trainingLabels = new ArrayList[Double]();
        val trainingExamples = new ArrayList[Double]();
        val testLabels = new ArrayList[Double]();
        val testExamples = new ArrayList[Double]();

        val addPoint = (line:String, labels:ArrayList[Double], points:ArrayList[Double]) => {
            val fields = line.split(" ");
            var label:Double = Double.parseDouble(fields(0));
            if (label < 0.0) label = 0.0; // scale -1,1 to 0,1
            labels.add(label);
            for (i in 1..numFeatures) {
                if (libsvmInput) {
                    // libsvm uses the format "label 1:x1 2:x2 ... n:xn"
                    val separatorIndex = fields(i).indexOf(":");
                    val pointIndex = Double.parseDouble(fields(i).substring(0n,separatorIndex));
                    val pointVal = Double.parseDouble(fields(i).substring(separatorIndex+1n));
                    // TODO assume dense points (no missing indices)
                    points.add(pointVal);
                } else {
                    // assume format "label x0 x1 x2 ... xn"
                    points.add(Double.parseDouble(fields(i)));
                }
            }
            points.add(1.0); // append bias for the intercept
        };

        val rand = new Random();
        try {
            while (line != null && line.trim().length() > 0) {
                if (rand.nextDouble() < trainingFraction) {
                    addPoint(line, trainingLabels, trainingExamples);
                } else {
                    addPoint(line, testLabels, testExamples);
                }
                line = file.readLine();
            }
        } catch (eof:EOFException) {
            // no more examples
        }

        Console.OUT.println("Training SVM against " + trainingLabels.size()
            + " examples with " + numFeatures + " features...");
        Console.OUT.println("stepSize: " + stepSize
            + ", regularization: "
            + regularization + ", iterations: " + iterations);

        // examples have an additional feature appended for the intercept
        val X = new DenseMatrix(trainingLabels.size(), numFeatures+1);
        X.init((i:Long, j:Long) => trainingExamples(i*(numFeatures+1)+j));
        val y = new Vector(X.M, trainingLabels.toRail());

        val seq = new SeqSVM(numFeatures+1).train(X, y, stepSize, regularization, iterations);

        Console.OUT.println("Attempting to predict " + testLabels.size()
            + " test examples...");

        var successful:Long = 0;
        for (i in 0..(testLabels.size()-1)) {
            val testData = testExamples.moveSectionToRail(0, numFeatures);
            val test = new Vector(testData);
            val raw = seq.predict(test);
            val predicted = (raw < 0.0 ? 0.0 : 1.0);
            val actual = testLabels(i);
            if (predicted == actual) successful++;
        }
        Console.OUT.printf("Success rate: %5.3f\n", successful as Double / (testLabels.size()) );
    }
}
