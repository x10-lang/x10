
/**************************************************************
 * kube-gustavson-fft.c
 *
 * Forward and inverse discrete 2D Fourier transforms.
 *
 * Originally a FORTRAN implementation published in:
 * Programming for Digital Signal Processing, IEEE Press 1979,
 * Section 1, by G. D. Bergland and M. T. Dolan.
 * Ported long ago from Fortran to old-fashioned Standard C by
 * someone who failed to put his or her name in the source.
 * Ported from Standard C to ANSI C by Stefan Gustavson
 * (stegu@itn.liu.se) 2003-10-20.
 *
 * This is a pretty fast FFT algorithm. Not super-optimized
 * lightning-fast, but very good considering its age and
 * relative simplicity. There are several places in the code
 * where clock cycles could be saved, for example by getting rid
 * of some copying of data back and forth, but the savings
 * would not be that great. If you want optimally fast FFTs,
 * consider the excellent FFTW package. (http://www.fftw.org)
 *
 * This software is in the public domain.
 *
 **************************************************************
 *
 * int forward_fft2f(COMPLEX *array, int rows, int cols)
 * int inverse_fft2f(COMPLEX *array, int rows, int cols)
 * int forward_fft2d(DCOMPLEX *array, int rows, int cols)
 * int inverse_fft2d(DCOMPLEX *array, int rows, int cols)
 *
 * These functions compute the forward and inverse DFT's, respectively, 
 * of a single-precision COMPLEX or DCOMPLEX array by means of an
 * FFT algorithm.
 *
 * The result is a COMPLEX/DCOMPLEX array of the same size, returned
 * in the same space as the input array.  That is, the original array
 * is overwritten and destroyed.
 *
 * Rows and columns must each be an integral power of 2.
 *
 * These routines return integer value ERROR if an error was detected,
 * NO_ERROR otherwise.
 *
 * This particular implementation of the DFT uses the transform pair
 * defined as follows:
 * Let there be two complex arrays each with n rows and m columns.
 * Index them as 
 * f(x,y):    0 <= x <= m - 1,  0 <= y <= n - 1
 * F(u,v):    -m/2 <= u <= m/2 - 1,  -n/2 <= v <= n/2 - 1
 *
 * Then the forward and inverse transforms are related as follows.
 * Forward:
 * F(u,v) = \sum_{x=0}^{m-1} \sum_{y=0}^{n-1} 
 *                      f(x,y) \exp{-2\pi i (ux/m + vy/n)}
 *
 * Inverse:
 * f(x,y) = 1/(mn) \sum_{u=-m/2}^{m/2-1} \sum_{v=-n/2}^{n/2-1} 
 *                      F(u,v) \exp{2\pi i (ux/m + vy/n)}
 *  
 * Therefore, the transforms have these properties:
 * 1.  \sum_x \sum_y  f(x,y) = F(0,0)
 * 2.  m n \sum_x \sum_y |f(x,y)|^2 = \sum_u \sum_v |F(u,v)|^2
 *
 */

#include <stdio.h>
#include <math.h>
#include <malloc.h>

#include "kube-gustavson-fft.h"

/*
 *	These somewhat awkward macros move data between the input/output array
 *  and stageBuff, while also performing any conversions float<->double.
 */

#define LoadRow(buffer,row,cols) if (1){\
    register int j,k;\
    for (j = row*cols, k = 0 ; k < cols ; j++, k++){\
      	stageBuff[k].re = buffer[j].re;\
    	  stageBuff[k].im = buffer[j].im;\
    	}\
    } else {}

#define StoreRow(buffer,row,cols) if (1){\
    register int j,k;\
    for (j = row*cols, k = 0 ; k < cols ; j++, k++){\
    	buffer[j].re = stageBuff[k].re;\
    	buffer[j].im = stageBuff[k].im;\
    	}\
    } else {}

#define LoadCol(buffer,col,rows,cols) if (1){\
    register int j,k;\
    for (j = i,k = 0 ; k < rows ; j+=cols, k++){\
    	stageBuff[k].re = buffer[j].re;\
    	stageBuff[k].im = buffer[j].im;\
    	}\
    } else {}

