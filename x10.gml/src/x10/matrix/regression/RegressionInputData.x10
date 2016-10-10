/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2016.
 */

package x10.matrix.regression;

import x10.io.File;
import x10.io.FileReader;
import x10.io.EOFException;
import x10.util.ArrayList;
import x10.util.Random;
import x10.matrix.util.StringTool;
import x10.matrix.util.RandTool;
import x10.matrix.ElemType;

/**
 * Holds labeled examples as input to a regression or classification algorithm.
 * Examples are divided into a training set and a test set.
 */
public class RegressionInputData(numFeatures:Long) {
    public var numTraining:Long;
    public var numTest:Long;

    public static class LocalData {
        public val trainingLabels = new ArrayList[ElemType]();
        public val trainingExamples = new ArrayList[ElemType]();
        public val testLabels = new ArrayList[ElemType]();
        public val testExamples = new ArrayList[ElemType]();
    }
    public val local:PlaceLocalHandle[LocalData];

    public def this(numFeatures:Long, places:PlaceGroup) {
        property(numFeatures);
        local = PlaceLocalHandle.make[LocalData](places, ()=> new LocalData());
    }

    /**
     * Read labeled points from a file, dividing them into a training set
     * and a test set according to the specified training fraction.
     * The file must contain one point per line, with the label (classification /
     * response variable) as the first field on each line.
     * Supports MLLib and libsvm formats.
     * @param fileName the input file name
     * @param libsvmFormat true if the input file is in libsvmFormat
     * @param trainingFraction the proportion of examples that should be assigned
     *     to the training set
     * @param addBias if true, append a bias column (all values 1.0) to the
     *     input examples
     */
    public static def readFromFile(fileName:String, places:PlaceGroup, libsvmFormat:Boolean, trainingFraction:ElemType, addBias:Boolean):RegressionInputData {
        val file = new File(fileName);
        val totalSize = file.size();

        val reader = new FileReader(file);
        var line:String = reader.readLine();
        val firstPoint = line.substring(line.indexOf(" ")+1n).split(" ");
        val numFeatures = firstPoint.size;

        val addPoint = (line:String, labels:ArrayList[ElemType], points:ArrayList[ElemType]) => {
            val fields = line.split(" ");
            var label:ElemType = StringTool.parse[ElemType](fields(0));
            if (label < (0 as ElemType)) label = 0 as ElemType; // scale -1,1 to 0,1
            labels.add(label);
            for (i in 1..numFeatures) {
                if (libsvmFormat) {
                    // libsvm uses the format "label 1:x1 2:x2 ... n:xn"
                    val separatorIndex = fields(i).indexOf(":");
                    val pointIndex = StringTool.parse[ElemType](fields(i).substring(0n,separatorIndex));
                    val pointVal = StringTool.parse[ElemType](fields(i).substring(separatorIndex+1n));
                    // TODO assume dense points (no missing indices)
                    points.add(pointVal);
                } else {
                    // assume MLLib format "label x0 x1 x2 ... xn"
                    points.add(StringTool.parse[ElemType](fields(i)));
                }
            }
            if (addBias) {
                points.add(1.0 as ElemType); // append bias for the intercept
            }
        };

        val trainingLabels = new ArrayList[ElemType]();
        val trainingExamples = new ArrayList[ElemType]();
        val testLabels = new ArrayList[ElemType]();
        val testExamples = new ArrayList[ElemType]();

        val rand = new Random();
        try {
            while (line != null && line.trim().length() > 0) {
                if (RandTool.nextElemType[ElemType](rand) < trainingFraction) {
                    addPoint(line, trainingLabels, trainingExamples);
                } else {
                    addPoint(line, testLabels, testExamples);
                }
                line = reader.readLine();
            }
        } catch (eof:EOFException) {
            // no more examples
        }

        val numFeaturesWithBias = addBias ? numFeatures+1 : numFeatures;
        return createRegressionData(numFeaturesWithBias, places,
            trainingLabels, trainingExamples, testLabels, testExamples);
    }

    private static def createRegressionData(numFeatures:Long, places:PlaceGroup,
        trainingLabels:ArrayList[ElemType], trainingExamples:ArrayList[ElemType],
        testLabels:ArrayList[ElemType], testExamples:ArrayList[ElemType]) {

        val data = new RegressionInputData(numFeatures-1, places);

        data.numTraining = trainingLabels.size();
        data.numTest = testLabels.size();

        Console.OUT.println(here + " input data with " + data.numFeatures + " features: "
            + data.numTraining + " training and "
            + data.numTest + " test examples.");

        val trainingChunkSize = data.numTraining / places.size();
        val trainingRemainder = data.numTraining % places.size();
        val testChunkSize = data.numTest / places.size();
        val testRemainder = data.numTest % places.size();
        finish for (place in places) {
            val trainingSize = places.indexOf(place) < trainingRemainder ? trainingChunkSize+1 : trainingChunkSize;
            val placeTrainingLabels = trainingLabels.moveSectionToRail(0, trainingSize-1);
            val placeTrainingExamples = trainingExamples.moveSectionToRail(0, trainingSize*numFeatures-1);
            val testSize = places.indexOf(place) < testRemainder ? testChunkSize+1 : testChunkSize;
            val placeTestLabels = testLabels.moveSectionToRail(0, testSize-1);
            val placeTestExamples = testExamples.moveSectionToRail(0, testSize*numFeatures-1);

            at(place) async {
                data.local().trainingLabels.addAll(placeTrainingLabels);
                data.local().trainingExamples.addAll(placeTrainingExamples);
                data.local().testLabels.addAll(placeTestLabels);
                data.local().testExamples.addAll(placeTestExamples);
            }
        }

        return data;
    }

