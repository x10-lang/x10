#ifdef MPI_COMMU
#include "summa_api.h"

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <math.h>
#include <string.h>

#include "summa.h"

int total_proc_num;
int myid;
int row_block; // Number of blocks row-wise
int col_block; // Number of block column-wise
int row_id; // local block index row-wise
int col_id; // local block index column-wise
int panel_size = 1;
char hostname[128];

MPI_Comm comm_row;
MPI_Comm comm_col;

void summa_init() {
    int rb;
    int namelen;

    MPI_Comm_size(MPI_COMM_WORLD, &total_proc_num);
    MPI_Comm_rank(MPI_COMM_WORLD, &myid);
    MPI_Get_processor_name(hostname, &namelen);

    if (total_proc_num <= 3) {
        row_block =1;
    } else {
        rb = (int) sqrt((float) total_proc_num);
        while (total_proc_num % rb) rb--;
        if (rb == 0) rb = 1;
        row_block =rb;
    }

    col_block = total_proc_num /row_block;
    // Partition communicator for colomn
    row_id = myid%row_block;
    col_id = myid/row_block;
    MPI_Comm_split(MPI_COMM_WORLD, row_id, col_id, &comm_row);
    MPI_Comm_split(MPI_COMM_WORLD, col_id, row_id, &comm_col);

    printf("P%i: Total proc: %i partitioned into %ix%i blocks\n",
            myid, total_proc_num, row_block, col_block);
    fflush(stdout);
}

//================================================================
int* partition_matrix(int total_n, int block_n, int* bdim) {
    int i;
    int k = total_n % block_n;
    int bs = total_n / block_n;

    for (i = 0; i < block_n; i++) {
        if (k) {
            bdim[i] = bs + 1;
            k--;
        } else {
            bdim[i] = bs;
        }
    }
    return bdim;
}

//-------------------------------------------
void set_matrix_dim(struct BlockMatrix* bm) {
    bm->row_list = (int*) malloc(sizeof(int) * row_block);
    partition_matrix(bm->total_row, row_block, bm->row_list);

    bm->col_list = (int*) malloc(sizeof(int) * col_block);
    partition_matrix(bm->total_col, col_block, bm->col_list);

    bm->local_row = (bm->row_list)[row_id];
    bm->local_col = (bm->col_list)[col_id];
}

void set_matrix_data(struct BlockMatrix* bm) {
    int c, r;
    int idx=0;

    bm->data = (double*) malloc(sizeof(double) * bm->local_col * bm->local_row);
    for (c = 0; c < bm->local_col; c++) {
        for (r = 0; r < bm->local_row; r++) {
            bm->data[idx++] = c+r;
        }
    }
}

void set_matrix_data_rand(struct BlockMatrix* bm) {
    int c, r;
    int idx = 0;
    double max = 100.0;
    double dval = 0.0;
    static double seed = 0.0;
    srand((int) seed+myid);
    seed = rand();

    bm->data = (double*) malloc(sizeof(double) * bm->local_col * bm->local_row);
    for (c = 0; c < bm->local_col; c++) {
        for (r = 0; r < bm->local_row; r++) {
            dval = max * ((double)rand()) / RAND_MAX;
            bm->data[idx++] = dval;
        }
    }
}

struct BlockMatrix* new_matrix(int m, int n) {
    struct BlockMatrix* bm = (struct BlockMatrix*) malloc(sizeof(struct BlockMatrix));
    bm->total_row =m;
    bm->total_col =n;
    set_matrix_dim(bm);
    set_matrix_data(bm);
    return bm;
}

struct BlockMatrix* new_matrix_rand(int m, int n) {
    struct BlockMatrix* bm = (struct BlockMatrix*) malloc(sizeof(struct BlockMatrix));
    bm->total_row =m;
    bm->total_col =n;
    set_matrix_dim(bm);
    set_matrix_data_rand(bm);
    return bm;
}

void free_matrix(struct BlockMatrix* bm) {
    if (bm->data) free(bm->data);
    if (bm->row_list) free(bm->row_list);
    if (bm->col_list) free(bm->col_list);
    free(bm);
}

