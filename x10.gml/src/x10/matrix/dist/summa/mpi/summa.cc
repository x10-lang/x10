#ifdef MPI_COMMU
#include "summa.h"

#include "mpi.h"

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <assert.h>

#define DELTA 0.00000001
/* macro for column major indexing */
#define A( i,j ) (a [j*lda +   i ])
#define B( i,j ) (b [j*ldb +   i ])
#define C( i,j ) (c [j*ldc +   i ])

#define ZERO(n) ((fabs(n)<DELTA)?1:0)

#define min( x, y ) ( (x) < (y) ? (x) : (y) )

extern "C" {
  //void dlacpy_(char*, int*, int*, double*, int*, double*, int*);
  //void daxpy_(int*, double*, double*, int*, double*, int* );
  void dgemm_(char*, char*, int*, int*, int*,
              double*, double*, int*, double*, int*, double*, double*, int*);
}
//==========================================================================

int     i_one=1;               /* used for constant passed to blas call */
double  d_one=1.0, d_zero=0.0; /* used for constant passed to blas call */
double  blas_usetime = 0.0;
double  comm_usetime = 0.0;
//==========================================================================
void RING_Bcast(double *buf, int count, MPI_Datatype type, int root, MPI_Comm comm );
void RING_Sum(double *buf, int count, MPI_Datatype type, 
              int root, MPI_Comm comm, double *work);
//==========================================================================
void get_usetime(double* t) {
  t[0] = blas_usetime * 1000; //change to millisecond
  t[1] = comm_usetime * 1000; //Change to milliscond
  //printf("Summa printing out time %f %f\n", blas_usetime, comm_usetime);
  //fflush(stdout);
} // time unit second
void reset_usetime() {
  blas_usetime = 0.0;
  comm_usetime = 0.0;
}

void dla_copy(int m, int n, double* inputA, int lda, double* outputB, int ldb) {
    int b=0; int c, r;
    int stcolA=0;
    int stcolB=0;
    for (c=0; c<n; c++, stcolA+=lda, stcolB+=ldb) {
        b = stcolB;
        for (r=stcolA; r<stcolA+m; r++, b++) {
            outputB[b]=inputA[r];
        }
    }
}

void dax_copy(int n, double alpha, double* x, int incx, double* y, int incy) {
    int i;
    int idx_x = 0;
    int idx_y = 0;
    double s=0.0;
    for (i=0; i<n; i++, idx_x+=incx, idx_y+=incy) {
        s = y[idx_y];
        y[idx_y] = s+ alpha * x[idx_x];
    }
}

