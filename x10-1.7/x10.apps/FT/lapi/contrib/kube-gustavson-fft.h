
/**************************************************************
 * kube_fft.h
 *
 * Forward and inverse discrete 2D Fourier transforms.
 *
 * This software is in the public domain.
 *
 **************************************************************/

typedef struct {float re; float im;} COMPLEX;
typedef struct {double re; double im;} DCOMPLEX;

#ifndef ERROR
#define ERROR -1
#define NO_ERROR 0
#endif

#define FFT_FORWARD	0
#define FFT_INVERSE 1

#define PI      3.1415926535897932
#define TWOPI   6.2831853071795865 /* 2.0 * PI */
#define HALFPI  1.5707963267948966 /* PI / 2.0 */
#define PI8 	0.392699081698724 /* PI / 8.0 */
#define RT2 	1.4142135623731  /* sqrt(2.0) */
#define IRT2 	0.707106781186548  /* 1.0/sqrt(2.0) */

/* Perform forward 2D transform on a COMPLEX array. */
extern int forward_fft2f(COMPLEX *array, int rows, int cols);

/* Perform inverse 2D transform on a COMPLEX array. */
extern int inverse_fft2f(COMPLEX *array, int rows, int cols);

/* Perform forward 2D transform on a DCOMPLEX array. */
extern int forward_fft2d(DCOMPLEX *array, int rows, int cols);

/* Perform inverse 2D transform on a DCOMPLEX array. */
extern int inverse_fft2d(DCOMPLEX *array, int rows, int cols);