#define StoreCol(buffer,col,rows,cols) if (1){\
    register int j,k;\
    for (j = i,k = 0 ; k < rows ; j+=cols, k++){\
    	buffer[j].re = stageBuff[k].re;\
    	buffer[j].im = stageBuff[k].im;\
    	}\
    } else {}


/* Do something useful with an error message */
#define handle_error(msg) fprintf(stderr,msg)

DCOMPLEX *stageBuff;  /* buffer to hold a row or column at a time */
COMPLEX 	*bigBuff;    /* a pointer to a float input array */
DCOMPLEX *bigBuffd;   /* a pointer to a double input array */


/* Allocate space for stageBuff */
int allocateBuffer(int size)
{
  stageBuff = (DCOMPLEX*) malloc(size*sizeof(DCOMPLEX));
  if(stageBuff==(DCOMPLEX*)NULL) 
    {
      handle_error("Insufficient storage for fft buffers");
      return(ERROR);
    }
  else return(NO_ERROR);
}


/* Free space for stageBuff */
void freeBuffer(void)
{
  if(stageBuff!=NULL)
    free((char*)stageBuff);
}


/* See if exactly one bit is set in integer argument */
int power_of_2(int n)
{
  int bits=0;
  while(n) {
    bits += n & 1;
    n >>= 1;
  }
  return(bits==1);
}


/* Get binary log of integer argument - exact if n is a power of 2 */
int fastlog2(int n)
{
  int log = -1;
  while(n) {
    log++;
    n >>= 1;
  }
  return(log);
}


/* Radix-2 iteration subroutine */
void R2TX(int nthpo, DCOMPLEX *c0, DCOMPLEX *c1)
{
  int k,kk;
  double *cr0, *ci0, *cr1, *ci1, r1, fi1;

  cr0 = &(c0[0].re);
  ci0 = &(c0[0].im);
  cr1 = &(c1[0].re);
  ci1 = &(c1[0].im);
  
  for(k=0; k<nthpo; k+=2) 
    {
      kk = k*2;

      r1 = cr0[kk] + cr1[kk];
      cr1[kk] = cr0[kk] - cr1[kk];
      cr0[kk] = r1;
      fi1 = ci0[kk] + ci1[kk];
      ci1[kk] = ci0[kk] - ci1[kk];
      ci0[kk] = fi1;
    }
}


