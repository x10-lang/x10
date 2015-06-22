/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2015.
 */

import x10.matrix.Vector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;

/**
 * Parallel implementation of linear support vector machine,
 * with L2 regularization and a hinge loss function.
 */
public class SVM(N:Long) {
    /** Weights for each feature.  w(N-1) is the intercept. */
	public val w:DupVector(N);

    /** Construct a new SVM with (N-1) features, plus an intercept. */
    public def this(N:Long, places:PlaceGroup) {
        property(N);
        w = DupVector.make(N, places);
        w.init((i:Long)=>(i==N-1) ? /* intercept */ 1.0 : 0.0);
    }

    /**
     * Train this SVM using the given examples and labels, using gradient
     * descent over the entire batch of examples.
     * @param X the M training examples, each with N-1 features plus a bias of
     *   1.0 for the intercept
     * @param y the training labels, [0.0, 1.0]
     * @param initialStepSize the starting step size for gradient descent
     * @param iterations the number of iterations of gradient descent
     */
	public def train(X:DistBlockMatrix{self.N==this.N},
                     y:DistVector(X.M),
                     initialStepSize:Double,
                     regularization:Double,
                     iterations:Long):SVM{self==this} {
        val scaledLabel = DistVector.make(X.M);
        val wX = DistVector.make(X.M);
        val gradient = DupVector.make(X.N);

        for (iter in 1..iterations) {
            // hinge loss = max(0, 1 - (2y - 1) * w * x )
            wX.mult(X, w);
            for (i in 0..(X.M-1)) {
                val s = 2.0 * y(i) - 1.0;
                if (s * wX(i) < 1.0) {
                    scaledLabel(i) = -s;
                    //loss(i) = 1.0 - scaledLabel(i) * loss(i);
                } else {
                    scaledLabel(i) = 0.0;
                    //loss(i) = 0.0;
                }
            }

            // gradient = -(2y - 1) * x
            gradient.mult(scaledLabel, X).scale(1.0 / X.M);

            // gradient descent on weights: w = (1 - stepSize * reg) - stepSize * gradient
            val stepSize = initialStepSize / Math.sqrt(iter);
            w.map(w, gradient, (w_i:Double, grad_i:Double) => w_i * (1.0 - stepSize * regularization) - stepSize * grad_i);
        }

		return this;
	}

    /**
     * Compute a raw prediction score for the given example.  If score <= 0.0,
     * the example can be classified as negative; otherwise it is positive.
     * @param x a vector containing the N-1 features for the example, plus an
     *   appended 1.0 for the intercept
     * @param the raw prediction score (-1.0 <= score <= 1.0)
     */
    public def predict(x:Vector(N)):Double {
        return x.dotProd(w.local());
    }
}
