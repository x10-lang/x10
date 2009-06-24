/*****************************************************************************
 * This is the X10 port of the C/MPI implementation of NAS FT Benchmark in the
 * Berkeley UPC FT suite.
 *
 * The allocation of the distributed working arrays, the synchronization,
 * and the communication are implemented at the x10 level, whereas the other
 * functionalities are still implemented using the original c codes.
 *
 * Pleae note that 1D double arrays are used in x10 while 1D complex arrays are
 * used in C. So, the index for the C part of this implementation is in terms of
 * complex numbers in contrast to doubles for the X10 part.
 *
 * For details, please refer to the README file.
 *
 * To compile and run the code, for example, do the following:
 *  1) x10c Ft.x10 (Add appropriate x10c options if needed)
 *  2) make
 *  3) x10  -J-mx2000m -J-ms2000m -libpath . -NUMBER_OF_LOCAL_PLACES=4 -INIT_THREADS_PER_PLACE=1 \
 *          -PRELOAD_CLASSES=true Ft -w [-p]
 *
 * The output looks like 
 *	Start timing of IBM X10 NAS FT: class W (NPB CLASS W 128x128x32 6 iterations, FT_COMM = 0)
 *	Iter = 1 checksum_real = 567.361217894362 checksum_imag = 529.3246849175001
 *	Iter = 2 checksum_real = 563.1436885271293 checksum_imag = 528.2149986629049
 *	Iter = 3 checksum_real = 559.4024089970092 checksum_imag = 527.0996558037011
 *	Iter = 4 checksum_real = 556.069804702024 checksum_imag = 526.0027904925023
 *	Iter = 5 checksum_real = 553.089899124982 checksum_imag = 524.9400845632628
 *	Iter = 6 checksum_real = 550.4159734538397 checksum_imag = 523.9212247086305
 *	The wall clock time for the timed section is 1.6411430000007385 secs
 *	The mean wall clock time for the timed section is 0.6077015000009851
 *	The overall wall clock time is 2.9783509999979287 secs
 *	Result verification successful
 *
 * Related files: Ft.c Ft.h fft_fftw3 c_randdp
 * Author: Tong Wen @ IBM Research
 * Date:   June 2007
 * Modification:
 *    On Aug 1, 2007: local x10 array uses local indexes instead of global ones;
 *            finish in FFT2DComm is removed and asyn is clocked.
 *    On Aug 7, 2007: remove the "if (PID == 0)" check in switch_view and set_view,
 *            which is special to the Java implementation of X10.
 *    On Aug 9, 2007: clean up "next"
 *    On Aug 10, 2007: remove finish and make async clocked in checksum().
 *    On Aug 14, 2007: add a new constructor of DoubleArray.
 *       Dec 07, 2007: fix the indexing error in arrayCopy
 */

