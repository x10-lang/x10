#include "FtStatic.h"

extern  void Ft_initialize (double* Array, x10_int_t* desc, x10_int_t size, x10_int_t offset, x10_int_t PID);


extern void Ft_initializeC (x10_int_t numPLACE, x10_int_t nx, x10_int_t ny, x10_int_t nz, x10_int_t offset, x10_int_t cpad_cols);


extern  void Ft_computeInitialConditions (double* Array, x10_int_t* desc,x10_int_t PID);


extern  void Ft_init_exp (double* Array, x10_int_t* desc,double alpha, x10_int_t PID);


extern  void Ft_set_orientation (x10_int_t orientation);


extern  void Ft_parabolic2 (double* out,  x10_int_t* desc1, double* in,  x10_int_t* desc2,double* ex,  x10_int_t* desc3, x10_int_t t, double alpha);


extern  void Ft_FFTInit (x10_int_t comm, double* local2d, x10_int_t* desc, double* local1d, x10_int_t* desc2, x10_int_t PID);


extern  void Ft_FFTWTest();


extern  void Ft_FT_1DFFT (x10_int_t ft_comm, double* in, x10_int_t* desc, double* out, x10_int_t* desc2, x10_int_t offset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);

extern void Ft_FFT2DLocalCols (double* local2d, x10_int_t* desc, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);

extern void Ft_FFT2DLocalRow (double* local2d, x10_int_t* desc, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);

extern x10_int_t Ft_origindexmap (x10_int_t x, x10_int_t y, x10_int_t z);

extern x10_int_t Ft_getowner (x10_int_t x, x10_int_t y, x10_int_t z);

void FtStatic::initialize (Array<double, 1>* Array, x10_int_t size, x10_int_t offset, x10_int_t PID)
{
  Ft_initialize (BASE_PTR(Array), NULL, size, offset, PID);
}

void FtStatic::initializeC (x10_int_t numPLACE, x10_int_t nx, x10_int_t ny, x10_int_t nz, x10_int_t offset, x10_int_t cpad_cols)
{
  Ft_initializeC (numPLACE, nx, ny, nz, offset, cpad_cols);
}
void  FtStatic::computeInitialConditions (Array<double, 1>* Array, x10_int_t PID)
{
  Ft_computeInitialConditions (BASE_PTR(Array), NULL, PID);
}
void  FtStatic::init_exp (Array<double, 1>* Array, double alpha, x10_int_t PID)
{
  Ft_init_exp (BASE_PTR(Array), NULL, alpha, PID);
}

void  FtStatic::set_orientation (x10_int_t orientation)
{
  Ft_set_orientation (orientation);
}
void  FtStatic::parabolic2 (Array<double, 1>* out, Array<double, 1>* in, Array<double, 1>* ex, x10_int_t t, double alpha)
{
  Ft_parabolic2 (BASE_PTR(out), NULL, BASE_PTR(in), NULL, BASE_PTR(ex), NULL, t, alpha);
}
void  FtStatic::FFTInit (x10_int_t comm, Array<double, 1>* local2d, Array<double, 1>* local1d, x10_int_t PID)
{
  Ft_FFTInit (comm, BASE_PTR(local2d), NULL, BASE_PTR(local1d), NULL, PID);
}
void  FtStatic::FFTWTest()
{
  Ft_FFTWTest();
}
void  FtStatic::FT_1DFFT (x10_int_t ft_comm, Array<double, 1>* in, Array<double, 1>* out, x10_int_t offset, x10_int_t dir, x10_int_t orientation, x10_int_t PID)
{
  Ft_FT_1DFFT (ft_comm, BASE_PTR(in), NULL, BASE_PTR(out), NULL, offset, dir, orientation, PID);
}
void  FtStatic::FFT2DLocalCols (Array<double, 1>* local2d, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID)
{
  Ft_FFT2DLocalCols (BASE_PTR(local2d), NULL, ComplexOffset, dir, orientation, PID);
}
void  FtStatic::FFT2DLocalRow (Array<double, 1>* local2d, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID)
{
  Ft_FFT2DLocalRow (BASE_PTR(local2d), NULL, ComplexOffset, dir, orientation, PID);
}
x10_int_t  FtStatic::origindexmap (x10_int_t x, x10_int_t y, x10_int_t z)
{
  Ft_origindexmap (x, y, z);
}

x10_int_t  FtStatic::getowner (x10_int_t x, x10_int_t y, x10_int_t z)
{
  Ft_getowner (x, y, z);
}
