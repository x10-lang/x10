#ifndef SUMMA_MPI_IMP_H
#define SUMMA_MPI_IMP_H

#ifdef MPI_COMMU

#include "mpi.h"

void get_usetime(double*);
void reset_usetime();

void pdgemm(int m, int n, int k,  int nb, 
			double alpha, double* a, int lda, 
			double* b, int ldb, 
			double beta, double* c, int ldc, 
			int* m_a, int* n_a, 
			int* m_b, int* n_b, 
			int* m_c, int* n_c,
			MPI_Comm comm_row, 
			MPI_Comm comm_col, 
			double* work1, double* work2 );

void pdgemmT(int  m, int n, int k,      /* global matrix dimensions */ 
			 int  nb,                   /* panel width */
			 double alpha, double* a, int lda,
			 double* b, int ldb,
			 double beta, double* c, int ldc,
			 int* m_a, int* n_a,
			 int* m_b, int* n_b,
			 int* m_c, int* n_c, 
			 MPI_Comm comm_row, 
			 MPI_Comm comm_col,
			 double* work1, double* work2);

#endif
#endif
