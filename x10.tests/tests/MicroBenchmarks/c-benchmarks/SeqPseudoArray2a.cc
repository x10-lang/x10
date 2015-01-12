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
 * Simulated array code. This is intended to represent the best
 * possible C++ code for a 2-d double array class.
 *
 * The following tests all do essentially the same work and so ideally
 * should deliver the same performance: SeqRail2.*,
 * SeqPseudoArray2*.*, SeqArray2*.*.
 *
 * @author bdlucas
 */

//
// parameters
//

const int N = 2000;

double expected() {return (double)N*N*(N-1);}
double operations() {return 2.0*N*N;}
char *name = "SeqPseudoArray2a";

//
// the benchmark
//

class Arr {

    int m0;
    int m1;
    double *raw;
        
public:
    Arr(int m0, int m1) {
        this->m0 = m0;
        this->m1 = m1;
        this->raw = new double[m0*m1];
    }
        
    void set(double v, int i0, int i1) {
        raw[i0*m1+i1] = v;
    }
        
    double apply(int i0, int i1) {
        return raw[i0*m1+i1];
    }
};
    
Arr *a = new Arr(N, N);

double once() {
    for (int i=0; i<N; i++)
        for (int j=0; j<N; j++)
            a->set(i+j, i,j);
    double sum = 0.0;
    for (int i=0; i<N; i++)
        for (int j=0; j<N; j++)
            sum += a->apply(i,j);
    return sum;
}

void init() {}