/* Radix-4 iteration subroutine */
void R4TX(int nthpo, DCOMPLEX *c0, DCOMPLEX *c1,
          DCOMPLEX *c2, DCOMPLEX *c3)
{
  int k,kk;
  double *cr0, *ci0, *cr1, *ci1, *cr2, *ci2, *cr3, *ci3;
  double r1, r2, r3, r4, i1, i2, i3, i4;

  cr0 = &(c0[0].re);
  cr1 = &(c1[0].re);
  cr2 = &(c2[0].re);
  cr3 = &(c3[0].re);
  ci0 = &(c0[0].im);
  ci1 = &(c1[0].im);
  ci2 = &(c2[0].im);
  ci3 = &(c3[0].im);
  
  for(k=1;k<=nthpo;k+=4) 
    {
      kk = (k-1)*2;  /* real and imag parts alternate */
      
      r1 = cr0[kk] + cr2[kk];
      r2 = cr0[kk] - cr2[kk];
      r3 = cr1[kk] + cr3[kk];
      r4 = cr1[kk] - cr3[kk];
      i1 = ci0[kk] + ci2[kk];
      i2 = ci0[kk] - ci2[kk];
      i3 = ci1[kk] + ci3[kk];
      i4 = ci1[kk] - ci3[kk];
      cr0[kk] = r1 + r3;
      ci0[kk] = i1 + i3;
      cr1[kk] = r1 - r3;
      ci1[kk] = i1 - i3;
      cr2[kk] = r2 - i4;
      ci2[kk] = i2 + r4;
      cr3[kk] = r2 + i4;
      ci3[kk] = i2 - r4;
    }
}

	
/* Radix-8 iteration subroutine */
void R8TX(int nxtlt, int nthpo, int length,
          DCOMPLEX *cc0, DCOMPLEX *cc1, DCOMPLEX *cc2, DCOMPLEX *cc3,
          DCOMPLEX *cc4, DCOMPLEX *cc5, DCOMPLEX *cc6, DCOMPLEX *cc7)
{
  int j,k,kk;
  double scale, arg, tr, ti;
  double c1, c2, c3, c4, c5, c6, c7;
  double s1, s2, s3, s4, s5, s6, s7;
  double ar0, ar1, ar2, ar3, ar4, ar5, ar6, ar7;
  double ai0, ai1, ai2, ai3, ai4, ai5, ai6, ai7;
  double br0, br1, br2, br3, br4, br5, br6, br7;
  double bi0, bi1, bi2, bi3, bi4, bi5, bi6, bi7;
  
  double *cr0, *cr1, *cr2, *cr3, *cr4, *cr5, *cr6, *cr7;
  double *ci0, *ci1, *ci2, *ci3, *ci4, *ci5, *ci6, *ci7;

  cr0 = &(cc0[0].re);
  cr1 = &(cc1[0].re);
  cr2 = &(cc2[0].re);
  cr3 = &(cc3[0].re);
  cr4 = &(cc4[0].re);
  cr5 = &(cc5[0].re);
  cr6 = &(cc6[0].re);
  cr7 = &(cc7[0].re);

  ci0 = &(cc0[0].im);
  ci1 = &(cc1[0].im);
  ci2 = &(cc2[0].im);
  ci3 = &(cc3[0].im);
  ci4 = &(cc4[0].im);
  ci5 = &(cc5[0].im);
  ci6 = &(cc6[0].im);
  ci7 = &(cc7[0].im);

  scale = TWOPI/length;

  for(j=1; j<=nxtlt; j++) 
    {
      arg = (j-1)*scale;
      c1 = cos(arg);
      s1 = sin(arg);
      c2 = c1*c1 - s1*s1;
      s2 = c1*s1 + c1*s1;
      c3 = c1*c2 - s1*s2;
      s3 = c2*s1 + s2*c1;
      c4 = c2*c2 - s2*s2;
      s4 = c2*s2 + c2*s2;
      c5 = c2*c3 - s2*s3;
      s5 = c3*s2 + s3*c2;
      c6 = c3*c3 - s3*s3;
      s6 = c3*s3 + c3*s3;
      c7 = c3*c4 - s3*s4;
      s7 = c4*s3 + s4*c3;

      for(k=j;k<=nthpo;k+=length) 
      	{
      	  kk = (k-1)*2; /* index by twos; re & im alternate */
      	  
      	  ar0 = cr0[kk] + cr4[kk];
      	  ar1 = cr1[kk] + cr5[kk];
      	  ar2 = cr2[kk] + cr6[kk];
      	  ar3 = cr3[kk] + cr7[kk];
      	  ar4 = cr0[kk] - cr4[kk];
      	  ar5 = cr1[kk] - cr5[kk];
      	  ar6 = cr2[kk] - cr6[kk];
      	  ar7 = cr3[kk] - cr7[kk];
      	  ai0 = ci0[kk] + ci4[kk];
      	  ai1 = ci1[kk] + ci5[kk];
      	  ai2 = ci2[kk] + ci6[kk];
      	  ai3 = ci3[kk] + ci7[kk];
      	  ai4 = ci0[kk] - ci4[kk];
      	  ai5 = ci1[kk] - ci5[kk];
      	  ai6 = ci2[kk] - ci6[kk];
      	  ai7 = ci3[kk] - ci7[kk];
      	  br0 = ar0 + ar2;
      	  br1 = ar1 + ar3;
      	  br2 = ar0 - ar2;
      	  br3 = ar1 - ar3;
      	  br4 = ar4 - ai6;
      	  br5 = ar5 - ai7;
      	  br6 = ar4 + ai6;
      	  br7 = ar5 + ai7;
      	  bi0 = ai0 + ai2;
      	  bi1 = ai1 + ai3;
      	  bi2 = ai0 - ai2;
      	  bi3 = ai1 - ai3;
      	  bi4 = ai4 + ar6;
      	  bi5 = ai5 + ar7;
      	  bi6 = ai4 - ar6;
      	  bi7 = ai5 - ar7;
      	  cr0[kk] = br0 + br1;
      	  ci0[kk] = bi0 + bi1;
      	  if(j>1) 
      	    {
      	      cr1[kk] = c4*(br0-br1) - s4*(bi0-bi1);
      	      cr2[kk] = c2*(br2-bi3) - s2*(bi2+br3);
      	      cr3[kk] = c6*(br2+bi3) - s6*(bi2-br3);
      	      ci1[kk] = c4*(bi0-bi1) + s4*(br0-br1);
      	      ci2[kk] = c2*(bi2+br3) + s2*(br2-bi3);
      	      ci3[kk] = c6*(bi2-br3) + s6*(br2+bi3);
      	      tr = IRT2*(br5-bi5);
      	      ti = IRT2*(br5+bi5);
      	      cr4[kk] = c1*(br4+tr) - s1*(bi4+ti);
      	      ci4[kk] = c1*(bi4+ti) + s1*(br4+tr);
      	      cr5[kk] = c5*(br4-tr) - s5*(bi4-ti);
      	      ci5[kk] = c5*(bi4-ti) + s5*(br4-tr);
      	      tr = -IRT2*(br7+bi7);
      	      ti = IRT2*(br7-bi7);
      	      cr6[kk] = c3*(br6+tr) - s3*(bi6+ti);
      	      ci6[kk] = c3*(bi6+ti) + s3*(br6+tr);
      	      cr7[kk] = c7*(br6-tr) - s7*(bi6-ti);
      	      ci7[kk] = c7*(bi6-ti) + s7*(br6-tr);
      	    }
      	  else 
      	    {
      	      cr1[kk] = br0 - br1;
      	      cr2[kk] = br2 - bi3;
      	      cr3[kk] = br2 + bi3;
      	      ci1[kk] = bi0 - bi1;
      	      ci2[kk] = bi2 + br3;
      	      ci3[kk] = bi2 - br3;
      	      tr = IRT2*(br5-bi5);
      	      ti = IRT2*(br5+bi5);
      	      cr4[kk] = br4 + tr;
      	      ci4[kk] = bi4 + ti;
      	      cr5[kk] = br4 - tr;
      	      ci5[kk] = bi4 - ti;
      	      tr = -IRT2*(br7+bi7);
      	      ti = IRT2*(br7-bi7);
      	      cr6[kk] = br6 + tr;
      	      ci6[kk] = bi6 + ti;
      	      cr7[kk] = br6 - tr;
      	      ci7[kk] = bi6 - ti;
      	    }
      	}
    }
}


