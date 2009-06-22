// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

#include <stdio.h>
#include "UTSRand.h"

// stats
int size = 0;
int sumb = 0;
bool first = true;

// parameters
const int r0 = 0;
const int b0 = 50000;
const double q = 0.12;
const int m = 8;

// info
double expected() {return 1234872.0;}
double operations() {return size;}
char *name = "SeqUTSBin1";

//
// the benchmark
//

void visit(UTSRand::descriptor r) {
    int b = UTSRand::number(r)<q? m : 0;
    sumb += b;
    size++;
    for (int i=0; i<b; i++)
        visit(UTSRand::next(r,i));
}
    
double once() {

    // root node
    size = 0;
    sumb = 0;
    for (int i=0; i<b0; i++)
        visit(UTSRand::next(r0,i));

    // sanity check
    if (first) {
        double expSize = b0 / (1.0 - q*m);
        double obsBranch = (double)sumb / size;
        double expBranch = q * m;
        printf("exp size / obs size: %.3f\n", expSize/size);
        printf("exp branching / obs branching: %.3f\n", expBranch / obsBranch);
    } 
    first = false;

    // should always get same size tree
    return size;
}

void init() {}