    /**
     * Read labeled points from a MLLib-format file, assigning all points to
     * the training set at a single place.
     * @param fileName the input file name
     * @param libsvmFormat true if the input file is in libsvmFormat
     * @param trainingFraction the proportion of examples that should be assigned
     *     to the training set
     * @param addBias if true, append a bias column (all values 1.0) to the
     *     input examples
     */
    public static def readFromFile(fileName:String, libsvmFormat:Boolean, trainingFraction:ElemType, addBias:Boolean)
        = readFromFile(fileName, PlaceGroup.make(1), libsvmFormat, trainingFraction, addBias);

    /**
     * Read labeled points from a MLLib-format file, assigning all points to
     * the training set at a single place.
     * @param fileName the input file name
     */
    public static def readFromFile(fileName:String)
        = readFromFile(fileName, PlaceGroup.make(1), false, 1.0 as ElemType, true);

    /**
     * Read labeled points from a SystemML file, dividing them into a training set
     * and a test set according to the specified training fraction.
     * Assumes that the datafile is in CSV format, with metadata in a
     * separate file <fileName>.mtd.
     * @param fileName the input file name
     * @param trainingFraction the proportion of examples that should be assigned
     *     to the training set
     * @param addBias if true, append a bias column (all values 1.0) to the
     *     input examples
     * @see https://systemml.apache.org/
     * TODO assumes dense matrix
     */
    public static def readFromSystemMLFile(featuresFileName:String, labelsFileName:String, places:PlaceGroup, trainingFraction:ElemType, addBias:Boolean):RegressionInputData {

        val featuresMetadataFile = new File(featuresFileName+".mtd");
        val featuresMetadataReader = new FileReader(featuresMetadataFile);
        var numExamples:Long = 0n;
        var N:Long = 0n;

        var line:String = featuresMetadataReader.readLine();
        try {
            while (line != null && line.trim().length() > 0) {
                if (line.contains("\"rows\"")) {
                    numExamples = Long.parseLong(line.substring(line.lastIndexOf(":")+1n, line.lastIndexOf(",")).trim());
                } else if (line.contains("\"cols\"")) {
                    N = Long.parseLong(line.substring(line.lastIndexOf(":")+1n, line.lastIndexOf(",")).trim());
                }
                line = featuresMetadataReader.readLine();
            }
        } catch (eof:EOFException) {
            // no more examples
        }
        val numFeatures = N;

        val featuresFile = new File(featuresFileName);
        val featuresReader = new FileReader(featuresFile);
        val labelsFile = new File(labelsFileName);
        val labelsReader = new FileReader(labelsFile);

        val addPoint = (labelLine:String, featuresLine:String, labels:ArrayList[ElemType], points:ArrayList[ElemType]) => {
            var label:ElemType = StringTool.parse[ElemType](labelLine);
            labels.add(label);
            val rawFeatures = featuresLine.split(",");
            for (n in 0..(numFeatures-1)) {
                val feature = StringTool.parse[ElemType](rawFeatures(n));
                points.add(feature);
            }
            if (addBias) {
                points.add(1.0 as ElemType); // append bias for the intercept
            }
        };

        val trainingLabels = new ArrayList[ElemType]();
        val trainingExamples = new ArrayList[ElemType]();
        val testLabels = new ArrayList[ElemType]();
        val testExamples = new ArrayList[ElemType]();

        val rand = new Random();
        try {
            line = labelsReader.readLine();
            while (line != null && line.trim().length() > 0) {
                if (RandTool.nextElemType[ElemType](rand) < trainingFraction) {
                    addPoint(line, featuresReader.readLine(), trainingLabels, trainingExamples);
                } else {
                    addPoint(line, featuresReader.readLine(), testLabels, testExamples);
                }
                line = labelsReader.readLine();
            }
        } catch (eof:EOFException) {
            // no more examples
        }

        val numFeaturesWithBias = addBias ? numFeatures+1 : numFeatures;
        return createRegressionData(numFeaturesWithBias, places,
            trainingLabels, trainingExamples, testLabels, testExamples);
    }
}
