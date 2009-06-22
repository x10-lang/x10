#ifndef _SMP_NODE_H
#define _SMP_NODE_H

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "types.h"

typedef struct smp_barrier {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  pthread_cond_t wait_cv;
} *smp_barrier_t;

smp_barrier_t smp_barrier_init(int n_clients);
void          smp_barrier_destroy(smp_barrier_t nbarrier);
void          smp_barrier_wait(smp_barrier_t nbarrier);

typedef struct smp_reduce_i_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  int sum;
  int result;
  pthread_cond_t wait_cv;
} *smp_reduce_i_t;

smp_reduce_i_t smp_reduce_init_i(int n_clients);
void           smp_reduce_destroy_i(smp_reduce_i_t nbarrier);
int            smp_reduce_i(smp_reduce_i_t nbarrier, int val, reduce_t op);

typedef struct smp_reduce_l_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  long sum;
  long result;
  pthread_cond_t wait_cv;
} *smp_reduce_l_t;

smp_reduce_l_t smp_reduce_init_l(int n_clients);
void           smp_reduce_destroy_l(smp_reduce_l_t nbarrier);
long           smp_reduce_l(smp_reduce_l_t nbarrier, long val, reduce_t op);

typedef struct smp_reduce_d_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  double sum;
  double result;
  pthread_cond_t wait_cv;
} *smp_reduce_d_t;

smp_reduce_d_t smp_reduce_init_d(int n_clients);
void           smp_reduce_destroy_d(smp_reduce_d_t nbarrier);
double         smp_reduce_d(smp_reduce_d_t nbarrier, double val, reduce_t op);

typedef struct smp_scan_i_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  int *result;
  pthread_cond_t wait_cv;
} *smp_scan_i_t;

smp_scan_i_t   smp_scan_init_i(int n_clients);
void           smp_scan_destroy_i(smp_scan_i_t nbarrier);
int            smp_scan_i(smp_scan_i_t nbarrier, int val, reduce_t op,int th_index);


typedef struct smp_scan_l_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  long *result;
  pthread_cond_t wait_cv;
} *smp_scan_l_t;

smp_scan_l_t   smp_scan_init_l(int n_clients);
void           smp_scan_destroy_l(smp_scan_l_t nbarrier);
long           smp_scan_l(smp_scan_l_t nbarrier, long val, reduce_t op,int th_index);

typedef struct smp_scan_d_s {
  pthread_mutex_t lock;
  int n_clients;
  int n_waiting;
  int phase;
  double *result;
  pthread_cond_t wait_cv;
} *smp_scan_d_t;

smp_scan_d_t   smp_scan_init_d(int n_clients);
void           smp_scan_destroy_d(smp_scan_d_t nbarrier);
double         smp_scan_d(smp_scan_d_t nbarrier, double val, reduce_t op,int th_index);

typedef struct spin_barrier {
  int n_clients;
  pthread_mutex_t lock;
  int n_waiting;
  int phase;
} *spin_barrier_t;

spin_barrier_t spin_barrier_init(int n_clients);
void           spin_barrier_destroy(spin_barrier_t sbarrier);
void           spin_barrier_wait(spin_barrier_t sbarrier);

#endif
