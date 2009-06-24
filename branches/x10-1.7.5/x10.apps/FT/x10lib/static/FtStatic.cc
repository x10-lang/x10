#include "FtStatic.h"
#include "timers.h"
#include "x10/reduce.h"


void add (double& arg1, const double& arg2)
{
  arg1 += arg2;
}


struct 
{
  FtStatic::DoubleArray* Planes1d;
  FtStatic* FtSolver;  
}GLOBAL_STATE;

struct __array_copy_args_0 
{
  __array_copy_args_0 (x10_int_t destStart) :
     _destStart (destStart) {}
  
  __array_copy_args_0 () {}

  x10_int_t _destStart;
};

struct __closure_0 : public Closure
{
  __closure_0 (int destStart) :
        Closure (sizeof(__array_copy_args_0), 0),
        args (destStart) { };

  __closure_0 () {}

  __array_copy_args_0 args;
};
 
struct __array_copy_args_1
{
  __array_copy_args_1 (x10_int_t destStart) :
     _destStart (destStart) {}

  __array_copy_args_1 () {}

  x10_int_t _destStart;
};

struct __closure_1 : public Closure
{
  __closure_1 (int destStart) :
        Closure (sizeof(__array_copy_args_1), 1),
        args (destStart) { };

  __closure_1 () {}

  __array_copy_args_1 args;
};

static int closureCount = 0;
Closure* closure; 

struct __async__0__args
{
  __async__0__args (x10_int_t _captVar1, double _captVar2, double _captVar3) : 
    captVar1 (_captVar1),
    captVar2 (_captVar2),
    captVar3 (_captVar3){}

  x10_int_t captVar1;
  double captVar2;
  double captVar3;
};

static x10_int_t CS = 0;

x10::ref<place> PLACE = (x10::lang::place) x10lib::here();

FtStatic::DoubleArray::DoubleArray (x10_int_t size, x10_int_t offset) 
{
  m_length = size;
  m_offset = offset;
  m_domain = new RectangularRegion<1> (-offset, size-1);
  m_start = 0;
  m_end = size-1;
    
  m_array = makeArrayLocalHeap <double, 1, RectangularRegion, ConstDist> (m_domain, NULL);
}
  
FtStatic::DoubleArray::DoubleArray (x10_int_t size, x10_int_t offset, x10_int_t p) 
{
  m_length = size;
  m_offset = offset;
  m_domain = new RectangularRegion<1> (0-offset, size-1);
  m_start = 0;
  m_end = size-1;
    
  m_array = makeArrayRemote <double, 1, RectangularRegion, ConstDist> (m_domain, NULL, p);
}
  
double FtStatic::mysecond() 
{
  return (double) ((double) (nanoTime() / 1000) * 1.e-6);
}


x10_int_t FtStatic::switch_view (x10_int_t orientation, x10_int_t PID) 
{
  x10_int_t next_orientation = (orientation + 1) % 3;
  set_orientation (next_orientation);
  return next_orientation;
}

x10_int_t FtStatic::set_view (x10_int_t orientation, x10_int_t PID) 
{
  set_orientation (orientation);
  return orientation;
}

FtStatic::FtStatic( x10_int_t type, x10_int_t comm) : 
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
  checksum_real =  new double [MAX_ITER];
  for (x10_int_t i = 0; i < MAX_ITER; i++)
    checksum_real[i] = 0;
  
  checksum_imag =  new double [MAX_ITER];
  for (x10_int_t i = 0; i < MAX_ITER; i++)
    checksum_imag[i] = 0;
  
  
  //FFTWTest(); //the name of dll has to begin with lower case!!!
}
  
