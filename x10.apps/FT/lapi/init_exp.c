#include <math.h>
#ifdef M_PI
#  define PI	M_PI
#elif !defined(PI)
#  define PI           3.14159265358979323846  
#endif



void init_exp(double *ex, double alpha, int THREADS, int MYTHREAD, int NX, int NY, int NZ)
{
  double   kon;
  double   *exptr;
  int i,j,k,t,n;
  int it,jt,kt;
  int i_start, i_end;
  double ex_0 = 1.0;
  double ex_1;
  double prev;
  double ii,jj;
  
  n=0;
  kon  = -4.*alpha*PI*PI;
  i_start = (NX/THREADS)*MYTHREAD;
  i_end = (NX/THREADS)*(MYTHREAD+1);
  

  for(i=i_start; i < i_end; i++) {
    if (i >= NX/2)
      it = i - NX;
    else
      it = i;
    ii = it*it;

    for(j=0; j < NY; j++) {
      if (j >= NY/2)
        jt = j - NY;
      else
        jt = j;
      jj = jt*jt;

      for (k=0; k < NZ; k++) {
        if (k >= NZ/2)
          kt = k - NZ;
	else
	  kt = k;
	ex[n++] = exp(kon*(ii + jj + kt*kt));
      }
    }
  }
}
