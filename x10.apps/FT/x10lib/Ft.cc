#include <iostream>
#include "timers.h"
#include <math.h>
#include <x10/x10lib.h>
#include <x10lang.h>

using namespace std;
using namespace x10lib;
using namespace x10::lang;
using namespace java::lang;

/**
 * X10LIB version of FT
 * Uses the same distribution as Ft.x10
 * Please see README in x10/ for more details
 */

extern  void Ft_initialize (double* Array, int* desc, int size, int offset, int PID);
extern void Ft_initializeC (int numPLACE, int nx, int ny, int nz, int offset, int cpad_cols);
extern  void Ft_computeInitialConditions (double* Array, int* desc,int PID);
extern  void Ft_init_exp (double* Array, int* desc,double alpha, int PID);
extern  void Ft_set_orientation (int orientation);
extern  void Ft_parabolic2 (double* out,  int* desc1, double* in,  int* desc2,double* ex,  int* desc3, int t, double alpha);
extern  void Ft_FFTInit (int comm, double* local2d, int* desc, double* local1d, int* desc2, int PID);
extern  void Ft_FFTWTest();
extern  void Ft_FT_1DFFT (int ft_comm, double* in, int* desc, double* out, int* desc2, int offset, int dir, int orientation, int PID);
extern void Ft_FFT2DLocalCols (double* local2d, int* desc, int ComplexOffset, int dir, int orientation, int PID);
extern void Ft_FFT2DLocalRow (double* local2d, int* desc, int ComplexOffset, int dir, int orientation, int PID);
extern int Ft_origindexmap (int x, int y, int z);
extern int Ft_getowner (int x, int y, int z);


class Ft;
struct 
{
  Ft* FtSolver;  
}GLOBAL_SPACE;

struct __async__0__args
{
  __async__0__args (int _captVar1, double _captVar2, double _captVar3) : 
    captVar1 (_captVar1),
    captVar2 (_captVar2),
    captVar3 (_captVar3){}

  int captVar1;
  double captVar2;
  double captVar3;
};
void __async__0 (__async__0__args args);




place PLACE (x10lib::here());

static int CS = 0;


