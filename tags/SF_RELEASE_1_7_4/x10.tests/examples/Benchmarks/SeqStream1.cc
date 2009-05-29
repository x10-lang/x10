const double alpha = 1.5;
const double beta = 2.5;
const double gamma = 3.0;

const int NUM_TIMES = 10;
const int PARALLELISM = 2;
const int localSize = 512*1024;

double operations() {return 1.0 * localSize * PARALLELISM * NUM_TIMES;}
double expected() {return (localSize+1)*(alpha+gamma*beta);}

char *name = "SeqStream1";

//
//
//

double **as = new double*[PARALLELISM];
double **bs = new double*[PARALLELISM];
double **cs = new double*[PARALLELISM];

void init() {
    for (int p=0; p<PARALLELISM; p++) {
        as[p] = new double[localSize];
        bs[p] = new double[localSize];
        cs[p] = new double[localSize];
        for (int i=0; i<localSize; i++) {
            bs[p][i] = alpha*(p*localSize+i);
            cs[p][i] = beta*(p*localSize+i);
        }
    }
}

double once() {
    for (int p=0; p<PARALLELISM; p++) {
        double * const a = as[p];
        double * const b = bs[p];
        double * const c = cs[p];
        for (int t=0; t<NUM_TIMES; t++)
            for (int i=0; i<localSize; i++)
                a[i] = b[i] + gamma*c[i];
    }
    return as[1][1];
}