public final value Ft {

	private static final boolean MAKE_FFTW_THREADSAFE = false; /* MAKE_FFTW_THREADSAFE in fft-fftw3.c must be set to 0, if false*/
	private final static int OFFSET = 3;
	private final static int CPAD_COLS = 0;
	private final static int PLANES_ORIENTED_X_Y_Z = 0; /*assumes original data layout (each plane is
	                                                      row major with the z dimension appearing z*NX*NY */
	private final static int PLANES_ORIENTED_Y_Z_X = 1; /*assumes that Y Z planes are contiguous in the memory*/
	private final static int PLANES_ORIENTED_Z_X_Y = 2; /*assumes that Z X planes are contiguous in the memory*/
	private final static int FT_COMM_SLABS = 0;
	private final static int FT_COMM_PENCILS = 1;
	private final static int FFT_FWD = 1;
	private final static int FFT_BWD = 0;

	public final int FT_COMM;

	public static value DoubleArray {
		double[:self.rect && self.rank==1] m_array;
		region (:rank==1 && self.rect) m_domain;
		int m_length;
		int m_offset;
		int m_start;
		int m_end;

		DoubleArray(int size, int offset) {
			m_length = size;
			m_offset = offset;
			m_domain = [0 : size-1];
			m_start = 0; m_end = size-1;
			// Arrays should be aligned with the cache line size (128 for FT).
			m_array = (double[:self.rect && self.rank==1]) new double[[-offset : size-1]];
		}
		
		DoubleArray(int size, int offset, place p) {
			m_length = size;
			m_offset = offset;
			m_domain = [0 : size-1];
			m_start = 0; m_end = size-1;
			// Arrays should be aligned with the cache line size (128 for FT).
			m_array = (double[:self.rect && self.rank==1]) new double[[-offset : size-1]->p];
		}
		
		DoubleArray(int start, int end, int offset) {
			m_length = end-start+1;
			m_offset = offset;
			m_domain = [start : end];
			m_start = start; m_end = end;
			//Arrays should be aligned with the cache line size.
			m_array = (double[:self.rect && self.rank==1]) new double[[start-offset : end]];
		}
		
		
	}

	// Distributed arrays of complex numbers, one array per place
	public static value DistDoubleArray {
		dist(:rank==1) UNIQUE = (dist(:rank==1))dist.UNIQUE;
		int N_PLACES = place.MAX_PLACES; //must be even

		DoubleArray value [:self.rank==1] m_array;
		long m_size;
		int m_localSize;

		DoubleArray getArray(int idx) {
			return m_array[idx];
		}
		/* The dimension is logical */
		DistDoubleArray(final long size, final int offset) {
			assert size >= N_PLACES;
			m_size = size;
			m_localSize = (int)(size/N_PLACES);
			dist(:rank==1) R = [0:N_PLACES-1]-> here; //otherwise you need to put a cast clause for the next statment
			m_array = new DoubleArray value [R]; /* (point p) {
								return new DoubleArray(m_localSize, offset);}; */

			/* It is assumed that the array size in the first dimension can be divided evenly by N_PLACES.
			 * Otherwise, a little bit more sophisticated load balancing algorithm should be employed.
			 * And the index here is global.
			 m_array = new DoubleArray value [UNIQUE]; //version 1.9 shouldn't pass compilation here. */
			finish for (point [i]: UNIQUE) { //changed to for from ateach on Aug 14, 2007
				//int a = i*m_localSize, b = (i+1)*m_localSize-1;
				//m_array[i] = new DoubleArray(a, b, offset);
				m_array[i] = new DoubleArray(m_localSize, offset, UNIQUE[i]);
			}
		}
	}

	private final static int NUMPLACES = place.MAX_PLACES;
	private final static dist(:rank==1) UNIQUE = (dist(:rank==1)) dist.UNIQUE;

	private final static int SS = 1;
	private final static int WW = 2;
	private final static int AA = 3;
	private final static int BB = 4;
	private final static int CC = 5;
	private final static int DD = 6;

	private final String class_id_str;
	private final char class_id_char;
	private final int CLASS, NX, NY, NZ, MAXDIM, MAX_ITER;
	private final long TOTALSIZE, MAX_PADDED_SIZE;
	private final int [:self.rect && self.rank==1] dims = (int[:self.rect && self.rank==1]) new int [[0:8]];

	private final double [:self.rect && self.rank==1] checksum_real;
	private final double [:self.rect && self.rank==1] checksum_imag;

	static {
		System.load("c:/FFTW/fftw-3.1.2-dll/libfftw3-3.dll"); //only for cygwin environment; comment it out for AIX
		System.loadLibrary("ft"); //for cygwin change it to Ft from ft (AIX)
	}

	public static double mysecond() {
		return (double) ((double)(System.nanoTime() / 1000) * 1.e-6);
	}

	public static extern void initialize(double[:self.rect && self.rank==1] Array, int size, int offset, int PID);
	public static extern void initializeC(int numPlace, int nx, int ny, int nz, int offset, int cpad_cols);
	public static extern void computeInitialConditions(double[:self.rect && self.rank==1] Array, int PID);
	public static extern void init_exp(double[:self.rect && self.rank==1] Array, double alpha, int PID);
	public static extern void set_orientation(int orientation);
	public static extern void parabolic2(double[:self.rect && self.rank==1] out,
	                                     double[:self.rect && self.rank==1] in,
	                                     double[:self.rect && self.rank==1] ex, int t, double alpha);
	public static extern void FFTInit(int comm, double[:self.rect && self.rank==1] local2d,
	                                     double[:self.rect && self.rank==1] local1d, int PID);
	public static extern void FFTWTest();
	public static extern void FT_1DFFT(int ft_comm, double[:self.rect && self.rank==1] in,
	                                     double[:self.rect && self.rank==1] out,
	                                     int offset, int dir, int orientation, int PID);
	public static extern void FFT2DLocalCols(double[:self.rect && self.rank==1] local2d,
	                                     int ComplexOffset, int dir, int orientation, int PID);
	public static extern void FFT2DLocalRow(double[:self.rect && self.rank==1] local2d,
	                                     int ComplexOffset, int dir, int orientation, int PID);

	public static extern int origindexmap(int x, int y, int z);
	public static extern int getowner(int x, int y, int z);

	private int switch_view(int orientation, int PID) {
		int next_orientation = (orientation + 1)%3;
		//if (PID == 0) set_orientation(next_orientation);
		set_orientation(next_orientation);
		return next_orientation;
	}

	private  int set_view(int orientation, int PID) {
		//if (PID == 0) set_orientation(orientation);
		set_orientation(orientation);
		return orientation;
	}

	public Ft(int type, int comm) {
		CLASS = type;
		FT_COMM = comm;
		switch (CLASS) {
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
		//System.out.println(class_id_str+" FT_COMM = "+FT_COMM); 

		TOTALSIZE = NX*NY*(long)NZ;
		MAX_PADDED_SIZE = Math.max(2L*NX*(NY+CPAD_COLS)*NZ, Math.max(2L*NY*(NZ+CPAD_COLS)*NX, 2L*NZ*(NX+CPAD_COLS)*NY));

		dims[0] = NX; dims[1] = NY+CPAD_COLS; dims[2] = NZ;
		dims[3] = NY; dims[4] = NZ+CPAD_COLS; dims[5] = NX;
		dims[6] = NZ; dims[7] = NX+CPAD_COLS; dims[8] = NY;
		checksum_real = (double [:self.rect && self.rank==1]) new double [[1:MAX_ITER]]; // (point p) {return 0;};
		checksum_imag = (double [:self.rect && self.rank==1]) new double [[1:MAX_ITER]]; // (point p) {return 0;};

		//FFTWTest(); //the name of dll has to begin with lower case!!!
	}

	public void solve() {

		final DistDoubleArray Planes2d = new DistDoubleArray(MAX_PADDED_SIZE, OFFSET);
		final DistDoubleArray Planes1d = new DistDoubleArray(MAX_PADDED_SIZE, 0);
		final DistDoubleArray V = new DistDoubleArray(MAX_PADDED_SIZE, 0);
		final DistDoubleArray ex = new DistDoubleArray(TOTALSIZE, 0);

		double cputime3 = -mysecond();

		final double [:rank==1] timers =new double [UNIQUE] (point p) {return 0;};
		finish async {
			final clock clk=clock.factory.clock();
			ateach (point [PID]: UNIQUE) clocked(clk) {
				/* passing constants to C, which are stored as external variables */
				initializeC(NUMPLACES, NX, NY, NZ, OFFSET, CPAD_COLS);
				double cputime1=0, cputime2=0;
				int current_orientation = set_view(PLANES_ORIENTED_X_Y_Z,PID);

				final DoubleArray local_ex = ex.getArray(PID);
				final DoubleArray localPlanes2d = Planes2d.getArray(PID);
				final DoubleArray localPlanes1d = Planes1d.getArray(PID);
				final DoubleArray local_V = V.getArray(PID);

				FFTInit(FT_COMM, localPlanes2d.m_array, localPlanes1d.m_array, PID);
				if (!MAKE_FFTW_THREADSAFE) next; //next needed here when MAKE_FFTW_THREADSAFE=0 in fft-fftw3.c
				init_exp(local_ex.m_array, 1.0e-6, PID);
				/*
				 * Run the entire problem once to make sure all the data is touched. This
				 * reduces variable startup costs, which is important for short benchmarks
				 */
				computeInitialConditions(localPlanes2d.m_array, PID);
				FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID, clk);
				next;
				FT_1DFFT(FT_COMM, localPlanes1d.m_array, localPlanes2d.m_array, 1, FFT_FWD, current_orientation, PID);
				next;  

				if (PID == 0) System.out.println("Start timing of IBM X10 NAS FT: class "+class_id_char+" ("+class_id_str+", FT_COMM = "+FT_COMM+")");
				cputime2 = -mysecond(); cputime1=cputime2;
				current_orientation = set_view(PLANES_ORIENTED_X_Y_Z,PID);
				computeInitialConditions(localPlanes2d.m_array, PID);
				init_exp(local_ex.m_array, 1.0e-6, PID);
				FFT2DComm(localPlanes2d, Planes1d, FFT_FWD, current_orientation, PID, clk);
				cputime1 += mysecond(); timers[PID]+=cputime1;
				next;
				cputime1 = -mysecond(); 
				FT_1DFFT(FT_COMM, localPlanes1d.m_array, local_V.m_array, 0, FFT_FWD, current_orientation, PID);
				cputime1 += mysecond(); timers[PID]+=cputime1;
				next; //not redundant
				cputime1 = -mysecond();
				current_orientation = switch_view(current_orientation, PID);
				int saved_orientation = current_orientation;
				
				for (int iter = 1; iter <= MAX_ITER; iter ++) {
					current_orientation = set_view(saved_orientation, PID);
					parabolic2(localPlanes2d.m_array, local_V.m_array, local_ex.m_array, iter, 1.0e-6);
					FFT2DComm(localPlanes2d, Planes1d, FFT_BWD, current_orientation, PID, clk);
					cputime1 += mysecond(); timers[PID]+=cputime1;
					next;
					cputime1 = -mysecond();
					FT_1DFFT(FT_COMM, localPlanes1d.m_array, localPlanes2d.m_array, 1, FFT_BWD, current_orientation, PID);
					current_orientation = switch_view(current_orientation, PID);
					checksum(localPlanes2d, PID, iter, clk);
					cputime1 += mysecond(); timers[PID]+=cputime1;
					next;
					cputime1 = -mysecond();
					if (PID == 0) {
						System.out.println(" Iter = "+iter+" checksum_real = "+
								checksum_real[iter]+" checksum_imag = "+checksum_imag[iter]);
					}
				}
				cputime2 += mysecond();
				if (PID == 0) System.out.println("The wall clock time for the timed section is "+cputime2+" secs");
			}
		}
		
		double sum = timers.sum();
		if (here.id == 0) System.out.println("The mean wall clock time for the timed section is "+(sum/NUMPLACES));
		cputime3 += mysecond();
		System.out.println("The overall wall clock time is "+cputime3+" secs");
		
		if (class_id_char !='T') checksum_verify(NX, NY, NZ, MAX_ITER, checksum_real, checksum_imag);
		//System.out.println("Content of Planes2d:"); printArray(Planes2d);
		//System.out.println("Content of ex:"); printArray(ex);
		//System.out.println("Content of Planes1d:"); printArray(Planes1d);
	}


	public void FFT2DComm_Slab(final DoubleArray local2d, final DistDoubleArray dist1d, final int dir, final int orientation, final int placeID, final clock clk) {
		final int dim0, dim1, dim2;
		final int plane_size, CHUNK_SZ, numrows;

		dim0 = dims[orientation*3];
		dim1 = dims[orientation*3+1];
		dim2 = dims[orientation*3+2];
		//System.out.println(" orientation ="+orientation+" dim0 = "+dim0+" dim1 = "+dim1+" dim2 = "+dim2);
		plane_size = dim0*dim1;
		CHUNK_SZ = (dim0/NUMPLACES)*dim1;
		numrows = dim0/NUMPLACES;
		int p, t, i, offset1, offset2;

		final double[:self.rect && self.rank==1] local2darray = local2d.m_array;
		//remove finish on August 1, 2007
		for (p = 0; p < dim2/NUMPLACES; p++) {
			offset1 = plane_size*p;
			FFT2DLocalCols(local2darray, offset1, dir, orientation, placeID);
			for (t = 0; t <NUMPLACES; t++) {
				//final int destID = (placeID + t) % NUMPLACES; //the MPI order
				final int destID = t;
				offset2 = offset1 + destID*CHUNK_SZ;

				for (i = 0; i < numrows; i++)
					FFT2DLocalRow(local2darray, offset2+i*dim1, dir, orientation, placeID);
				//int srcStart = local2d.m_start + offset2*2;
				final int srcStart = offset2*2;
				//int destStart = (dist1d.getArray(destID)).m_start +
				//        2*(placeID*dim2/NUMPLACES+p)*CHUNK_SZ;
				final int destStart = 2*(placeID*dim2/NUMPLACES+p)*CHUNK_SZ;
				final place destPlace = UNIQUE[destID];
				final double[:self.rect && self.rank==1] local1darray =
					(dist1d.getArray(destID)).m_array;

				//System.out.println(" 2DComm place: t = "+t+" placeID ="+placeID+ " destID = "+destID+ " destSstart ="+destStart);
				async (destPlace) clocked(clk) {
					x10.lang.Runtime.arrayCopy(local2darray, srcStart+OFFSET, dist1d.getArray(here.id).m_array, destStart, 2*CHUNK_SZ);
				}
				//the element wise version of the above arrayCopy method
				/*for (int j = 0; j < 2*CHUNK_SZ; j++) {
					final double srcVal = local2darray[srcStart + j];
					final int destIdx = destStart + j;
					//add clocked clause on Aug 1, 2007
					async (destPlace) clocked(clk) {
						local1darray[destIdx] = srcVal;
					}
				}*/
			}
		}
		//System.out.flush();
	}

	public void FFT2DComm_Pencil(final DoubleArray local2d, final DistDoubleArray dist1d, final int dir, final int orientation, final int placeID, final clock clk) {
		final int dim0, dim1, dim2;
		final int plane_size, CHUNK_SZ, numrows;

		dim0 = dims[orientation*3];
		dim1 = dims[orientation*3+1];
		dim2 = dims[orientation*3+2];

		plane_size = dim0*dim1;
		CHUNK_SZ = (dim0/NUMPLACES)*dim1;
		numrows = dim0/NUMPLACES;
		int p, t, i, offset1, offset2;

		final double[:self.rect && self.rank==1] local2darray = local2d.m_array;
		//remove finish on August 1, 2007
		for (p = 0; p < dim2/NUMPLACES; p++) {
			offset1 = plane_size*p;
			FFT2DLocalCols(local2darray, offset1, dir, orientation, placeID);
			for (i = 0; i < numrows; i++)
				for (t = 0; t <NUMPLACES; t++) {
					//final int destID = (placeID + t) % NUMPLACES; //the MPI order
					final int destID = t;
					offset2 = offset1 + destID*CHUNK_SZ + i*dim1;
					FFT2DLocalRow(local2darray, offset2, dir, orientation, placeID);

					//int srcStart = local2d.m_start + offset2*2;
					final int srcStart = offset2*2;
					//int destStart = (dist1d.getArray(destID)).m_start +
					//        2*(placeID*dim2/NUMPLACES*dim1+p*dim1 + i*dim1*dim2);
					final int destStart = 2*(placeID*dim2/NUMPLACES*dim1+p*dim1 + i*dim1*dim2);
					final place destPlace = UNIQUE[destID];
					final double[:self.rect && self.rank==1] local1darray =
						(dist1d.getArray(destID)).m_array;
					async (destPlace) clocked(clk) {
						x10.lang.Runtime.arrayCopy(local2darray, srcStart+OFFSET, dist1d.getArray(here.id).m_array, destStart, 2*dim1);
					}
					/*for (int j = 0; j < 2*dim1; j++) {
						final double srcVal = local2darray[srcStart + j];
						final int destIdx = destStart + j;
						//add clocked clause on Aug 1, 2007
						async (destPlace) clocked(clk) {
							local1darray[destIdx] = srcVal;
						}
					}*/
				}
		}
	}

	public void FFT2DComm(final DoubleArray local2d, final DistDoubleArray dist1d, final int dir, final int orientation, final int placeID, final clock clk) {
		if (FT_COMM == FT_COMM_SLABS)
			FFT2DComm_Slab(local2d, dist1d, dir, orientation, placeID, clk);
		else
			FFT2DComm_Pencil(local2d, dist1d, dir, orientation, placeID, clk);
	}

	private void checksum(final DoubleArray C, final int PID, final int itr, final clock clk) {
		int j, q, r, s, idx;
		double sum_real = 0;
		double sum_imag = 0;

		int proc;
		final double[:self.rect && self.rank==1] temp = C.m_array;
		finish for (j=1; j <= 1024; ++j) {
			q = j % NX;
			r = (3*j) % NY;
			s = (5*j) % NZ;

			proc = getowner(q, r, s);
			if (proc == PID) {
				//idx = 2*origindexmap(q,r,s)+C.m_start;
				idx = 2*origindexmap(q,r,s);
				//System.out.println(" [ "+proc+", "+idx+"])");
				sum_real+=temp[idx];
				sum_imag+=temp[idx+1];
			}
		}
		final double res_real = ((sum_real/NX)/NY)/NZ;
		final double res_imag = ((sum_imag/NX)/NY)/NZ;
		async(UNIQUE[0]) clocked(clk) atomic { //On Aug 10, 2007remove finish and make async clocked
			checksum_real[itr]+=res_real;
			checksum_imag[itr]+=res_imag;
		}
	}

	private void printArray(final DistDoubleArray DDA) {
		finish ateach (point [PID]: UNIQUE) {
			DoubleArray da = DDA.getArray(PID);
			System.out.println("At place "+PID);
			for (point [i]: da.m_domain)
				if (i%2 == 0)
					System.out.print(" ["+(i/2)+"]= ("+ da.m_array[i]);
				else
					System.out.print(", "+ da.m_array[i]+")");
			System.out.println();
		}
	}

	public static void  main(String[] args) {
		if (NUMPLACES>1 &&(NUMPLACES != (NUMPLACES/2*2))) {
			System.out.println("The number of places must be even.");
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
			if (args[q].equals("-s") || args[q].equals("-S")) {
				CLASS = 1;
			}
			if (args[q].equals("-w") || args[q].equals("-W")) {
				CLASS = 2;
			}
			if (args[q].equals("-a") || args[q].equals("-A")) {
				CLASS = 3;
			}
			if (args[q].equals("-b") || args[q].equals("-B")) {
				CLASS = 4;
			}
			if (args[q].equals("-c") || args[q].equals("-C")) {
				CLASS = 5;
			}
			if (args[q].equals("-d") || args[q].equals("-D")) {
				CLASS = 6;
			}
			if (args[q].equals("-p") || args[q].equals("-P")) {
				COMM = FT_COMM_PENCILS;
			}
		}

		final Ft FtSolver = new Ft(CLASS, COMM);
		FtSolver.solve();
	}

	private void checksum_verify(final int d1, final int d2, final int d3, final int nt,
	                             final double [:self.rect && self.rank==1] real_sums,
	                             final double [:self.rect && self.rank==1] imag_sums)
	{
		int i;
		double err, epsilon;
		boolean known_class = false;
		boolean verified = false;
		/*--------------------------------------------------------------------
		  c   Sample size reference checksums
		  c-------------------------------------------------------------------*/

		/*--------------------------------------------------------------------
		  c   Class S size reference checksums
		  c-------------------------------------------------------------------*/
		double [] vdata_real_s =
		{
			5.546087004964e+02,
			5.546385409189e+02,
			5.546148406171e+02,
			5.545423607415e+02,
			5.544255039624e+02,
			5.542683411902e+02
		};
		double [] vdata_imag_s =
		{
			4.845363331978e+02,
			4.865304269511e+02,
			4.883910722336e+02,
			4.901273169046e+02,
			4.917475857993e+02,
			4.932597244941e+02
		};
		/*--------------------------------------------------------------------
		  c   Class W size reference checksums
		  c-------------------------------------------------------------------*/
		double [] vdata_real_w =
		{
			5.673612178944e+02,
			5.631436885271e+02,
			5.594024089970e+02,
			5.560698047020e+02,
			5.530898991250e+02,
			5.504159734538e+02
		};
		double [] vdata_imag_w =
		{
			5.293246849175e+02,
			5.282149986629e+02,
			5.270996558037e+02,
			5.260027904925e+02,
			5.249400845633e+02,
			5.239212247086e+02
		};
		/*--------------------------------------------------------------------
		  c   Class A size reference checksums
		  c-------------------------------------------------------------------*/
		double [] vdata_real_a =
		{
			5.046735008193e+02,
			5.059412319734e+02,
			5.069376896287e+02,
			5.077892868474e+02,
			5.085233095391e+02,
			5.091487099959e+02
		};
		double [] vdata_imag_a =
		{
			5.114047905510e+02,
			5.098809666433e+02,
			5.098144042213e+02,
			5.101336130759e+02,
			5.104914655194e+02,
			5.107917842803e+02
		};
		/*--------------------------------------------------------------------
		  c   Class B size reference checksums
		  c-------------------------------------------------------------------*/
		double [] vdata_real_b =
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
			5.124146770029e+02
		};
		double [] vdata_imag_b =
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
			5.115744692211e+02
		};
		/*--------------------------------------------------------------------
		  c   Class C size reference checksums
		  c-------------------------------------------------------------------*/
		double [] vdata_real_c =
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
			5.129714421109e+02
		};
		double [] vdata_imag_c =
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
			5.123465164008e+02
		};
		double [] vdata_real_d =
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
			5.118822370068e+02
		};
		double [] vdata_imag_d =
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
			5.119794338060e+02
		};

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
				err = (real_sums[i+1] - vdata_real_s[i]) / vdata_real_s[i];
				if (Math.abs (err) > epsilon)
				{

					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_s[i]) / vdata_imag_s[i];
				if (Math.abs (err) > epsilon)
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
				err = (real_sums[i+1] - vdata_real_w[i]) / vdata_real_w[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_w[i]) / vdata_imag_w[i];
				if (Math.abs (err) > epsilon)
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
				err = (real_sums[i+1] - vdata_real_a[i]) / vdata_real_a[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_a[i]) / vdata_imag_a[i];
				if (Math.abs (err) > epsilon)
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
				err = (real_sums[i+1] - vdata_real_b[i]) / vdata_real_b[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_b[i]) / vdata_imag_b[i];
				if (Math.abs (err) > epsilon)
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
				err = (real_sums[i+1] - vdata_real_c[i]) / vdata_real_c[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_c[i]) / vdata_imag_c[i];
				if (Math.abs (err) > epsilon)
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
				err = (real_sums[i+1] - vdata_real_d[i]) / vdata_real_d[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
				err = (imag_sums[i+1] - vdata_imag_d[i]) / vdata_imag_d[i];
				if (Math.abs (err) > epsilon)
				{
					verified = false;
					break;
				}
			}
		}

		if (known_class && verified)
		{
			System.out.println(" Result verification successful\n");
		}
		else
		{
			System.out.println(" Result verification failed\n");
		}
	}
}