void print_block(struct BlockMatrix* bm) {
    int i, r, c;
    for (i = 0; i < total_proc_num; i++) {
        if (myid == i) {
            printf("P%i: Matrix block [%i %i] size %ix%i\n",
                    myid, row_id, col_id, bm->local_row, bm->local_col);
            for (r = 0; r < bm->local_row; r++) {
                for (c = 0; c < bm->local_col; c++) {
                    printf("%.2f ", bm->data[c*bm->local_row+r]);
                }
                printf("\n");
            }
            printf("------------------\n");
        }
        fflush(stdout);
        MPI_Barrier(MPI_COMM_WORLD);
    }
}

void assemble_matrix(struct BlockMatrix* bm, double* matdata) {
    int t, br, bc, coldisp, rowdisp, maxcol;
    int* cntlist = NULL;
    int* displs = NULL;
    double* sendbuf = NULL;
    int root = 0;

    if (myid == root) {
        cntlist = (int*) malloc(sizeof(int) * row_block*col_block);
        displs = (int*) malloc(sizeof(int) * row_block*col_block);
        coldisp = 0;
        for (bc = 0; bc < col_block; bc++) {
            rowdisp = coldisp;
            for (br = 0; br < row_block; br++) {
                int brow = bm->row_list[br];
                cntlist[bc*row_block+br] = brow;
                displs[bc*row_block+br] = rowdisp;
                rowdisp += brow;
            }
            coldisp += bm->col_list[bc] * bm->total_row;
        }
    }

    sendbuf = bm->data;
    maxcol = bm->col_list[0]; //pick the block with the largest columns
    for (t = 0; t < maxcol; t++) {
        //reset 0 if the local block do not have so many columns
        int sendcnt = (t >= bm->local_col) ? 0 : bm->local_row;

        MPI_Gatherv(sendbuf, sendcnt, MPI_DOUBLE,
                matdata, cntlist, displs, MPI_DOUBLE,
                root, MPI_COMM_WORLD);
        sendbuf += bm->local_row; // set the location for next gather
        if (myid == root) { // reset the root receiving info
            for (bc = 0; bc < col_block; bc++) {
                for (br = 0; br < row_block; br++) {
                    if (t+1 >= bm->col_list[bc]) {
                        cntlist[bc*row_block+br] =0; //reset the cnt to 0, not sending data
                    } else {
                        displs[bc*row_block+br] += bm->total_row; //reset offset
                    }
                }
            }
        }
    }
    if (myid == root) {
        if (cntlist) free(cntlist);
        if (displs) free(displs);
    }
}

void print_matrix_summa(struct BlockMatrix* bm) {
    double *d = NULL;
    int r, c;
    int m = bm->total_row;
    int n = bm->total_col;

    //print_block(bm);
    if (myid == 0)
    d = (double*) malloc(sizeof(double) * m * n);

    assemble_matrix(bm, d);

    if (myid == 0) {
        printf("P%i Matrix %i x %i\n", myid, m, n);
        for (r = 0; r < m; r++) {
            for (c = 0; c < n; c++)
            printf("%.2f ", d[c*m+r]);
            printf("\n");
        }
        printf("======================\n");
        fflush(stdout);
        free(d);
    }
    MPI_Barrier(MPI_COMM_WORLD);
}

void summa_mult_c(struct BlockMatrix* A,
        struct BlockMatrix* B, int trans,
        struct BlockMatrix* C,
        double alpha, double beta) {
    double* work1;
    double* work2;

    work1 = (double*) malloc(sizeof(double)*(A->local_row)*panel_size);
    work2 = (double*) malloc(sizeof(double)*(B->local_col)*panel_size);

    if (trans) {
        pdgemmT(//int m, int n, int k, int nb,
                A->total_row, B->total_col, C->total_col, panel_size, // K is not common part of A and B
                //double alpha, double* a, int lda,
                alpha, A->data, A->local_row,
                //double* b, int ldb,
                B->data, B->local_row,
                //double beta, double* c, int ldc,
                beta, C->data, C->local_row,
                //int* m_a, int* n_a,
                A->row_list, A->col_list,
                //int* m_b, int* n_b,
                B->row_list, B->col_list,
                //int* m_c, int* n_c,
                C->row_list, C->col_list,
                //MPI_Comm comm_row, MPI_Comm comm_col,
                comm_row, comm_col,
                //double* work1, double* work2
                work1, work2);
    } else {
        pdgemm(//int m, int n, int k,  int nb,
                A->total_row, B->total_col, A->total_col, panel_size,
                //double alpha, double* a, int lda,
                alpha, A->data, A->local_row,
                //double* b, int ldb,
                B->data, B->local_row,
                //double beta, double* c, int ldc,
                beta, C->data, C->local_row,
                //int* m_a, int* n_a,
                A->row_list, A->col_list,
                //int* m_b, int* n_b,
                B->row_list, B->col_list,
                //int* m_c, int* n_c,
                C->row_list, C->col_list,
                //MPI_Comm comm_row, MPI_Comm comm_col,
                comm_row, comm_col,
                //double* work1, double* work2
                work1, work2);
    }
    free(work2);
    free(work1);
}

