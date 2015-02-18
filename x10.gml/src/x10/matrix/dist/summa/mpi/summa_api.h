#ifndef SUMMA_API_H
#define SUMMA_API_H

#ifdef MPI_COMMU

#include "mpi.h"

//========================================
// Interface to C program
//========================================
struct BlockMatrix
{
  // Global matrix information
  /* Matrix size */
  int total_row;
  int total_col;

  int* row_list;
  int* col_list;
  //---------------------------------
  // Local block information
  /* Local block sizes*/
  int local_row;
  int local_col;
  //---------------------------------
  double* data;
};

//=========================================

void summa_init();
void assemble_matrix(struct BlockMatrix* bm, double* matdata);

struct BlockMatrix* new_matrix(int m, int n);
struct BlockMatrix* new_matrix_rand(int m, int n);
void  free_matrix(struct BlockMatrix* bm);

//=========================================
void print_block(struct BlockMatrix* bm);
void print_matrix_summa(struct BlockMatrix* bm);

//=========================================
void summa_mult_c(struct BlockMatrix* A, 
				  struct BlockMatrix* B, int trans,
				  struct BlockMatrix* C,
				  double alpha, double beta);

//========================================
// Interface to X10
//========================================

void print_proc_info();
void set_panel_size(int);
void setup_grid_partition(int pid, int nr, int nc, int rowid, int colid);

void summa_mult_T(int* mat,   double* scal ,
				  int* Arows, int* Acols, double* Adata, 
				  int* Brows, double* Bdata,
				  int* Ccols, double* Cdata); /*C's rows partitioning == A's rows partitioning */
void summa_mult(int* mat,   double* scal ,
				int* Arows, int* Acols, double* Adata, 
				int* Brows, int* Bcols, double* Bdata, 
				double* Cdata);
#endif
#endif

