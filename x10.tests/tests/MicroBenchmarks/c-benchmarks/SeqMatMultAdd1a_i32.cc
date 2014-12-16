/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <inttypes.h>

int32_t N = 55*5;

double operations() {return 1.0*N*N*N;}
double expected() {return -6866925;}
        
const char *name = "SeqMatMultAdd1a";


//
// the benchmark
//

const int32_t Na = N;
const int32_t Nb = N;
const int32_t Nc = N;

static double *a = new double[Na*Na];
static double *b = new double[Nb*Nb];
static double *c = new double[Nc*Nc];

void init() {
    for (int32_t i=0; i<Na; i++) {
        for (int32_t j=0; j<Na; j++) {
            a[i*Na+j] = i*j;
            b[i*Na+j] = i-j;
            c[i*Na+j] = i+j;
        }
    }
}

double once() {
    for (int32_t i=0; i<Na; i++)
        for (int32_t j=0; j<Nb; j++)
            for (int32_t k=0; k<Nc; k++)
                a[i*Na+j] += b[i*Nb+k]*c[k*Nc+j];
    return a[10*Na+10];
}