/*
 * FFT842 (Name kept from the original Fortran version)
 * This routine replaces the input DCOMPLEX vector by its
 * finite discrete complex fourier transform if in==FFT_FORWARD.
 * It replaces the input DCOMPLEX vector by its finite discrete
 * complex inverse fourier transform if in==FFT_INVERSE.
 *
 * The implementation is a radix-2 FFT, but with faster shortcuts for
 * radix-4 and radix-8. It performs as many radix-8 iterations as
 * possible, and then finishes with a radix-2 or -4 iteration if needed.
 */
void FFT842(int direction, int n, DCOMPLEX *b)
/* direction: FFT_FORWARD or FFT_INVERSE
 * n:  length of vector
 * *b: input vector */
{
  double fn, r, fi;

  int L[16],L1,L2,L3,L4,L5,L6,L7,L8,L9,L10,L11,L12,L13,L14,L15;
/*  int j0,j1,j2,j3,j4,j5,j6,j7,j8,j9,j10,j11,j12,j13,j14;*/
  int j1,j2,j3,j4,j5,j6,j7,j8,j9,j10,j11,j12,j13,j14;
  int i, j, ij, ji, ij1, ji1;
/*  int nn, n2pow, n8pow, nthpo, ipass, nxtlt, length;*/
  int n2pow, n8pow, nthpo, ipass, nxtlt, length;

  n2pow = fastlog2(n);
  nthpo = n;
  fn = 1.0 / (double)nthpo; /* Scaling factor for inverse transform */
    
  if(direction==FFT_FORWARD) 
    /* Conjugate the input */
    for(i=0;i<n;i++) {
      b[i].im = -b[i].im;
    }
  
  if(direction==FFT_INVERSE)
    /* Scramble the inputs */
    for(i=0,j=n/2;j<n;i++,j++)
      {
      	r = b[j].re; fi = b[j].im;
      	b[j].re = b[i].re; b[j].im = b[i].im;
      	b[i].re = r; b[i].im = fi;
      }
  
  n8pow = n2pow/3;
  
  if(n8pow)
    {
      /* Radix 8 iterations */
      for(ipass=1;ipass<=n8pow;ipass++) 
      	{
      	  nxtlt = 0x1 << (n2pow - 3*ipass);
      	  length = 8*nxtlt;
      	  R8TX(nxtlt, nthpo, length,
      	       b, b+nxtlt, b+2*nxtlt, b+3*nxtlt,
               b+4*nxtlt, b+5*nxtlt, b+6*nxtlt, b+7*nxtlt);
      	}
    }
  
  if(n2pow%3 == 1) 
    {
      /* A final radix 2 iteration is needed */
    	R2TX(nthpo, b, b+1); 
    }
  
  if(n2pow%3 == 2)  
    {
      /* A final radix 4 iteration is needed */
      R4TX(nthpo, b, b+1, b+2, b+3); 
    }
  
  for(j=1;j<=15;j++) 
    {
      L[j] = 1;
      if(j-n2pow <= 0) L[j] = 0x1 << (n2pow + 1 - j);
    }

  L15=L[1];L14=L[2];L13=L[3];L12=L[4];L11=L[5];L10=L[6];L9=L[7];
  L8=L[8];L7=L[9];L6=L[10];L5=L[11];L4=L[12];L3=L[13];L2=L[14];L1=L[15];

  ij = 1;
    
  for(j1=1;j1<=L1;j1++)
    for(j2=j1;j2<=L2;j2+=L1)
      for(j3=j2;j3<=L3;j3+=L2)
        for(j4=j3;j4<=L4;j4+=L3)
          for(j5=j4;j5<=L5;j5+=L4)
            for(j6=j5;j6<=L6;j6+=L5)
              for(j7=j6;j7<=L7;j7+=L6)
                for(j8=j7;j8<=L8;j8+=L7)
                  for(j9=j8;j9<=L9;j9+=L8)
                    for(j10=j9;j10<=L10;j10+=L9)
                      for(j11=j10;j11<=L11;j11+=L10)
                        for(j12=j11;j12<=L12;j12+=L11)
                          for(j13=j12;j13<=L13;j13+=L12)
                            for(j14=j13;j14<=L14;j14+=L13)
                              for(ji=j14;ji<=L15;ji+=L14) 
                                {
                                  ij1 = ij-1;
                                  ji1 = ji-1;     
                                  if(ij-ji<0)
                                  	{
                                  	  r = b[ij1].re;
                                  	  b[ij1].re = b[ji1].re;
                                  	  b[ji1].re = r;
                                  	  fi = b[ij1].im;
                                  	  b[ij1].im = b[ji1].im;
                                  	  b[ji1].im = fi;
                                  	}
                                  ij++;
                                }

  /* if(direction==FFT_FORWARD)  /\* Take conjugates & unscramble outputs *\/ */
/*     for(i=0,j=n/2; j<n; i++,j++) */
/*     	{ */
/*     	  r = b[j].re; fi = b[j].im; */
/*     	  b[j].re = b[i].re; b[j].im = -b[i].im; */
/*     	  b[i].re = r; b[i].im = -fi; */
/*     	} */
  if(direction==FFT_FORWARD)
    for(j=0; j<n; j++) {
      b[j].im = -b[j].im;
    }
  
  if(direction==FFT_INVERSE) /* Scale outputs */
    for(i=0; i<nthpo; i++) 
      {
      	b[i].re *= fn;
      	b[i].im *= fn;
	if(i%2==1) {
	  b[i].re = -b[i].re;
	  b[i].im = -b[i].im;
	} 
	}
}


