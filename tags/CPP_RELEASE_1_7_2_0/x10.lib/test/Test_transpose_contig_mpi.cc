/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Test_transpose_contig_mpi.cc,v 1.1 2007-12-04 11:38:57 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

/* transpose a 2D array */

#include <iostream>
#include <x10/timers.h>
#include "mpi.h"

#define NBLK 16

using namespace std;

double* data;
double* data2;
const int* lda;

int 
main (int argc, char* argv[])
{

  MPI_Init (&argc, &argv);


  if (argc != 2)  {
     cout << "Syntax: ./a.out SQRTN " << endl;
     exit (-1);
  }

  int rank, nplaces;
  MPI_Comm_size (MPI_COMM_WORLD, &nplaces);
  MPI_Comm_rank (MPI_COMM_WORLD, &rank);

  int SQRT_N = atoi (argv[1]);

  int X = SQRT_N /  nplaces;

  int Y = SQRT_N;

  int nRows = SQRT_N / nplaces;

  int N = X * Y; 
  
  data = new double[N];
  data2 = new double[N];

  int P = rank;

  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	data [i * Y + j] = (i + P * nRows) * Y + j;
       	data2 [i * Y + j] =  0;
      }
  

  int n = 0;

  double timers[4];
  timers[0] = nanoTime();
  /* tranpose local chunk and copy to contiguous location */

  for (int ii = 0; ii < X; ii += NBLK)
    for (int jj = 0; jj < Y; jj += NBLK) 
      for (int i = ii; i < ii + NBLK; i++)
         for (int j = jj; j < jj + NBLK; j++)
             data2 [j * X + i] = data [ i * Y + j];

  timers[1] = nanoTime();

  int chunk_size = nRows * nRows;
  MPI_Alltoall (data2, chunk_size,  MPI_DOUBLE, 
                data, chunk_size, MPI_DOUBLE,
                MPI_COMM_WORLD);
  
  timers[2] = nanoTime();

  /* scatter the result back, so we get the row contributions from 
   * different processors in contiguous locations
   */
  int n2 = X;
  int n1 = nplaces;
  for (int k = 0; k < n2; k++)
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
      data2 [k * n2 * n1 + j + i * n2] = data [ i * n2 * n2 + k * n2 + j];
  
  timers[3] = nanoTime();

  for (int i = 0; i < X; i++)
    for (int j = 0; j < Y; j++)
      {
	assert (data2 [i * Y + j] == j * Y + i + P * nRows);       	
      }
 
  cout << "*************** Summary BEGIN ***********************************" << endl
       << "***************** Timing (Seconds) BEGIN************************* " << endl
       << "Total Time: " << (timers[3] - timers[0]) * 1e-9 << endl
       << "local transposition: " << (timers[1] - timers[0]) * 1e-9 << endl
       << "alltoall: " << (timers[2] - timers[1]) * 1e-9 << endl
       << "scatter: " << (timers[3] - timers[2]) * 1e-9 << endl
       << "***************** Timing (Seconds) END ************************* " << endl
       << "Total Memory (per place) : " << 2 * X * Y * sizeof(double) / (1024 * 1024) << "Mega Bytes" << endl
       << "*************** Summary END ***********************************" << endl ;

  cout << "Test_transpose_contig PASSED" << endl;


  MPI_Finalize();
 
  return 0;
}