//=================================================================
// X10 interface
//=================================================================

void print_proc_info() {
    int pid;
    int nlen;

    MPI_Comm_size(MPI_COMM_WORLD, &total_proc_num);
    MPI_Comm_rank(MPI_COMM_WORLD, &pid);
    MPI_Get_processor_name(hostname, &nlen);
    printf("P%i %s is ready of %i proc\n", pid, hostname, total_proc_num);
    fflush(stdout);
}

void set_panel_size(int s) {
    panel_size = s;
}

void setup_grid_partition(int pid, int nr, int nc, int rowid, int colid) {
    myid = pid;
    row_block = nr;
    col_block = nc;
    row_id = rowid;
    col_id = colid;
    MPI_Comm_split(MPI_COMM_WORLD, rowid, colid, &comm_row);
    MPI_Comm_split(MPI_COMM_WORLD, colid, rowid, &comm_col);
}

void summa_mult_T(int* mat, double* scal,
        int* Arows, int* Acols, double* Adata,
        int* Brows, double* Bdata, /* Bcn_c [icurcol]-jjols == Acols */
        int* Ccols, double* Cdata) { /* Crows == Arows */
    int M = mat[0];
    int N = mat[1];
    int K = mat[2];
    int ps= mat[3];
    double alpha = scal[0];
    double beta = scal[1];

    int* Bcols = Acols;
    int* Crows = Arows;

    int lda = Arows[row_id];
    int ldb = Brows[row_id];
    int ldc = Crows[row_id];
    int sM = lda;
    int sN = Bcols[col_id];
    double* work1;
    double* work2;

    panel_size = ps;
    work1 = new double[sM*panel_size];
    work2 = new double[panel_size*sN];

    pdgemmT(//int m, int n, int k, int nb,
            M, N, K, panel_size, // K is not common part of A and B, instead it is C's coloumn
            //double alpha, double* a, int lda,
            alpha, Adata, lda,
            //double* b, int ldb,
            Bdata, ldb,
            //double beta, double* c, int ldc,
            beta, Cdata, ldc,
            //int* m_a, int* n_a,
            Arows, Acols,
            //int* m_b, int* n_b,
            Brows, Bcols, // Acols == Bcols,
            //int* m_c, int* n_c,
            Crows, /* The partitioning on C's rows is same as A's rows */
            Ccols, /* The partitioning on C's columns is NOT same as B's rows, though C's columns==B's rows */
            //MPI_Comm comm_row, MPI_Comm comm_col,
            comm_row, comm_col,
            //double* work1, double* work2
            work1, work2);
    delete work2;
    delete work1;
}

void summa_mult(int* mat, double* scal ,
        int* Arows, int* Acols, double* Adata,
        int* Brows, int* Bcols, double* Bdata,
        double* Cdata) {
    int M = mat[0];
    int N = mat[1];
    int K = mat[2];
    int ps= mat[3];
    double alpha = scal[0];
    double beta = scal[1];
    int lda = Arows[row_id];
    int ldb = Brows[row_id];
    int ldc = Arows[row_id];
    int sM = lda;
    int sN = Bcols[col_id];
    double* work1;
    double* work2;

    panel_size = ps;

    work1 = new double[sM*panel_size];
    work2 = new double[panel_size*sN];

    pdgemm(//int m, int n, int k, int nb,
            M, N, K, panel_size,
            //double alpha, double* a, int lda,
            alpha, Adata, lda,
            //double* b, int ldb,
            Bdata, ldb,
            //double beta, double* c, int ldc,
            beta, Cdata, ldc,
            //int* m_a, int* n_a,
            Arows, Acols,
            //int* m_b, int* n_b,
            Brows, Bcols,
            //int* m_c, int* n_c,
            Arows, Bcols,
            //MPI_Comm comm_row, MPI_Comm comm_col,
            comm_row, comm_col,
            //double* work1, double* work2
            work1, work2);

    delete work2;
    delete work1;
}

#endif