void pdgemm( int  m, int n, int k,      /* global matrix dimensions */ 
             int  nb,                   /* panel width */
             double alpha, double* a, int lda,
             double* b, int ldb,
             double beta, double* c, int ldc,
             int* m_a, int* n_a,
             int* m_b, int* n_b,
             int* m_c, int* n_c, 
             MPI_Comm comm_row, MPI_Comm comm_col,
             double* work1, double* work2) {
  //-----------------------------------
  int myrow, mycol;     /* my row and column index */
  int nprow, npcol;      /* number of node rows and columns */
  int i, j, kk, iwrk;    /* misc. index variables */
  int icurrow, icurcol; /* index of row and column that hold current
                           row and column, resp., for rank-1 update*/
  int ii, jj;           /* local index (on icurrow and icurcol, resp.) 
                            of row and column for rank-1 update  */
  double *p;            /* get myrow, mycol  */
  //-----------------------------------
  double ut = 0.0;

  reset_usetime();
  //----------------------------------
  MPI_Comm_rank( comm_row, &mycol ); 
  MPI_Comm_rank( comm_col, &myrow ); 
  //-----------------------------------
  /* scale local block of C */
  if (ZERO(beta)) {
      for ( j=0; j<n_c[ mycol ]; j++)
        for ( i=0; i<m_c[ myrow ]; i++)
          C( i,j ) = 0.0;
  } else if (ZERO(beta - 1.0) == 0) {
      for ( j=0; j<n_c[ mycol ]; j++)
        for ( i=0; i<m_c[ myrow ]; i++)
          C( i,j ) = beta * C( i,j );
  }

  icurrow = 0;
  ii = 0;
  jj = 0;
  icurcol = 0;  
  //printf("Start SUMMA iteration\n"); fflush(stdout);

  for (kk=0; kk<k; kk+=iwrk) {
      char info[] = "General";
      char tran[] = "No transpose";
      //printf("P(%i,%i) Starting iteration %i\n", myrow, mycol, kk); fflush(stdout);

      iwrk = min(nb,   m_b[icurrow]-ii );
      iwrk = min(iwrk, n_a[icurcol]-jj );
      /* pack current iwrk columns of A into work1 */
       //printf("P(%i, %i) starting copy to local work1 %i\n", myrow, mycol, kk); fflush(stdout);
      if (mycol == icurcol ) {
          dla_copy(m_a[myrow], iwrk, &A( 0, jj ), lda, work1, m_a[myrow] );
          //dlacpy_(info, &m_a[myrow], &iwrk, &A( 0, jj ), &lda, work1, &m_a[myrow] );
      }
      /* pack current iwrk rows of B into work2  */
       //printf("P(%i, %i) starting copy to local work2 %i\n", myrow, mycol, kk); fflush(stdout);
      if (myrow == icurrow ) {
          dla_copy(iwrk, n_b[mycol], &B( ii, 0 ), ldb, work2, iwrk );
          //dlacpy_(info, &iwrk, &n_b[mycol], &B( ii, 0 ), &ldb, work2, &iwrk );
      }
      /* broadcast work1 and work2  */
      //MPI_Barrier(MPI_COMM_WORLD);
       //printf("P(%i, %i) starting bcast %i\n", myrow, mycol, kk); fflush(stdout);
      ut = MPI_Wtime();
      RING_Bcast(work1, m_a[myrow]*iwrk, MPI_DOUBLE, icurcol, comm_row );
      RING_Bcast(work2, n_b[mycol]*iwrk, MPI_DOUBLE, icurrow, comm_col );
      //MPI_Barrier(MPI_COMM_WORLD);
      comm_usetime += MPI_Wtime() - ut;

      /* update local block */
       //printf("P(%i, %i) starting blas %i\n", myrow, mycol, kk); fflush(stdout);
      ut = MPI_Wtime();
      dgemm_( tran, tran, &m_c[myrow], &n_c[mycol], &iwrk,
              &alpha, work1, &m_a[myrow], //Summa paper error: &m_b[myrow]
              work2, &iwrk, &d_one,
              c, &ldc );
      // SUMMA paper error
      blas_usetime += MPI_Wtime() - ut;
       //printf("P(%i, %i) done blas %i\n", myrow, mycol, kk); fflush(stdout);
      /* update icurcol, icurrow, ii, jj */
      ii += iwrk;
      jj += iwrk;
      if ( jj>=n_a[ icurcol ] ) { icurcol++; jj = 0; };
      if ( ii>=m_b[ icurrow ] ) { icurrow++; ii = 0; };
    }
  //printf("P(%i, %i) done SUMMA\n", myrow, mycol, kk); fflush(stdout);
}

void RING_Bcast(double *buf, int count, MPI_Datatype type, int root, MPI_Comm comm) {
  int me, np;
  int src, dst;
  MPI_Status status;
  MPI_Comm_rank(comm, &me );
  MPI_Comm_size(comm, &np );

  src = (me-1+np)%np;
  dst = (me+1)%np;
  if (me != root) {
    //printf("Place %i recv from %i size %i\n", me, src, count);fflush(stdout);
    MPI_Recv( buf, count, type, src, src /*MPI_ANY_TAG*/, comm, &status );
  }
  if (( me+1 )%np != root) {
    //printf("Place %i send to %i size %i\n", me, dst, count); fflush(stdout);
    MPI_Send( buf, count, type, dst, me, comm );
  }
  //printf("Place %i Done commu\n", me); fflush(stdout);
}