void  FtStatic::solve() {


  x10_int_t offset = 0;
  
  x10_int_t size = 0;
  
  char* buf = NULL;
  
  closure = (FT_COMM == FT_COMM_PENCILS) ? (Closure*) new __closure_1 [MAX_PADDED_SIZE/NUMPLACES] : 
    (Closure*) new __closure_0 [MAX_PADDED_SIZE/NUMPLACES];
  
  const DoubleArray* Planes2d = new DoubleArray (MAX_PADDED_SIZE/NUMPLACES, OFFSET);
  const DoubleArray* Planes1d = new DoubleArray (MAX_PADDED_SIZE/NUMPLACES, OFFSET);
  GLOBAL_STATE.Planes1d =const_cast<DoubleArray*>(Planes1d);
  const DoubleArray* V = new DoubleArray (MAX_PADDED_SIZE/NUMPLACES, OFFSET);
  const DoubleArray* ex = new DoubleArray (MAX_PADDED_SIZE/NUMPLACES, OFFSET);
    
  /* passing constants to C, which are stored as external variables */		  
  initializeC (NUMPLACES, NX, NY, NZ, OFFSET, CPAD_COLS);
   
  double cputime1 = -mysecond();		
   
  x10_place_t PID = PLACE->x10__id;

  CS = finishStart (CS);

  Clock* clk = new Clock;
   
  double cputime2; 
  x10_int_t current_orientation = set_view (PLANES_ORIENTED_X_Y_Z, PID);
   
  //clockNext (clk, 1);
     
  const DoubleArray* local_ex =  ex;
  
  const DoubleArray* localPlanes2d = Planes2d;
  
  const DoubleArray* localPlanes1d = Planes1d;

  const DoubleArray* local_V = V;
   
  FFTInit(FT_COMM, localPlanes2d->m_array, localPlanes1d->m_array, PID);

  init_exp(local_ex->m_array, 1.0e-6, PID);

  /*
   * Run the entire problem once to make sure all the data is touched. This
   * reduces variable startup costs, which is important for short benchmarks
   */

  //clockNext (clk, 1);
  
  computeInitialConditions(localPlanes2d->m_array, PID);
  
  //(3)
  //SyncGlobal();


  FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID, clk);

  SyncGlobal();

  closureCount = 0;

  FT_1DFFT(FT_COMM, localPlanes1d->m_array, localPlanes2d->m_array, 1, FFT_FWD, current_orientation, PID);

  SyncGlobal();
   
  if (PID == 0) cout << "Start timing of IBM X10 NAS FT: class " << class_id_char << " (" << class_id_str << ")" << endl;
  cputime2 = -mysecond();

  current_orientation = set_view(PLANES_ORIENTED_X_Y_Z,PID);

  computeInitialConditions(localPlanes2d->m_array, PID);

  init_exp(local_ex->m_array, 1.0e-6, PID);

  //(2)
  //  SyncGlobal();

  FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID, clk);
  
  SyncGlobal();

  closureCount = 0;

  FT_1DFFT (FT_COMM, localPlanes1d->m_array, local_V->m_array, 0, FFT_FWD, current_orientation, PID);

  //(1)
  SyncGlobal();
   
  current_orientation = switch_view(current_orientation, PID);

  x10_int_t saved_orientation = current_orientation;
   
  for (x10_int_t iter = 1; iter <= MAX_ITER; iter ++){
    
    current_orientation = set_view(saved_orientation, PID);
        
    parabolic2(localPlanes2d->m_array, local_V->m_array, local_ex->m_array, iter, 1.0e-6);
    
    FFT2DComm(localPlanes2d, Planes1d, FFT_BWD, current_orientation, PID, clk);
    
    SyncGlobal();
    
    closureCount = 0;
    
    FT_1DFFT(FT_COMM, localPlanes1d->m_array, localPlanes2d->m_array, 1, FFT_BWD, current_orientation, PID);

    current_orientation = switch_view(current_orientation, PID);
        
    checksum(localPlanes2d, PID, iter);
    
    SyncGlobal();

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

void FtStatic::FFT2DComm_Slab(const DoubleArray* local2d, const DoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* clk) {

  int dim0, dim1, dim2;
  int plane_size, CHUNK_SZ, numrows;
  
  dim0 = dims[orientation*3];
  dim1 = dims[orientation*3+1];
  dim2 = dims[orientation*3+2];
  
  //System.out.println(" orientation ="+orientation+" dim0 = "+dim0+" dim1 = "+dim1+" dim2 = "+dim2);
  plane_size = dim0*dim1;
  CHUNK_SZ = (dim0/NUMPLACES)*dim1;
  numrows = dim0/NUMPLACES;
  int p, t, i, offset1, offset2;
  
  Array<double, 1>* local2darray = local2d->m_array;
  
  //remove finish on August 1, 2007
  for (p = 0; p < dim2/NUMPLACES; p++) {
    offset1 = plane_size*p;
    FFT2DLocalCols(local2darray, offset1, dir, orientation, placeID);
    for (t = 0; t <NUMPLACES; t++) {
      //final int destID = (placeID + t) % NUMPLACES; //the MPI order
      const int destID = t;
      offset2 = offset1 + destID*CHUNK_SZ;
      
      for (i = 0; i < numrows; i++)
	FFT2DLocalRow(local2darray, offset2+i*dim1, dir, orientation, placeID);
      //int srcStart = local2d.m_start + offset2*2;
      int srcStart = offset2*2;
      //int destStart = (dist1d.getArray(destID)).m_start +
      //        2*(placeID*dim2/NUMPLACES+p)*CHUNK_SZ;
      int destStart = 2*(placeID*dim2/NUMPLACES+p)*CHUNK_SZ;
      int destPlace = UNIQUE->place (destID);
            
      //System.out.println(" 2DComm place: t = "+t+" placeID ="+placeID+ " destID = "+destID+ " destSstart ="+destStart);
      
      
      /* using asyncArrayCopy */
      /* __closure_0 closure (destStart);
      asyncArrayCopy ((void*) (local2darray->raw() +  srcStart + OFFSET), 
      &closure, 2 * CHUNK_SZ * sizeof(double), destID); */
      

      
      /* using asyncArrayCopyRaw */
      ((__closure_0*) closure)[closureCount].handler = 1;
      ((__closure_0*) closure)[closureCount].len = sizeof (__closure_0);
      ((__closure_0*) closure)[closureCount].args._destStart = destStart;
      
      asyncArrayCopyRaw ((void*) (local2darray->raw() + (srcStart + OFFSET)), 
			 &(((__closure_0*)closure))[closureCount], 2 * CHUNK_SZ * sizeof(double), destID); 
      
      closureCount++;
      
      
    }
  }
}

void  FtStatic::FFT2DComm_Pencil (const DoubleArray* local2d, const DoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* clk){
   
  x10_int_t dim0, dim1, dim2;
  x10_int_t plane_size, CHUNK_SZ, numrows;
     
  dim0 = dims[orientation*3];
  dim1 = dims[orientation*3+1];
  dim2 = dims[orientation*3+2];
       
  plane_size = dim0*dim1;

  CHUNK_SZ = (dim0/NUMPLACES)*dim1;

  numrows = dim0/NUMPLACES;
  x10_int_t p, t, i, offset1, offset2;
  
  Array<double, 1>* local2darray = local2d->m_array;
     
  //  x10_switch_t swch = AllocSwitch();

  for (p = 0; p < dim2/NUMPLACES; p++){
    offset1 = plane_size*p;
    
    FFT2DLocalCols (local2darray, offset1, dir, orientation, placeID);
    
    for (i = 0; i < numrows; i++) 
      
      for (t = 0; t <NUMPLACES; t++){
	
	x10_int_t destID = t;
	
	offset2 = offset1 + destID * CHUNK_SZ + i*dim1;
	
	FFT2DLocalRow(local2darray, offset2, dir, orientation, placeID);
	
	x10_int_t srcStart = offset2*2;
	
	x10_int_t destStart = 2*(placeID*dim2/NUMPLACES*dim1+p*dim1 + i*dim1*dim2);
	
	const x10_place_t destPlace =  UNIQUE->place (destID);
	
	//cout << "hello in FT " << endl;

	/* using asyncArrayCopy */
	/* __closure_1 closure (destStart);
	   asyncArrayCopy ((void*) (local2darray->raw() + (srcStart + OFFSET)), 
	   &closure, 2 * dim1 * sizeof(double), destID); */
	
	
	/* using asyncArrayCopyRaw */
	((__closure_1*) closure)[closureCount].handler = 1;
	((__closure_1*) closure)[closureCount].len = sizeof (__closure_1);
	((__closure_1*) closure)[closureCount].args._destStart = destStart;
	
	asyncArrayCopyRaw ((void*) (local2darray->raw() + (srcStart + OFFSET)), 
			   &(((__closure_1*)closure))[closureCount], 2 * dim1 * sizeof(double), destID); 
	closureCount++;
      
      }    
  }
   
}

	
void  FtStatic::FFT2DComm(const DoubleArray* local2d, const DoubleArray* dist1d, const x10_int_t dir, const x10_int_t orientation, const x10_int_t placeID, Clock* clk)
{
  if (FT_COMM == FT_COMM_SLABS)
    FFT2DComm_Slab (local2d, dist1d, dir, orientation, placeID, clk);
  else
    FFT2DComm_Pencil (local2d, dist1d, dir, orientation, placeID, clk);
}

void  FtStatic::checksum(const DoubleArray* C, const x10_int_t PID, const x10_int_t itr) {
  x10_int_t j, q, r, s, idx;
  double sum_real = 0;
  double sum_imag = 0;
     
  x10_int_t proc;
  const double* temp = BASE_PTR(C->m_array) + OFFSET; 

  x10_int_t total=0;
  //  CS = finishStart (CS);
  for (j=1; j <= 1024; ++j){
    q = j % NX;
    r = (3*j) % NY;
    s = (5*j) % NZ;
       
    proc = getowner(q, r, s);
    if (proc == PID) {
      total++;
      idx = 2*origindexmap(q,r,s);//+C.m_start;
      //System.out.prx10_int_tln(" [ "+proc+", "+idx+"])");
      sum_real+=temp[idx];
      sum_imag+=temp[idx+1];
    }
  }

  // finishEnd (NULL);

  double res_real = ((sum_real/NX)/NY)/NZ;
  double res_imag = ((sum_imag/NX)/NY)/NZ;  

  //CS = finishStart(CS);

  //  complex c (res_real, res_imag);
  //reduce (&c);

  reduce(&res_real);
  reduce(&res_imag);
  finishReduceAll<double, add>();

  GLOBAL_STATE.FtSolver->checksum_real[itr-1] = res_real; //c.real;
  GLOBAL_STATE.FtSolver->checksum_imag[itr-1] = res_imag; //c.imag;


  //  __async__0__args args0(itr, res_real, res_imag);
  //if (0 == PLACE->id)
  //  __async__0 (args0);
  //else
  //  asyncSpawnInline (0, 0, &args0 , sizeof(__async__0__args));
  
  //asyncFlush (0, sizeof(__async__0__args));
  
  //finishEnd (NULL);
} 
   
   
void  FtStatic::print_Array(const DoubleArray* DDA){

  x10_place_t PID = PLACE->x10__id;   
  const DoubleArray* da = DDA;
  cout << "At place " << PID << endl;
     
  for (x10_int_t i = da->m_domain->origin().value(0); i < da->m_domain->diagonal().value(0); i++) {
    if (i%2 == 0) 
      cout << " [" << (i/2) << "]= (" <<  BASE_PTR(da->m_array)[i] << endl;
    else
      cout << ", " <<  BASE_PTR(da->m_array)[i] << ")" << endl;
  }	
}
   
   
void   FtStatic::main (x10::array<x10::ref<x10::lang::String> >& args) {
  
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
     
  x10_int_t CLASS = 0; 
  x10_int_t COMM = FT_COMM_SLABS;  
  for (x10_int_t q = 0; q < args.x10__length; ++q) {		
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
  
  GLOBAL_STATE.FtSolver = new FtStatic (CLASS, COMM);
  GLOBAL_STATE.FtSolver->solve();
}	
   
   
void  FtStatic::checksum_verify(const x10_int_t d1, const x10_int_t d2, const x10_int_t d3, const x10_int_t nt,
			  const double* real_sums, 
			  const double* imag_sums){
     
  x10_int_t i;
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
   
x10_int_t FtStatic::NUMPLACES=numPlaces();
const Dist<1>* FtStatic::UNIQUE = Dist<1>::makeUnique();

extern "C" {
int main (x10_int_t ac, char* av[])
{
x10::array<x10::ref<x10::lang::String> >* args = x10::convert_args (ac, av);
    
FtStatic::main (*args);
    
x10::free_args (args);
        
return x10::exitCode;    
}
}

void
__async__0 (__async__0__args args)
{
  x10_int_t itr = args.captVar1;
  double res_real = args.captVar2;
  double res_imag = args.captVar3;
  GLOBAL_STATE.FtSolver->checksum_real[itr-1] += res_real;
  GLOBAL_STATE.FtSolver->checksum_imag[itr-1] += res_imag;
}


void
asyncSwitch (x10_async_handler_t h, void* arg, x10_int_t niter)
{
  char* args = (char*) arg;
  switch (h) {
  case 0:
    for (x10_int_t i = 0; i < niter; i++) {
      __async__0(*((__async__0__args*)args));
      args += sizeof(__async__0__args);
    }
    break;
  }
}


void*
arrayCopySwitch (int handler, void* args)
{

  switch (handler) {
  case 0:
    {
      __array_copy_args_0 *array_copy_args = (__array_copy_args_0*) args;
    return (GLOBAL_STATE.Planes1d->m_array->raw() + array_copy_args->_destStart); 
    }
  case 1:
    {
      __array_copy_args_1 *array_copy_args = (__array_copy_args_1*) args;
    //    cout << "hello " << closure_args->destOffset << endl;
    return (GLOBAL_STATE.Planes1d->m_array->raw() + array_copy_args->_destStart); 
    }
  }
  
  return NULL;
}