class Ft {  
private: const static int OFFSET = 3;
private: const static int CPAD_COLS = 0;
private: const static int PLANES_ORIENTED_X_Y_Z = 0;
private: const static int PLANES_ORIENTED_Y_Z_X = 1;
private: const static int PLANES_ORIENTED_Z_X_Y = 2;
private: const static int FT_COMM_SLABS = 0;
private: const static int FT_COMM_PENCILS = 1;
private: const static int FFT_FWD = 1;
private: const static int FFT_BWD = 0;
 
public: const int FT_COMM;
 
public: class DoubleArray  {     
public:
  
  Array<double, 1>* m_array;
  RectangularRegion <1>* m_domain;
  int m_length;
  int m_offset;
  int m_start;
  int m_end;
   
  DoubleArray  () :
    m_array (NULL),
    m_domain (NULL),
    m_length (0),
    m_offset (0),
    m_start (0),
    m_end (0)
  {

  }

  DoubleArray (const DoubleArray& other) :
    m_array (other.m_array),
    m_domain (other.m_domain),
    m_length (other.m_length),
    m_offset (other.m_offset),
    m_start (other.m_start),
    m_end (other.m_end)
  {

  }

  DoubleArray& operator= (const DoubleArray& other)
  {
    m_array = other.m_array;
    m_domain = other.m_domain;
    m_length = other.m_length;
    m_offset = other.m_offset;
    m_start = other.m_start;
    m_end = other.m_end;

    return *this;
  }

  DoubleArray (int size, int offset) 
  {
    m_length = size;
    m_offset = offset;
    m_domain = new RectangularRegion<1> (0-offset, size-1);
    m_start = 0;
    m_end = size-1;

    m_array = makeArrayLocal <double, 1, RectangularRegion, ConstDist> (m_domain, NULL);
  }

  DoubleArray (int start, int end, int offset) 
  {
    m_length = end-start+1;
    m_offset = offset;
    m_domain = new RectangularRegion<1> (start-offset, end);
    m_start = 0;
    m_end = end;


    m_array = makeArrayLocal <double, 1, RectangularRegion, ConstDist> (m_domain, NULL);
  }     
};

public : class DistDoubleArray 
  {    
  public:

    static const Dist<1>* ALLPLACES;

    static const int N_PLACES;

    Array<DoubleArray, 1>* m_array;
    int m_size;
    int m_localSize;
    
    DoubleArray getArray (int idx) const
      {        
	return m_array->getLocalElementAt (idx);
      }
    
    DistDoubleArray (const int size, const int offset) 
    {	
      m_size = size;
      m_localSize = size / N_PLACES;	
      
      m_array = makeArrayLocal <DoubleArray, 1, RectangularRegion, UniqueDist> (ALLPLACES->region(), ALLPLACES);
      
      CS = finishStart (CS);
      int a = PLACE.HERE * m_localSize;
      int b =  (PLACE.HERE+1) * m_localSize - 1;     
            
      m_array->getLocalElementAt (PLACE.HERE) = *(new DoubleArray (m_localSize, offset));      
            
      finishEnd (NULL);

      
      //This will go away.
      CS = finishStart(CS);
      //caching
      for (int i  = 0; i < N_PLACES; i++) {
	if ( i == PLACE.HERE) continue;
	m_array->getLocalElementAt (i) = m_array->getRemoteElementAt (Point<1>(i));
      }             
      finishEnd (NULL);


    }
};
  
friend void __async__0 (__async__0__args args);

private : static int NUMPLACES;
private : const static Dist<1>* ALLPLACES;
  
private : const static int SS=1;
private : const static int WW=2;
private : const static int AA=3;
private : const static int BB=4;
private : const static int CC=5;
private : const static int DD=6;

private : String class_id_str;
private : char class_id_char;
private : int CLASS, NX, NY, NZ, MAXDIM, MAX_ITER, TOTALSIZE, MAX_PADDED_SIZE;
private : int dims[9];
private : double* checksum_real;
private : double* checksum_imag;

public : class Init 
{
  Init () {
    cout << "LOAD FT NOT IMPLEMENTED " << endl;
  }
};
  
public: static double mysecond() 
  {
    return (double) ((double) (nanoTime() / 1000) * 1.e-6);
  }
 

public : int switch_view (int orientation, int PID) {
  int next_orientation = (orientation + 1) % 3;
  //if (PID == 0)
    set_orientation (next_orientation);
  return next_orientation;
}

public : int set_view (int orientation, int PID) {
  //if (PID == 0) 
  set_orientation (orientation);
  return orientation;
}
 
public :  Ft( int type, int comm) : 
  CLASS (type),
  FT_COMM(comm)
  {
    switch ( CLASS ){
  case SS:
    NX = 64; NY = 64; NZ = 64; MAXDIM = 64; MAX_ITER = 6;
    class_id_str = "NPB CLASS S 64x64x64 6 iterations";
    class_id_char = 'S';
    break;
  case WW:
    NX = 128; NY = 128; NZ = 32; MAXDIM = 128; MAX_ITER = 6;
    class_id_str = "NPB CLASS W 128x128x32 6 iterations";
    class_id_char = 'W';
    break;
  case AA:
    NX = 256; NY = 256; NZ = 128; MAXDIM = 256; MAX_ITER = 6;
    class_id_str = "NPB CLASS A 256x256x128 6 iterations";
    class_id_char = 'A';
    break;
  case BB:
    NX = 512; NY = 256; NZ = 256; MAXDIM = 512; MAX_ITER = 20;
    class_id_str = "NPB CLASS B 512x256x256 20 iterations";
    class_id_char = 'B';
    break;
  case CC:
    NX = 512; NY = 512; NZ = 512; MAXDIM = 512; MAX_ITER = 20;
    class_id_str = "NPB CLASS C 512x512x512 20 iterations";
    class_id_char = 'C';
    break;
  case DD:
    NX = 2048; NY = 1024; NZ = 1024; MAXDIM = 2048; MAX_ITER = 25;
    class_id_str = "NPB CLASS D 2048x1024x1024 25 iterations";
    class_id_char = 'D';
    break;
  default:
    NX = 2; NY = 4; NZ = 4; MAXDIM = 4; MAX_ITER = 1;
    class_id_str = "Test mode: 2x4x4 1 iterations";
    class_id_char = 'T';
  }
   
   
  TOTALSIZE = NX*NY*NZ; 
  MAX_PADDED_SIZE = max(2*NX*(NY+CPAD_COLS)*NZ, max(2*NY*(NZ+CPAD_COLS)*NX, 2*NZ*(NX+CPAD_COLS)*NY));
   
  dims[0] = NX; dims[1] = NY+CPAD_COLS; dims[2] = NZ;
  dims[3] = NY; dims[4] = NZ+CPAD_COLS; dims[5] = NX;
  dims[6] = NZ; dims[7] = NX+CPAD_COLS; dims[8] = NY;
  checksum_real =  new double [MAX_ITER];// (point p) {return 0;};
  checksum_imag =  new double [MAX_ITER];// (point p) {return 0;};
   
  //FFTWTest(); //the name of dll has to begin with lower case!!!
  }
 
  
public : static  void initialize (double* Array, int size, int offset, int PID)
  {
    Ft_initialize (Array, NULL, size, offset, PID);
  }
  
public: static void initializeC (int numPLACE, int nx, int ny, int nz, int offset, int cpad_cols)
  {
    Ft_initializeC (numPLACE, nx, ny, nz, offset, cpad_cols);
  }
public: static  void computeInitialConditions (double* Array, int PID)
  {
    Ft_computeInitialConditions (Array, NULL, PID);
  }
public: static  void init_exp (double* Array, double alpha, int PID)
  {
    Ft_init_exp (Array, NULL, alpha, PID);
  }

