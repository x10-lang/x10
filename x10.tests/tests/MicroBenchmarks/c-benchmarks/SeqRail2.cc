/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

/**
 * Basic rail performance test. Allocate a rail, assign values to each
 * element, read it back.
 *
 * Test is written to simulate treating the rail as a 2-d array in
 * order to facilitate comparisons with array performance tests
 * SeqPseudoArray2*.* and SeqArray2*.*.
 *
 * The following tests all do essentially the same work and so ideally
 * should deliver the same performance: SeqRail2.*,
 * SeqPseudoArray2*.*, SeqArray2*.*.
 *
 * @author bdlucas
 */

const int N = 2000;

double expected() {return (double)N*N*(N-1);}
double operations() {return 2.0*N*N;}
char *name = "SeqRail2";

double *a = new double[N*N];

void init() {}

double once() {
    for (int i=0; i<N; i++)
        for (int j=0; j<N; j++)
            a[i*N+j] = i+j;
    double sum = 0.0;
    for (int i=0; i<N; i++)
        for (int j=0; j<N; j++)
            sum += a[i*N+j];
    return sum;
}


