#include <inttypes.h>
#include <stdlib.h>
#include <fftw.h>

#include "ft.h"

#define FT_FFTW_FLAGS (FFTW_MEASURE | FFTW_IN_PLACE)

#define DOUBLEBUF   0

static fftw_plan   ft_2dplan_fwd[3];
static fftw_plan   ft_2dplan_bwd[3];
static fftw_plan   ft_1dplan_fwd[3];
static fftw_plan   ft_1dplan_bwd[3];
static int		ft_dims[9];
static int		ft_threads;

static ComplexPtr_t doublebuf;
static void	    *dbufPtr;


const char *FFTName = "FFTW2";

void 
FFTInit(int threads, int *dims, ComplexPtr_t localplanes2d)
{
    int nx = dims[0];
    int ny = dims[1];
    int nz = dims[2];


    ft_2dplan_fwd[PLANES_ORIENTED_X_Y_Z] = 
      fftw_create_plan(nx, FFTW_FORWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    ft_2dplan_bwd[PLANES_ORIENTED_X_Y_Z] = 
      fftw_create_plan(nx, FFTW_BACKWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    
    ft_1dplan_fwd[PLANES_ORIENTED_X_Y_Z] = fftw_create_plan(nz, FFTW_FORWARD, 
							    FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);
    ft_1dplan_bwd[PLANES_ORIENTED_X_Y_Z] = fftw_create_plan(nz, FFTW_BACKWARD, 
							    FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);

    ft_2dplan_fwd[PLANES_ORIENTED_Y_Z_X] = 
      fftw_create_plan(ny, FFTW_FORWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    ft_2dplan_bwd[PLANES_ORIENTED_Y_Z_X] = 
      fftw_create_plan(ny, FFTW_BACKWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    
    ft_1dplan_fwd[PLANES_ORIENTED_Y_Z_X] = 
      fftw_create_plan(nx, FFTW_FORWARD, FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);
    ft_1dplan_bwd[PLANES_ORIENTED_Y_Z_X] = 
      fftw_create_plan(nx, FFTW_BACKWARD, FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);


    ft_2dplan_fwd[PLANES_ORIENTED_Z_X_Y] = 
      fftw_create_plan(nz, FFTW_FORWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    ft_2dplan_bwd[PLANES_ORIENTED_Z_X_Y] = 
      fftw_create_plan(nz, FFTW_BACKWARD, FT_FFTW_FLAGS|FFTW_OUT_OF_PLACE);
    
    ft_1dplan_fwd[PLANES_ORIENTED_Z_X_Y] = 
      fftw_create_plan(ny, FFTW_FORWARD, FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);
    ft_1dplan_bwd[PLANES_ORIENTED_Z_X_Y] = 
      fftw_create_plan(ny, FFTW_BACKWARD, FFTW_OUT_OF_PLACE|FFTW_ESTIMATE);

    ft_threads = threads;
    memcpy(&ft_dims, dims, 9*sizeof(int));
    #if DOUBLEBUF
    printf("WARNING: Double-buffer is enabled. Serious performance impact!\n");
    doublebuf = (ComplexPtr_t) malloc_align(&dbufPtr, SIZEOF_COMPLEX*nx*ny*(nz/ft_threads));
    if (doublebuf == NULL) {
	fprintf(stderr, "malloc failed\n");
	exit(-1);
    }
    #endif
    return;
}

void 
FFTFinalize()
{
    int i;
    for(i=0; i<3; i++){ 
      fftw_destroy_plan(ft_2dplan_fwd[i]); 
      fftw_destroy_plan(ft_2dplan_bwd[i]); 
      fftw_destroy_plan(ft_1dplan_fwd[i]);
      fftw_destroy_plan(ft_1dplan_bwd[i]); 
    } 
    #if DOUBLEBUF
    free(doublebuf);
    #endif
}

#if 0
void
FFT2DLocal(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan	planx; 
  fftw_plan	plany;
    

  if (dir == FFT_FWD) {
    
    plany = ft_2dplan_fwd[orientation];
    planx = ft_2dplan_fwd[(orientation+1)%3];
  }
  else {
    plany = ft_2dplan_bwd[orientation];
    planx = ft_2dplan_bwd[(orientation+1)%3];
    
  }
  fftw(plany, dims[1], (fftw_complex*)inout, dims[1], 1, NULL, 0, 0);
  fftw(planx, dims[0], (fftw_complex*)inout, 1, dims[1], NULL, 0, 0);

  return;
}
#else

void
FFT2DLocal(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  int i;
  FFT2DLocalCols(inout, dir, orientation, dims);
  FFT2DLocalRows(dims[0], inout, dir, orientation, dims);
  return;
}
#endif

void
FFT2DLocalCols(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan	plany; 

  if (dir == FFT_FWD)
    plany = ft_2dplan_fwd[orientation];
  else 
    plany = ft_2dplan_bwd[orientation];
  
  fftw(plany, dims[1], (fftw_complex*)inout, dims[1], 1, NULL, 0, 0);
}

void
FFT2DLocalColsOut(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation, int *dims)
{
  fftw_plan	plany; 

  if (dir == FFT_FWD)
    plany = ft_2dplan_fwd[orientation];
  else 
    plany = ft_2dplan_bwd[orientation];
  
  fftw(plany, dims[1], (fftw_complex*)in, dims[1], 1, (fftw_complex*)out, dims[1], 1);
}


void
FFT2DLocalRow(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan	planx;
    
  if (dir == FFT_FWD) 
    planx = ft_2dplan_fwd[(orientation+1)%3];
  else 
    planx = ft_2dplan_bwd[(orientation+1)%3];

  fftw_one(planx, (fftw_complex*)inout, NULL);

  return;
}

void
FFT2DLocalRowOut(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation, int *dims)
{
  fftw_plan	planx;
    
  if (dir == FFT_FWD) 
    planx = ft_2dplan_fwd[(orientation+1)%3];
  else 
    planx = ft_2dplan_bwd[(orientation+1)%3];

  fftw_one(planx, (fftw_complex*)in, (fftw_complex*)out);

  return;
}


void
FFT2DLocalRows(int nrows, ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan	planx;
    
  if (dir == FFT_FWD) 
    planx = ft_2dplan_fwd[(orientation+1)%3];
  else 
    planx = ft_2dplan_bwd[(orientation+1)%3];

  fftw(planx, nrows, (fftw_complex*)inout, 1, dims[1], NULL, 0, 0);

  return;
}

void
FFT1DLocalTranspose(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation)
{
  int dims0, dims1, dims2;
  int CHUNK_SZ;
  int i;
  ComplexPtr_t copyin;
  fftw_plan plan;

  if (dir == FFT_FWD)
    plan = ft_1dplan_fwd[orientation];
  else
    plan = ft_1dplan_bwd[orientation];
  
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];
  CHUNK_SZ = dims0*dims1/ft_threads;

  timer_update(T_FFT1DPOST, FT_TIME_BEGIN);

  /* NX*NY pencils of length NZ are split across threads */
  fftw(plan, (dims0 / ft_threads)*dims1, 
	  (fftw_complex*)in, dims0*dims1/ft_threads, 1, 
	  (fftw_complex*)out, 1, dims2);

    /*
    printf("1D local: in=[%p, %p], out=[%p, %p]\n",
		    in, in + dims0*dims1*(dims2/ft_threads),
		    out, out + dims0*dims1*(dims2/ft_threads));
		    */
    
  timer_update(T_FFT1DPOST, FT_TIME_END);
}

void
FFT1DLocal(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation)
{
  int dims0, dims1, dims2;
  int CHUNK_SZ;
  int i;
  ComplexPtr_t copyin;
  fftw_plan plan;

  if (dir == FFT_FWD)
    plan = ft_1dplan_fwd[orientation];
  else
    plan = ft_1dplan_bwd[orientation];
  
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];
  CHUNK_SZ = dims0*dims1/ft_threads;

  timer_update(T_FFT1DPOST, FT_TIME_BEGIN);
  for (i = 0; i < dims0/ft_threads; i++) {
	fftw(plan, dims1,
	  (fftw_complex*)in + i * dims1*dims2, dims1, 1, 
	  (fftw_complex*)out + i * dims1*dims2, 1, dims2);
  }
  timer_update(T_FFT1DPOST, FT_TIME_END);
}