  public: static  void set_orientation (int orientation)
  {
    Ft_set_orientation (orientation);
  }
public: static  void parabolic2 (double* out, double* in, double* ex, int t, double alpha)
  {
    Ft_parabolic2 (out, NULL, in, NULL, ex, NULL, t, alpha);
  }
public: static  void FFTInit (int comm, double* local2d, double* local1d, int PID)
  {
    Ft_FFTInit (comm, local2d, NULL, local1d, NULL, PID);
  }
public: static  void FFTWTest()
  {
    Ft_FFTWTest();
  }
public: static  void FT_1DFFT (int ft_comm, double* in, double* out, int offset, int dir, int orientation, int PID)
  {
    Ft_FT_1DFFT (ft_comm, in, NULL, out, NULL, offset, dir, orientation, PID);
  }
public: static void FFT2DLocalCols (double* local2d, int ComplexOffset, int dir, int orientation, int PID)
  {
    Ft_FFT2DLocalCols (local2d, NULL, ComplexOffset, dir, orientation, PID);
  }
public: static void FFT2DLocalRow (double* local2d, int ComplexOffset, int dir, int orientation, int PID)
  {
    Ft_FFT2DLocalRow (local2d, NULL, ComplexOffset, dir, orientation, PID);
  }
public: static int origindexmap (int x, int y, int z)
  {
    Ft_origindexmap (x, y, z);
  }
public: static int getowner (int x, int y, int z)
  {
    Ft_getowner (x, y, z);
  }
  
public :  void solve(){
   
  const DistDoubleArray Planes2d (MAX_PADDED_SIZE, OFFSET);
  const DistDoubleArray Planes1d (MAX_PADDED_SIZE, 0);
  const DistDoubleArray V (MAX_PADDED_SIZE, 0);
  const DistDoubleArray ex (TOTALSIZE, 0);
   
   
  /* passing constants to C, which are stored as external variables */		  
  initializeC (NUMPLACES, NX, NY, NZ, OFFSET, CPAD_COLS);
   
  double cputime1 = -mysecond();		
   
  x10_place_t PID = PLACE.HERE;

  CS = finishStart (CS);

  Clock* clk = new Clock;
   
  double cputime2; 
  int current_orientation = set_view (PLANES_ORIENTED_X_Y_Z, PID);
   
  //clockNext (clk, 1);
     
  const DoubleArray local_ex =   ex.getArray(PID);
  
  const DoubleArray localPlanes2d = Planes2d.getArray(PID);
  
  const DoubleArray localPlanes1d = Planes1d.getArray(PID);

  const DoubleArray local_V = V.getArray(PID);
   
  FFTInit(FT_COMM, localPlanes2d.m_array->raw(), localPlanes1d.m_array->raw(), PID);


  init_exp(local_ex.m_array->raw(), 1.0e-6, PID);

  /*
   * Run the entire problem once to make sure all the data is touched. This
   * reduces variable startup costs, which is important for short benchmarks
   */
  
  computeInitialConditions(localPlanes2d.m_array->raw(), PID);

  clockNext (clk, 1);

  FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID);

