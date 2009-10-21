// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

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