void pdgemmT(int  m, int n, int k,      /* global matrix dimensions */ 
             int  nb,                   /* panel width */
             double alpha, double* a, int lda,
             double* b, int ldb,
             double beta, double* c, int ldc,
             int* m_a, int* n_a,
             int* m_b, int* n_b,
             int* m_c, int* n_c, 
             MPI_Comm comm_row, MPI_Comm comm_col,
             double* work1, double* work2) {
  //-----------------------------------
  int myrow, mycol;     /* my row and column index */
  int nprow, npcol;      /* number of node rows and columns */
  int i, j, kk, iwrk;    /* misc. index variables */
  int icurrow, icurcol; /* index of row and column that hold current
                           row and column, resp., for rank-1 update*/
  int ii, jj;           /* local index (on icurrow and icurcol, resp.) 
                            of row and column for rank-1 update  */
  double *temp;         /* temporary pointer used in pdgemm_abt */
  double *p;            /* get myrow, mycol  */
  //-----------------------------------
  double ut;

  reset_usetime();
  MPI_Comm_rank( comm_row, &mycol ); 
  MPI_Comm_rank( comm_col, &myrow ); 

  /* scale local block of C */
  if (ZERO(beta)) {
      for ( j=0; j<n_c[ mycol ]; j++ )
        for ( i=0; i<m_c[ myrow ]; i++ )
          C( i,j ) = 0.0;
  } else if (ZERO(beta - 1.0) == 0 ) {
      for ( j=0; j<n_c[ mycol ]; j++ )
        for ( i=0; i<m_c[ myrow ]; i++ )
          C( i,j ) = beta * C( i,j );
  }

  icurrow = 0;
  ii = jj = 0;
  icurcol = 0;
  /* malloc temp space for summation   */
  temp = new double[m_c[myrow]*nb];
  /* loop over all column panels of C */
  for (kk=0; kk<k; kk+=iwrk) {
      char info[] = "General";
      char tranA[] = "No transpose";
      char tranB[] = "Transpose";
      iwrk = min( nb,   m_b [icurrow]-ii );
      iwrk = min( iwrk, n_c [icurcol]-jj );

      /* pack current iwrk rows of B into work2 */
      if ( myrow == icurrow )
        dla_copy(iwrk, n_b[mycol], &B(ii, 0), ldb, work2, iwrk);
        //dlacpy_(info, &iwrk, &n_b[mycol], &B(ii, 0), &ldb, work2, &iwrk);
      /* broadcast work2  */
      ut = MPI_Wtime();
      RING_Bcast(work2, n_b[mycol]*iwrk, MPI_DOUBLE, icurrow, comm_col);
      comm_usetime += MPI_Wtime() - ut;

      /* Multiply local block of A times incoming rows of B */
      ut = MPI_Wtime();
      dgemm_(tranA, tranB, &m_c[myrow], &iwrk, &n_a[mycol],
              &alpha, a, &lda,
              work2, &iwrk,
              &d_zero, work1, &m_c[myrow]);
      blas_usetime += MPI_Wtime() - ut;

      //printf("P %i %i Mult Work1 %i %i [%.2f]\n", myrow, mycol, m_a[myrow], iwrk, work1[0]); fflush(stdout);

      ut = MPI_Wtime();
      /* Sum to node that holds current columns of C */
      RING_Sum(work1, m_c[myrow]*iwrk, MPI_DOUBLE, icurcol, comm_row, temp);
      comm_usetime += MPI_Wtime() - ut;
      //printf("P %i %i Sum Work1 %i %i [%.2f]\n", myrow, mycol, m_a[myrow], iwrk, work1[0]); fflush(stdout);
      /* Add to current columns of C*/
      if ( mycol == icurcol ) {
          //printf("Place(%i,%i) update C\n", myrow, mycol); fflush(stdout);
          p = work1;
          for (j=jj; j<jj+iwrk; j++, p+=m_c[myrow]) {
              //daxpy_(&m_c[myrow], &d_one, p, &i_one, &C(0,j), &i_one );
              dax_copy(m_c[myrow], d_one, p, i_one, &C(0,j), i_one );
              //printf("Place(%i,%i) C(0,%i)=[%.2f]\n", myrow, mycol, j, C(0,j)); fflush(stdout);
          }
      }
      /* update icurcol, icurrow, ii, jj*/
      ii += iwrk;
      jj += iwrk;
      if ( jj>=n_c[icurcol] ) { icurcol++; jj = 0; };
      if ( ii>=m_b[icurrow] ) { icurrow++; ii = 0; };
    }
  delete temp;
}

void RING_Sum( double *buf, int count, MPI_Datatype type, int root,
               MPI_Comm comm, double *work ) {
  int me, np;
  MPI_Status status;
  MPI_Comm_size(comm, &np );
  MPI_Comm_rank(comm, &me );
  if ( me != (root+1)%np ) {
      MPI_Recv(work, count, type, (me-1+np)%np, MPI_ANY_TAG, comm, &status);
      dax_copy(count, d_one, work, i_one, buf, i_one);
      //daxpy_(&count, &d_one, work, &i_one, buf, &i_one);
  }
  if (me != root)
    MPI_Send(buf, count, type, (me+1)%np, 0, comm);
}
#endif
