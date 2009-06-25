#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

int n;
double *A, *Tmp;
const double epsilon = 0.000001;
int main(int argc, char* argv[]) {
  int i, iters;
  double delta;
  int numprocs, rank, mysize;
  double sum;
  
  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  if (argc != 2) {
    printf("usage: fixedpt n\n");
    exit(1);
  }
  n = atoi(argv[1]);
  mysize  = n * (rank+1)/numprocs -  n *  rank / numprocs;
  A = malloc((mysize+2)*sizeof(double));
  for (i = 0; i <= mysize; i++) 
    A[i] = 0.0;
  if (rank == numprocs - 1)
    A [mysize+1] = n + 1.0;
  Tmp = malloc((mysize+2)*sizeof(double));
  iters = 0;
  do {
    iters++;
    id (rank < numprocs -1)
      MPI_Send(&(A[mysize]), 1, MPI_DOUBLE, rank+1, 1, MPI_COMM_WORLD);
    if (rank > 0)
      MPI_Recv(&(A[0]), 1, MPI_DOUBLE, rank-1, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    if (rank > 0)
      MPI_Send(&(A[1]), 1, MPI_DOUBLE, rank-1, 1, MPI_COMM_WORLD);
    if (rank < numprocs-1)
      MPI_Recv(&(A[mysize+1]), 1, MPI_DOUBLE, rank+1, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    for (i=1; i <=mysize; i++)
      Tmp[i] = (A[i-1]+A[i+1])/2.0;
    delta = 0.0;
    for (i = 1; i <= mysize; i++)
      delta +=fabs(A[i]-Tmp[i]);
    MPI_Allreduce(&delta, &sum, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    delta = sum;
    for (i = 1; i <= mysize; i++)
      A[i]=Tmp[i];
  } while (delta > epsilon);
  if (rank == 0)
    printf("Iterations: %d\n", iters);
  MPI_Finalize();
}