/*
 * Compute 2D DFT on float data:
 * forward if direction==FFT_FORWARD,
 * inverse if direction==FFT_INVERSE.
 */
int fft2f(COMPLEX *array, int rows, int cols, int direction)
{
  int i, maxsize, errflag;
  
  if(!power_of_2(rows) || !power_of_2(cols)) {
    handle_error("fft: input array must have dimensions a power of 2");
    return(ERROR);
  }
  
  /* Allocate 1D buffer */
  bigBuff = array;
  maxsize = rows>cols ? rows : cols;
  errflag = allocateBuffer(maxsize);
  if(errflag != NO_ERROR) return(errflag);

  /* Compute transform row by row */
  if(cols>1)
    for(i=0;i<rows;i++) 
      {
      	LoadRow(bigBuff,i,cols);
      	FFT842(direction,cols,stageBuff);
      	StoreRow(bigBuff,i,cols);
      }
  
  /* Compute transform column by column */
  if(rows>1)
    for(i=0;i<cols;i++) 
      {
        LoadCol(bigBuff,i,rows,cols);
        FFT842(direction,rows,stageBuff);
        StoreCol(bigBuff,i,rows,cols);
      }

  freeBuffer();
  return(NO_ERROR);
}

/* 
 * Compute 2D DFT on double data:
 * forward if direction==FFT_FORWARD,
 * inverse if direction==FFT_INVERSE.
 */
