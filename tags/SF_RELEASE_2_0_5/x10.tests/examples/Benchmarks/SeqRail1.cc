/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

/**
 * @author bdlucas
 */

const int N = 10000000;
const int M = 20;

double expected() {return N*M;}
double operations() {return N*M;}
char *name = "SeqRail1";

double *a = new double[N+M];

void init() {
    for (int i=0; i<N+M; i++)
        a[i] = 1;
}

double once() {
    double sum = 0.0;
    for (int k=0; k<M; k++)
        for (int i=0; i<N; i++)
            sum += a[i+k];
    return sum;
}




