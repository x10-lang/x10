#include <iostream>
#include <math.h>
#include <x10/x10lib.h>
#include <x10lang.h>

using namespace std;
using namespace x10lib;
using namespace x10::lang;
using namespace java::lang;

typedef long x10_long_t;
typedef int x10_int_t;

/**
 * X10LIB version of FT
 * Uses the same distribution as Ft.x10
 * Please see README in x10/ for more details
 */


//extern "C" void*
//arrayCopySwitch (x10_async_handler_t h, void* arg);


struct __async__0__args;
void __async__0 (__async__0__args args);

class Ft {  
private: const static x10_int_t OFFSET = 3;
private: const static x10_int_t CPAD_COLS = 0;
private: const static x10_int_t PLANES_ORIENTED_X_Y_Z = 0;
private: const static x10_int_t PLANES_ORIENTED_Y_Z_X = 1;
private: const static x10_int_t PLANES_ORIENTED_Z_X_Y = 2;
private: const static x10_int_t FT_COMM_SLABS = 0;
private: const static x10_int_t FT_COMM_PENCILS = 1;
private: const static x10_int_t FFT_FWD = 1;
private: const static x10_int_t FFT_BWD = 0;
 
public: const x10_int_t FT_COMM;
   
public: class DoubleArray  {     

public:
  
  Array<double, 1>* m_array;
  RectangularRegion <1>* m_domain;
  x10_int_t m_length;
  x10_int_t m_offset;
  x10_int_t m_start;
  x10_int_t m_end;
  DoubleArray (x10_int_t size, x10_int_t offset, x10_int_t proc);
  char* __serialize (char* buf) const;
  DoubleArray (char* buf, int& offset);
  size_t size() const;
};
  
  // Distributed arrays of complex numbers, one array per place
public : class DistDoubleArray 
{    
public:
  static const Dist<1>* UNIQUE;  
  static const x10_int_t N_PLACES;    
  DoubleArray** m_array;  
  x10_long_t m_size;  
  x10_long_t m_localSize;  
  DoubleArray* getArray (x10_long_t idx) const;  
  DistDoubleArray (const x10_long_t size, const x10_long_t offset) ;
  char* __serialize (char* buf) const;  
  DistDoubleArray (char* buf, x10_int_t& offset);
  size_t size ()  const;
};
  
  friend void __async__0 (__async__0__args args);
  
private : static x10_int_t NUMPLACES;
private : const static Dist<1>* UNIQUE;
  
private : const static x10_int_t SS=1;
private : const static x10_int_t WW=2;
private : const static x10_int_t AA=3;
private : const static x10_int_t BB=4;
private : const static x10_int_t CC=5;
private : const static x10_int_t DD=6;
 
private : String class_id_str;
private : char class_id_char;
private : x10_long_t CLASS, NX, NY, NZ, MAXDIM, MAX_ITER, TOTALSIZE, MAX_PADDED_SIZE;
private : x10_int_t dims[9];
private : double* checksum_real;
private : double* checksum_imag;
 
public : class Init;
public: static double mysecond();
public : x10_int_t switch_view (x10_int_t orientation, x10_int_t PID);
public : x10_int_t set_view (x10_int_t orientation, x10_int_t PID); 
public :  Ft( x10_int_t type, x10_int_t comm);
public : static  void initialize (Array<double, 1>* Array, x10_int_t size, x10_int_t offset, x10_int_t PID); 
public: static void initializeC (x10_int_t numPLACE, x10_int_t nx, x10_int_t ny, x10_int_t nz, x10_int_t offset, x10_int_t cpad_cols);
public: static  void computeInitialConditions (Array<double, 1>* Array, x10_int_t PID);
public: static  void init_exp (Array<double, 1>* Array, double alpha, x10_int_t PID);
public: static  void set_orientation (x10_int_t orientation);
public: static  void parabolic2 (Array<double, 1>* out, Array<double, 1>* in, Array<double, 1>* ex, x10_int_t t, double alpha);
public: static  void FFTInit (x10_int_t comm, Array<double, 1>* local2d, Array<double, 1>* local1d, x10_int_t PID);
public: static  void FFTWTest();
public: static  void FT_1DFFT (x10_int_t ft_comm, Array<double, 1>* in, Array<double, 1>* out, x10_int_t offset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);
public: static void FFT2DLocalCols (Array<double, 1>* local2d, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);
public: static void FFT2DLocalRow (Array<double, 1>* local2d, x10_int_t ComplexOffset, x10_int_t dir, x10_int_t orientation, x10_int_t PID);
public: static x10_int_t origindexmap (x10_int_t x, x10_int_t y, x10_int_t z);
public: static x10_int_t getowner (x10_int_t x, x10_int_t y, x10_int_t z);
public :  void solve();
public : void FFT2DComm_Pencil (const DoubleArray* local2d, const DistDoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* c);
public : void FFT2DComm_Slab (const DoubleArray* local2d, const DistDoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* c);
public :  void FFT2DComm(const DoubleArray* local2d, const DistDoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* clk);
private : void checksum(const DoubleArray* C, const x10_int_t PID, const x10_int_t itr) ;
public :  void print_Array(const DistDoubleArray* DDA);
public : static void  main (x10::array<x10::ref<x10::lang::String> >& args);
private :  void checksum_verify(const x10_int_t d1, const x10_int_t d2, const x10_int_t d3, const x10_int_t nt,
				const double* real_sums, 
				const double* imag_sums);
};


// Local Variables:
// mode: C++
// End:
