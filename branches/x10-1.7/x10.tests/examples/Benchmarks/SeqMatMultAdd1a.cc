// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

const int N = 55*5;

double operations() {return 1.0*N*N*N;}
double expected() {return -6866925;}
        
char *name = "SeqMatMultAdd1a";


//
// the benchmark
//

const int Na = N;
const int Nb = N;
const int Nc = N;

double * const a = new double[Na*Na];
double * const b = new double[Nb*Nb];
double * const c = new double[Nc*Nc];

void init() {
    for (int i=0; i<Na; i++) {
        for (int j=0; j<Na; j++) {
            a[i*Na+j] = i*j;
            b[i*Na+j] = i-j;
            c[i*Na+j] = i+j;
        }
    }
}

double once() {
    for (int i=0; i<Na; i++)
        for (int j=0; j<Nb; j++)
            for (int k=0; k<Nc; k++)
                a[i*Na+j] += b[i*Nb+k]*c[k*Nc+j];
    return a[10*Na+10];
}