  clockNext (clk, 1);

  FT_1DFFT(FT_COMM, localPlanes1d.m_array->raw(), localPlanes2d.m_array->raw(), 1, FFT_FWD, current_orientation, PID);

  clockNext (clk, 1);
   
  if (PID == 0) cout << "Start timing of IBM X10 NAS FT: class " << class_id_char << " (" << class_id_str << ")" << endl;
  cputime2 = -mysecond();

  current_orientation = set_view(PLANES_ORIENTED_X_Y_Z,PID);

  clockNext (clk, 1);

  computeInitialConditions(localPlanes2d.m_array->raw(), PID);

  init_exp(local_ex.m_array->raw(), 1.0e-6, PID);

  FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID);
  
  clockNext (clk, 1);

  FT_1DFFT (FT_COMM, localPlanes1d.m_array->raw(), local_V.m_array->raw(), 0, FFT_FWD, current_orientation, PID);

  //clockNext (clk, 1);
   
  current_orientation = switch_view(current_orientation, PID);

  clockNext (clk, 1);

  int saved_orientation = current_orientation;
   
  for (int iter = 1; iter <= MAX_ITER; iter ++){

    current_orientation = set_view(saved_orientation, PID);

    //clockNext (clk, 1);

    parabolic2(localPlanes2d.m_array->raw(), local_V.m_array->raw(), local_ex.m_array->raw(), iter, 1.0e-6);

    FFT2DComm(localPlanes2d, Planes1d, FFT_BWD, current_orientation, PID);

    clockNext (clk, 1);

    FT_1DFFT(FT_COMM, localPlanes1d.m_array->raw(), localPlanes2d.m_array->raw(), 1, FFT_BWD, current_orientation, PID);

    current_orientation = switch_view(current_orientation, PID);

    checksum_real[iter-1] = 0.0;
    checksum_imag[iter-1] = 0.0;

    clockNext (clk, 1);

    checksum(localPlanes2d, PID, iter);

    clockNext (clk, 1);

    if (PID == 0){ 
      cout << " Iter = " << iter << " checksum_real = " <<
	checksum_real[iter-1] << " checksum_imag = " << checksum_imag[iter-1] << endl;
    }
  }
   
  cputime2 += mysecond(); 
  if (PID == 0) cout << " The wall clock time for the timed section is " << cputime2 << " secs" << endl;
   
   
  finishEnd (NULL);
   
  cputime1 += mysecond();
  
  double mflops = (1.0e-6*((double)NX*NY*NZ) *
              (14.8157 + 7.19641*log((double)NX*NY*NZ)
               + (5.23518 + 7.21113*log((double)NX*NY*NZ))*MAX_ITER)) /  cputime2;

  if (PID == 0) cout << "The overall wall clock time is " << cputime1 << "secs " << "mflops : " << mflops << endl;
  
  if (PID==0 ) if (class_id_char != 'T') checksum_verify (NX, NY, NZ, MAX_ITER, checksum_real, checksum_imag);   


}
 
