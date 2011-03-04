#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <errno.h>
#include "mach_def.h"
#include "smp_node.h"
#include "types.h"

smp_barrier_t smp_barrier_init(int n_clients) {
  smp_barrier_t nbarrier = (smp_barrier_t)
    malloc(sizeof(struct smp_barrier));
  assert_malloc(nbarrier);
  
  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_barrier_destroy(smp_barrier_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier);
}

void smp_barrier_wait(smp_barrier_t nbarrier) {
  int my_phase;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
}

smp_reduce_i_t smp_reduce_init_i(int n_clients) {
  smp_reduce_i_t nbarrier = (smp_reduce_i_t)
    malloc(sizeof(struct smp_reduce_i_s));
  assert_malloc(nbarrier);
  
  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->sum       = 0;
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_reduce_destroy_i(smp_reduce_i_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier);
}

int smp_reduce_i(smp_reduce_i_t nbarrier, int val, reduce_t op) {
  int my_phase;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  if (nbarrier->n_waiting==0) {
    nbarrier->sum = val;
  }
  else {
    switch (op) {
    case MIN:  nbarrier->sum  = min(nbarrier->sum,val);  break;
    case MAX : nbarrier->sum  = max(nbarrier->sum,val);  break;
    case SUM : nbarrier->sum += val;  break;
    default  : perror("ERROR: smp_reduce_i() Bad reduction operator");
    }
  }
  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {
    nbarrier->result    = nbarrier->sum;
    nbarrier->sum       = 0;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result);
}

smp_reduce_l_t smp_reduce_init_l(int n_clients) {
  smp_reduce_l_t nbarrier = (smp_reduce_l_t)
    malloc(sizeof(struct smp_reduce_l_s));
  assert_malloc(nbarrier);

  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->sum       = 0;
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_reduce_destroy_l(smp_reduce_l_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier);
}

long smp_reduce_l(smp_reduce_l_t nbarrier, long val, reduce_t op) {
  int my_phase;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  if (nbarrier->n_waiting==0) {
    nbarrier->sum = val;
  }
  else {
    switch (op) {
    case MIN:  nbarrier->sum  = min(nbarrier->sum,val);  break;
    case MAX : nbarrier->sum  = max(nbarrier->sum,val);  break;
    case SUM : nbarrier->sum += val;  break;
    default  : perror("ERROR: smp_reduce_l() Bad reduction operator");
    }
  }
  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {
    nbarrier->result    = nbarrier->sum;
    nbarrier->sum       = 0;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result);
}

smp_reduce_d_t smp_reduce_init_d(int n_clients) {
  smp_reduce_d_t nbarrier = (smp_reduce_d_t)
    malloc(sizeof(struct smp_reduce_d_s));
  assert_malloc(nbarrier);

  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->sum       = 0.000001;
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_reduce_destroy_d(smp_reduce_d_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier);
}

double smp_reduce_d(smp_reduce_d_t nbarrier, double val, reduce_t op) {
  int my_phase;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  if (nbarrier->n_waiting==0) {
    nbarrier->sum = val;
  }
  else {
    switch (op) {
    case MIN:  nbarrier->sum  = min(nbarrier->sum,val);  break;
    case MAX : nbarrier->sum  = max(nbarrier->sum,val);  break;
    case SUM : nbarrier->sum += val;  break;
    default  : perror("ERROR: smp_reduce_i() Bad reduction operator");
    }
  }
  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {
    nbarrier->result    = nbarrier->sum;
    nbarrier->sum       = 0.0;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result);
}

smp_scan_i_t smp_scan_init_i(int n_clients) {
  smp_scan_i_t nbarrier = (smp_scan_i_t)
    malloc(sizeof(struct smp_scan_i_s));
  assert_malloc(nbarrier);

  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->result    = (int *)malloc(n_clients*sizeof(int));
    assert_malloc(nbarrier->result);
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_scan_destroy_i(smp_scan_i_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier->result);
  free(nbarrier);
}