int fft2d(DCOMPLEX *array, int rows, int cols, int direction)
{
  int i, maxsize, errflag;
  
  if(!power_of_2(rows) || !power_of_2(cols)) {
    handle_error("fft: input array must have dimensions a power of 2");
    return(ERROR);
  }
  
  /* Allocate 1D buffer */
  bigBuffd = array;
  maxsize = rows>cols ? rows : cols;
  errflag = allocateBuffer(maxsize);
  if(errflag != NO_ERROR) return(errflag);

  /* Compute transform row by row */
  if(cols>1)
    for(i=0;i<rows;i++) 
      {
      	LoadRow(bigBuffd,i,cols);
      	FFT842(direction,cols,stageBuff);
      	StoreRow(bigBuffd,i,cols);
      }
  
  /* Compute transform column by column */
  if(rows>1)
    for(i=0;i<cols;i++) 
      {
        LoadCol(bigBuffd,i,rows,cols);
        FFT842(direction,rows,stageBuff);
        StoreCol(bigBuffd,i,rows,cols);
      }

  freeBuffer();
  return(NO_ERROR);
}

/* Finally, the entry points we announce in kube_fft.h */

/* Perform forward 2D transform on a COMPLEX array. */
int forward_fft2f(COMPLEX *array, int rows, int cols)
{
	return(fft2f(array, rows, cols, FFT_FORWARD));
}

/* Perform inverse 2D transform on a COMPLEX array. */
int inverse_fft2f(COMPLEX *array, int rows, int cols)
{
	return(fft2f(array, rows, cols, FFT_INVERSE));
}

/* Perform forward 2D transform on a DCOMPLEX array. */
int forward_fft2d(DCOMPLEX *array, int rows, int cols)
{
	return(fft2d(array, rows, cols, FFT_FORWARD));
}

/* Perform inverse 2D transform on a DCOMPLEX array. */
int inverse_fft2d(DCOMPLEX *array, int rows, int cols)
{
	return(fft2d(array, rows, cols, FFT_INVERSE));
}