public : void FFT2DComm_Pencil (const DoubleArray local2d, const DistDoubleArray dist1d, const int dir, const int orientation, const int placeID){
   
  int dim0, dim1, dim2;
  int plane_size, CHUNK_SZ, numrows;
     
  dim0 = dims[orientation*3];
  dim1 = dims[orientation*3+1];
  dim2 = dims[orientation*3+2];
       
  plane_size = dim0*dim1;

  CHUNK_SZ = (dim0/NUMPLACES)*dim1;

  numrows = dim0/NUMPLACES;
  int p, t, i, offset1, offset2;
     
  double* local2darray = local2d.m_array->raw();
     
  //  x10_switch_t swch = AllocSwitch();

  for (p = 0; p < dim2/NUMPLACES; p++){
    offset1 = plane_size*p;
    
    FFT2DLocalCols (local2darray, offset1, dir, orientation, placeID);
    
    for (i = 0; i < numrows; i++) 
      
      for (t = 0; t <NUMPLACES; t++){
	
	int destID = t;
	
	//int destID = (placeID + t) % NUMPLACES;

	offset2 = offset1 + destID*CHUNK_SZ + i*dim1;
	
	FFT2DLocalRow(local2darray, offset2, dir, orientation, placeID);
	
	int srcStart = offset2*2;
	
	int destStart = 2*(placeID*dim2/NUMPLACES*dim1+p*dim1 + i*dim1*dim2);
	
	const x10_place_t destPlace =  ALLPLACES->place (destID);
	
	//double* local1darray = (double*) (((char*) (dist1d.getArray(destID).m_array)) + sizeof(Array<double, 1>)) ; 
	asyncArrayCopy (local2d.m_array, srcStart + OFFSET, dist1d.getArray(destID).m_array, destStart, destID, 2 * dim1, NULL);

	//memcpy (dist1d.getArray(0).m_array->raw() + destStart, local2d.m_array->raw() + srcStart + OFFSET, 2 * dim1 * sizeof(double));
      }
    
  }
  
  //swch->next ();
  
}

	
public :  void FFT2DComm(const DoubleArray local2d, const DistDoubleArray dist1d, const int dir, const int orientation, const int placeID)
  {
    if (FT_COMM == FT_COMM_SLABS)
      FFT2DComm_Pencil(local2d, dist1d, dir, orientation, placeID);
    else
      FFT2DComm_Pencil(local2d, dist1d, dir, orientation, placeID);
  }