int smp_scan_i(smp_scan_i_t nbarrier, int val, reduce_t op,int th_index) {
  int my_phase,i,temp;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  nbarrier->result[th_index]  = val;

  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) { /* get the prefix result in result array*/
    switch (op) {
    case MIN : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = min(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case MAX : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = max(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case SUM : 
      for(i = 1; i < nbarrier->n_clients;i++) 
         nbarrier->result[i] += nbarrier->result[i-1];
      break;
    default  : perror("ERROR: smp_scan_i() Bad reduction operator");
    }
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result[th_index]);
}

smp_scan_l_t smp_scan_init_l(int n_clients) {
  smp_scan_l_t nbarrier = (smp_scan_l_t)
    malloc(sizeof(struct smp_scan_l_s));
  assert_malloc(nbarrier);

  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->result    = (long *)malloc(n_clients*sizeof(long));
    assert_malloc(nbarrier->result);
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_scan_destroy_l(smp_scan_l_t nbarrier) {
  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier->result);
  free(nbarrier);
}

long smp_scan_l(smp_scan_l_t nbarrier, long val, reduce_t op, int th_index) {
  int my_phase,i;
  long temp;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  nbarrier->result[th_index] = val; 

  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {/*get the prefix*/
    switch (op) {
    case MIN : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = min(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case MAX : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = max(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case SUM : 
      for(i = 1; i < nbarrier->n_clients;i++) 
         nbarrier->result[i] += nbarrier->result[i-1];
      break;
    default  : perror("ERROR: smp_scan_i() Bad reduction operator");
    }
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result[th_index]);
}

smp_scan_d_t smp_scan_init_d(int n_clients) {
  smp_scan_d_t nbarrier = (smp_scan_d_t)
    malloc(sizeof(struct smp_scan_d_s));
  assert_malloc(nbarrier);

  if (nbarrier != NULL) {
    nbarrier->n_clients = n_clients;
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 0;
    nbarrier->result    = (double *)malloc(n_clients*sizeof(double));
    assert_malloc(nbarrier->result);
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&nbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&nbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
#if defined(PTHREAD_USE_D4)
    pthread_cond_init(&nbarrier->wait_cv, pthread_condattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_cond_init(&nbarrier->wait_cv, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(nbarrier);
}

void smp_scan_destroy_d(smp_scan_d_t nbarrier) {

  pthread_mutex_destroy(&nbarrier->lock);
  pthread_cond_destroy(&nbarrier->wait_cv);
  free(nbarrier->result);
  free(nbarrier);
}

double smp_scan_d(smp_scan_d_t nbarrier, double val, reduce_t op,int th_index) {
  int my_phase,i;
  double temp;

  pthread_mutex_lock(&nbarrier->lock);
  my_phase = nbarrier->phase;
  nbarrier->result[th_index] = val;
  nbarrier->n_waiting++;
  if (nbarrier->n_waiting == nbarrier->n_clients) {
    switch (op) {
    case MIN : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = min(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case MAX : temp = nbarrier->result[0];
      for(i = 1; i < nbarrier->n_clients;i++) {
         temp  = max(nbarrier->result[i],temp);
         nbarrier->result[i] = temp;
      }  
      break;
    case SUM : 
      for(i = 1; i < nbarrier->n_clients;i++) 
         nbarrier->result[i] += nbarrier->result[i-1];
      break;
    default  : perror("ERROR: smp_scan_i() Bad reduction operator");
    }
    nbarrier->n_waiting = 0;
    nbarrier->phase     = 1 - my_phase;
    pthread_cond_broadcast(&nbarrier->wait_cv);
  }
  while (nbarrier->phase == my_phase) {
    pthread_cond_wait(&nbarrier->wait_cv, &nbarrier->lock);
  }
  pthread_mutex_unlock(&nbarrier->lock);
  return(nbarrier->result[th_index]);
}



spin_barrier_t spin_barrier_init(int n_clients) {
  spin_barrier_t sbarrier = (spin_barrier_t)
    malloc(sizeof(struct spin_barrier));
  assert_malloc(sbarrier);

  if (sbarrier != NULL) {
    sbarrier->n_clients = n_clients;
    sbarrier->n_waiting = 0;
    sbarrier->phase     = 0;
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&sbarrier->lock, pthread_mutexattr_default);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_mutex_init(&sbarrier->lock, NULL);
#endif /* defined(PTHREAD_USE_D4) */
  }
  return(sbarrier);
}

void spin_barrier_destroy(spin_barrier_t sbarrier) {
  pthread_mutex_destroy(&sbarrier->lock);
  free(sbarrier);
}

void spin_barrier_wait(spin_barrier_t sbarrier) {
  int my_phase;

  while (pthread_mutex_trylock(&sbarrier->lock) == EBUSY) ;
  my_phase = sbarrier->phase;
  sbarrier->n_waiting++;
  if (sbarrier->n_waiting == sbarrier->n_clients) {
    sbarrier->n_waiting = 0;
    sbarrier->phase     = 1 - my_phase;
  }
  pthread_mutex_unlock(&sbarrier->lock);

  while (sbarrier->phase == my_phase) ;
}

