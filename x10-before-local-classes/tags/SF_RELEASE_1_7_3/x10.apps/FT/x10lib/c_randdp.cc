#include <inttypes.h>
#include <stdio.h>

#if 0
#define r23 pow(0.5, 23.0)
#define r46 (r23*r23)
#define t23 pow(2.0, 23.0)
#define t46 (t23*t23)
#else
#define r23 (0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5*0.5)
#define r46 (r23*r23)
#define t23 (2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0*2.0)
#define t46 (t23*t23)
#endif

double	    tran = 314159265.0;
double	    d2m46;
//uint64_t    i246m1; 
long    i246m1; 
double	    rand_seed;    


void init_seed(double sd) {
  rand_seed = sd;
  d2m46 = r46;

  i246m1 = (1ULL<<46) - 1;
}

double randlc (double *x, double a) 
{
  /*simple used to modify the seed?!*/
  double t1,t2,t3,t4,a1,a2,x1,x2,z;
  double ret;
  t1 = r23 * a;
  a1 = (int)t1;
  a2 = a - t23 * a1;
  
  t1 = r23 * (*x);
  x1 = (int)t1;
  x2 = (*x) - t23 * x1;
  t1 = a1 * x2 + a2 * x1;
  t2 = (int)(r23 * t1);
  z = t1 - t23 * t2;
  t3 = t23 * z + a2 * x2;
  t4 = (int)(r46 * t3);
  *x = t3 - t46 * t4;
  ret = r46 * (*x);
  return ret;

}
void vranlc(int n, double *x, double a, double *y) {
  double t1,t2,t3,t4,a1,a2,x1,x2,z;
  double ret;
  int i;
    
  t1 = r23 * a;
  a1 = (int)t1;
  a2 = a - t23 * a1;

  for(i=0; i<n; i++) {
    t1 = r23 * (*x);
    x1 = (int)t1;
    x2 = (*x) - t23 * x1;
    t1 = a1 * x2 + a2 * x1;
    t2 = (int)(r23 * t1);
    z = t1 - t23 * t2;
    t3 = t23 * z + a2 * x2;
    t4 = (int)(r46 * t3);
    *x = t3 - t46 * t4;
    y[i] = r46 * (*x);
  }
  return;
}


double ipow46(double a, int exp_1, int exp_2) {
  double result;
  double dummy;
  double q;
  double r;
  int n;
  int n2;
  int ierr;
  int two_pow;
 
  result = 1;
  if(exp_1 == 0 || exp_2 == 0) return result;
  q = a;
  r = 1;
  n = exp_1;
  two_pow= 1;
  while(two_pow==1) {
    n2 = n/2;
    if(n2*2 == n ) {
      dummy = randlc(&q,q);
      n = n2;
    } else {
      n = n*exp_2;
      two_pow = 0;
    }
  } 
  while(n > 1) {
    n2 = n/2;
    if(n2*2 == n) {
      dummy = randlc(&q,q);
      n = n2;
    } else {
      dummy = randlc(&r,q);
      n = n-1;
    } 
  }
  dummy = randlc(&r,q);
  result = r;
  return result;
}