private : void checksum(const DoubleArray C, const int PID, const int itr) {
  int j, q, r, s, idx;
  double sum_real = 0;
  double sum_imag = 0;
     
  int proc;
  const double* temp = C.m_array->raw() + OFFSET; 

  int total=0;
//  CS = finishStart (CS);
  for (j=1; j <= 1024; ++j){
    q = j % NX;
    r = (3*j) % NY;
    s = (5*j) % NZ;
       
    proc = getowner(q, r, s);
    if (proc == PID) {
      total++;
      idx = 2*origindexmap(q,r,s);//+C.m_start;
      //System.out.println(" [ "+proc+", "+idx+"])");
      sum_real+=temp[idx];
      sum_imag+=temp[idx+1];
    }
  }

 // finishEnd (NULL);

  const double res_real = ((sum_real/NX)/NY)/NZ;
  const double res_imag = ((sum_imag/NX)/NY)/NZ;

  

  //CS = finishStart(CS);
  __async__0__args args0(itr, res_real, res_imag);
  asyncSpawnInlineAgg (0, 0, &args0 , sizeof(__async__0__args));
  
  asyncFlush (0, sizeof(__async__0__args));
  
  //finishEnd (NULL);
} 
   
   
public :  void printArray(const DistDoubleArray DDA){

  x10_place_t PID = PLACE.HERE;   
  DoubleArray da = DDA.getArray(PID);
  cout << "At place " << PID << endl;
     
  for (int i = da.m_domain->origin().value(0); i < da.m_domain->diagonal().value(0); i++) {
    if (i%2 == 0) 
      cout << " [" << (i/2) << "]= (" <<  da.m_array->raw()[i] << endl;
    else
      cout << ", " <<  da.m_array->raw()[i] << ")" << endl;
  }	
}
   
   
public : static void  main (x10::array<x10::ref<x10::lang::String> >& args) {
  

  if (NUMPLACES>1 &&!(NUMPLACES/2 != (NUMPLACES/2*2))){
    cout << "The number of places must be even." << endl; 
    return;
  }
  
  /*
   * Define CLASS accordingly
   *
   * CLASS=1 => NPB CLASS S
   * CLASS=2 => NPB CLASS W
   * CLASS=3 => NPB CLASS A
   * CLASS=4 => NPB CLASS B
   * CLASS=5 => NPB CLASS C
   * CLASS=6 => NPB CLASS D
   * others  => Debug cube
   */
     
  int CLASS = 0; 
  int COMM = FT_COMM_SLABS;  
  for (int q = 0; q < args.length; ++q) {		
    if ((*args[q]).equals(String ("-s")) || (*args[q]).equals(String ("-S"))) {
      CLASS = 1;
    }
    if ((*args[q]).equals(String ("-w")) || (*args[q]).equals(String ("-W"))) {
      CLASS = 2;
    }
       
    if ((*args[q]).equals(String ("-a")) || (*args[q]).equals(String ("-A"))) {
      CLASS = 3;
    }
    if ((*args[q]).equals(String ("-b")) || (*args[q]).equals(String ("-B"))) {
      CLASS = 4;
    }
    if ((*args[q]).equals(String ("-c")) || (*args[q]).equals(String ("-C"))) {
      CLASS = 5;
    }
    if ((*args[q]).equals(String ("-d")) || (*args[q]).equals(String ("-D"))) {
      CLASS = 6;
    }
    if ((*args[q]).equals(String ("-p")) || (*args[q]).equals(String ("-P"))) {
      COMM = FT_COMM_PENCILS;
    }
  }
  
  GLOBAL_SPACE.FtSolver = new Ft(CLASS, COMM);
  GLOBAL_SPACE.FtSolver->solve();
}	
   
   
private :  void checksum_verify(const int d1, const int d2, const int d3, const int nt,
				const double* real_sums, 
				const double* imag_sums){
     
  int i;
  double err, epsilon;
  bool known_class = false;
  bool verified = false;
  /*--------------------------------------------------------------------
    c   Sample size reference checksums
    c-------------------------------------------------------------------*/
     
  /*--------------------------------------------------------------------
    c   Class S size reference checksums
    c-------------------------------------------------------------------*/
  double  vdata_real_s []=
    {
      5.546087004964e+02,
      5.546385409189e+02,
      5.546148406171e+02,
      5.545423607415e+02,
      5.544255039624e+02,
      5.542683411902e+02};
  double vdata_imag_s []=
    {
      4.845363331978e+02,
      4.865304269511e+02,
      4.883910722336e+02,
      4.901273169046e+02,
      4.917475857993e+02,
      4.932597244941e+02};
  /*--------------------------------------------------------------------
    c   Class W size reference checksums
    c-------------------------------------------------------------------*/
  double  vdata_real_w [] =
    {
      5.673612178944e+02,
      5.631436885271e+02,
      5.594024089970e+02,
      5.560698047020e+02,
      5.530898991250e+02,
      5.504159734538e+02};
  double vdata_imag_w [] =
    {
      5.293246849175e+02,
      5.282149986629e+02,
      5.270996558037e+02,
      5.260027904925e+02,
      5.249400845633e+02,
      5.239212247086e+02};
  /*--------------------------------------------------------------------
    c   Class A size reference checksums
    c-------------------------------------------------------------------*/
  double vdata_real_a [] =
    {
      5.046735008193e+02,
      5.059412319734e+02,
      5.069376896287e+02,
      5.077892868474e+02,
      5.085233095391e+02,
      5.091487099959e+02};
  double vdata_imag_a [] =
    {
      5.114047905510e+02,
      5.098809666433e+02,
      5.098144042213e+02,
      5.101336130759e+02,
      5.104914655194e+02,
      5.107917842803e+02};
  /*--------------------------------------------------------------------
    c   Class B size reference checksums
    c-------------------------------------------------------------------*/
  double vdata_real_b [] =
    {
      5.177643571579e+02,
      5.154521291263e+02,
      5.146409228649e+02,
      5.142378756213e+02,
      5.139626667737e+02,
      5.137423460082e+02,
      5.135547056878e+02,
      5.133910925466e+02,
      5.132470705390e+02,
      5.131197729984e+02,
      5.130070319283e+02,
      5.129070537032e+02,
      5.128182883502e+02,
      5.127393733383e+02,
      5.126691062020e+02,
      5.126064276004e+02,
      5.125504076570e+02,
      5.125002331720e+02,
      5.124551951846e+02,
      5.124146770029e+02};
  double vdata_imag_b [] =
    {
      5.077803458597e+02,
      5.088249431599e+02,
      5.096208912659e+02,
      5.101023387619e+02,
      5.103976610617e+02,
      5.105948019802e+02,
      5.107404165783e+02,
      5.108576573661e+02,
      5.109577278523e+02,
      5.110460304483e+02,
      5.111252433800e+02,
      5.111968077718e+02,
      5.112616233064e+02,
      5.113203605551e+02,
      5.113735928093e+02,
      5.114218460548e+02,
      5.114656139760e+02,
      5.115053595966e+02,
      5.115415130407e+02,
      5.115744692211e+02};
  /*--------------------------------------------------------------------
    c   Class C size reference checksums
    c-------------------------------------------------------------------*/
  double vdata_real_c [] =
    {
      5.195078707457e+02,
      5.155422171134e+02,
      5.144678022222e+02,
      5.140150594328e+02,
      5.137550426810e+02,
      5.135811056728e+02,
      5.134569343165e+02,
      5.133651975661e+02,
      5.132955192805e+02,
      5.132410471738e+02,
      5.131971141679e+02,
      5.131605205716e+02,
      5.131290734194e+02,
      5.131012720314e+02,
      5.130760908195e+02,
      5.130528295923e+02,
      5.130310107773e+02,
      5.130103090133e+02,
      5.129905029333e+02,
      5.129714421109e+02};
  double vdata_imag_c [] =
    {
      5.149019699238e+02,
      5.127578201997e+02,
      5.122251847514e+02,
      5.121090289018e+02,
      5.121143685824e+02,
      5.121496764568e+02,
      5.121870921893e+02,
      5.122193250322e+02,
      5.122454735794e+02,
      5.122663649603e+02,
      5.122830879827e+02,
      5.122965869718e+02,
      5.123075927445e+02,
      5.123166486553e+02,
      5.123241541685e+02,
      5.123304037599e+02,
      5.123356167976e+02,
      5.123399592211e+02,
      5.123435588985e+02,
      5.123465164008e+02};
  double vdata_real_d [] =
    {
      5.122230065252e+02,
      5.120463975765e+02,
      5.119865766760e+02,
      5.119518799488e+02,
      5.119269088223e+02,
      5.119082416858e+02,
      5.118943814638e+02,
      5.118842385057e+02,
      5.118769435632e+02,
      5.118718203448e+02,
      5.118683569061e+02,
      5.118661708593e+02,
      5.118649768950e+02,
      5.118645605626e+02,
      5.118647586618e+02,
      5.118654451572e+02,
      5.118665212451e+02,
      5.118679083821e+02,
      5.118695433664e+02,
      5.118713748264e+02,
      5.118733606701e+02,
      5.118754661974e+02,
      5.118776626738e+02,
      5.118799262314e+02,
      5.118822370068e+02};
  double vdata_imag_d [] =
    {
      5.118534037109e+02,
      5.117061181082e+02,
      5.117096364601e+02,
      5.117373863950e+02,
      5.117680347632e+02,
      5.117967875532e+02,
      5.118225281841e+02,
      5.118451629348e+02,
      5.118649119387e+02,
      5.118820803844e+02,
      5.118969781011e+02,
      5.119098918835e+02,
      5.119210777066e+02,
      5.119307604484e+02,
      5.119391362671e+02,
      5.119463757241e+02,
      5.119526269238e+02,
      5.119580184108e+02,
      5.119626617538e+02,
      5.119666538138e+02,
      5.119700787219e+02,
      5.119730095953e+02,
      5.119755100241e+02,
      5.119776353561e+02,
      5.119794338060e+02};
  
  epsilon = 1.0e-12;
  verified = true;

  /* CLASS S*/
  if (d1 == 64 &&
      d2 == 64 &&
      d3 == 64 &&
      nt == 6)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_s[i]) / vdata_real_s[i];
	  if (fabs (err) > epsilon)
	    {

	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_s[i]) / vdata_imag_s[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }
  /*CLASS W*/
  else if (d1 == 128 &&
	   d2 == 128 &&
	   d3 == 32 &&
	   nt == 6)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_w[i]) / vdata_real_w[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_w[i]) / vdata_imag_w[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }
  /*Class A*/
  else if (d1 == 256 &&
	   d2 == 256 &&
	   d3 == 128 &&
	   nt == 6)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_a[i]) / vdata_real_a[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_a[i]) / vdata_imag_a[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }
  /*CLASS B*/
  else if (d1 == 512 &&
	   d2 == 256 &&
	   d3 == 256 &&
	   nt == 20)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_b[i]) / vdata_real_b[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_b[i]) / vdata_imag_b[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }
  /*CLASS C*/
  else if (d1 == 512 &&
	   d2 == 512 &&
	   d3 == 512 &&
	   nt == 20)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_c[i]) / vdata_real_c[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_c[i]) / vdata_imag_c[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }
  /*CLASS D*/
  else if (d1 == 2048 &&
	   d2 == 1024 &&
	   d3 == 1024 &&
	   nt == 25)
    {
      known_class = true;
      for (i = 0; i < nt; i++)
	{
	  err = (real_sums[i] - vdata_real_d[i]) / vdata_real_d[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	  err = (imag_sums[i] - vdata_imag_d[i]) / vdata_imag_d[i];
	  if (fabs (err) > epsilon)
	    {
	      verified = false;
	      break;
	    }
	}
    }

  if (known_class && verified)
    {
      cout << " Result verification successful\n";
    }
  else
    {
      cout << " Result verification failed\n";
    }
  
}
   
};

int Ft::NUMPLACES=numPlaces();
const Dist<1>* Ft::ALLPLACES = Dist<1>::makeUnique();
const int Ft::DistDoubleArray::N_PLACES=numPlaces();
const Dist<1>* Ft::DistDoubleArray::ALLPLACES = Dist<1>::makeUnique();

extern "C" {
  int main (int ac, char* av[])
  {
    x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args (ac, av);
    
    Ft::main (*args);
    
    x10::free_args (args);
        
    return x10::exitCode;    
  }
}

void
__async__0 (__async__0__args args)
{
  int itr = args.captVar1;
  double res_real = args.captVar2;
  double res_imag = args.captVar3;
  GLOBAL_SPACE.FtSolver->checksum_real[itr-1] += res_real;
  GLOBAL_SPACE.FtSolver->checksum_imag[itr-1] += res_imag;
}


void
asyncSwitch (x10_async_handler_t h, void* arg, int niter)
{
  char* args = (char*) arg;
  switch (h) {
  case 0:
    for (int i = 0; i < niter; i++) {
      __async__0(*((__async__0__args*)args));
      args += sizeof(__async__0__args);
    }
    break;
  }
}
