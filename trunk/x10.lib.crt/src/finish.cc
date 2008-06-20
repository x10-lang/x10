#include <assert.h>

#include "rts_messaging.h"

#include "x10_internal.h"

#define X10_MAX_FINISH_ID 100

#define ID(frecord) frecord->finish_root * X10_MAX_FINISH_ID + frecord->finish_id

int __x10::FinishCounter = 1;

int** __x10::AsyncCounts = NULL;

int* __x10::AsyncSpawned = NULL;

X10::Err
X10::Acts::FinishChild(const x10_finish_record_t* frecord, void* ex_buf, int ex_buf_size)
{
  __x10::AsyncCounts[ID(frecord)][__x10::here]--; 
  
  if (__x10::IsPlaceQuiescent(frecord))
    {
      __x10::PropagateCredits(frecord);
    }  
  return X10_OK;
}

X10::Err
X10::Acts::FinishBegin(x10_finish_record_t* frecord, void* multi_ex_buf, int* ex_offsets, 
		       int max_ex_buf_size, int max_num_exceptions)
{
  frecord->finish_root = __x10::here;

  assert (__x10::FinishCounter < X10_MAX_FINISH_ID);

  frecord->finish_id = __x10::FinishCounter++;  
  return X10_OK;
}

X10::Err
X10::Acts::FinishBeginGlobal(x10_finish_record_t* frecord, void* multi_ex_buf, int* ex_offsets, 
			     int max_ex_buf_size, int max_num_exceptions)
{
  assert (__x10::here == 0);
  frecord->finish_root = 0;
  frecord->finish_id = 0;
  return X10_OK;
}

X10::Err
X10::Acts::FinishEnd(const x10_finish_record_t* frecord, int* num_exceptions)
{  
  int cnt = 0;

  do {
    
    cnt = 0;
    
    for (int i = 0; i < __x10::nplaces; i++)
      if (__x10::AsyncCounts[ID(frecord)][i] == 0)
	cnt++;
    
    x10_probe();
    
  }while (cnt != __x10::nplaces) ;
  
  //  __upcrt_distr_fence(0);  

  return X10_OK;
}

// C Bindings

EXTERN x10_err_t
x10_finish_begin(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions)
{
  return X10::Acts::FinishBegin(frecord, mult_ex_buf, ex_offsets, max_ex_buf_size, max_num_exceptions);
}

EXTERN x10_err_t
x10_finish_begin_global(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions)
{
  return X10::Acts::FinishBeginGlobal(frecord, mult_ex_buf, ex_offsets, max_ex_buf_size, max_num_exceptions);
}

EXTERN x10_err_t
x10_finish_end(const x10_finish_record_t* frecord, int* num_exceptions)
{
  return X10::Acts::FinishEnd(frecord, num_exceptions);
}

EXTERN x10_err_t
x10_finish_child(const x10_finish_record_t* frecord, void* ex_buf, int ex_buf_size)
{
  return X10::Acts::FinishChild(frecord, ex_buf, ex_buf_size);
}

// __x10

void
 __x10::FinishInit()
{
  int i, j;

  int max_finish_overall = X10_MAX_FINISH_ID * __x10::nplaces;

  __x10::AsyncSpawned = (int*) malloc(sizeof(int) * max_finish_overall);

  __x10::AsyncCounts = (int**) malloc(sizeof(int) * max_finish_overall);

  for (i = 0; i < max_finish_overall; ++i)
    __x10::AsyncCounts[i] = (int*) malloc(sizeof(int) * __x10::nplaces);
  
  for (i = 0; i < max_finish_overall; i++)
    __x10::AsyncSpawned[i] = 0;
  
  for (i = 0; i < max_finish_overall; ++i)
    for (j  = 0; j < __x10::nplaces; ++j)
      __x10::AsyncCounts[i][j] = 0;
}

void __x10::FinishComplHandler(void* arg)
{
  __x10::FinishComplMessage* tmp = (__x10::FinishComplMessage*) arg;
  int i;
  for (i = 0; i < tmp->num_tuples; i++)
    {
      __x10::AsyncCounts[tmp->finish_id + X10_MAX_FINISH_ID * __x10::here][tmp->tuples[i].place] += tmp->tuples[i].count;
    }  

  printf ("%d :\n", tmp->tuples[0].count);
}


__xlupc_local_addr_t __x10::FinishHandler(const __upcrt_AMHeader_t* header, 
				   __upcrt_AMComplHandler_t** comp_h, 
				   void** arg)  
{
  __x10::FinishMessage* message = (__x10::FinishMessage*) header;
  
  __x10::Tuple* tuples = (__x10::Tuple*) malloc(message->usize);
  __x10::FinishComplMessage* tmp = (__x10::FinishComplMessage*) malloc(sizeof(__x10::FinishComplMessage));
  tmp->tuples = tuples;
  tmp->num_tuples = message->usize / sizeof(__x10::Tuple);
  tmp->finish_id = message->finish_id;
  
  *comp_h = __x10::FinishComplHandler;
  
  *arg = (void*) tmp;
  
  return (__xlupc_local_addr_t) tuples;  
}

__x10::Tuple*
__x10::ConstructTuples (int* size, const x10_finish_record_t* frecord)
{
  __x10::Tuple* tuples = (__x10::Tuple*) malloc(sizeof(__x10::Tuple)* X10_MAX_FINISH_ID);
  int non_zeros= 0;
  int i;
  for (i = 0; i < __x10::nplaces; i++)
    if(__x10::AsyncCounts[ID(frecord)][i] != 0) {
      tuples[non_zeros].count = __x10::AsyncCounts[ID(frecord)][i] ;
      tuples[non_zeros].place = i;
      non_zeros++;
    }

  *size = non_zeros;

  return tuples;
}

int
__x10::IsPlaceQuiescent(const x10_finish_record_t* frecord)
{
  return --__x10::AsyncSpawned[ID(frecord)] == 0 && frecord->finish_root != __x10::here;
}

void
__x10::PropagateCredits(const x10_finish_record_t* frecord)
{
  int non_zeros;
  
  __x10::Tuple* tuples = __x10::ConstructTuples(&non_zeros, frecord);
  
  __x10::FinishMessage* header = (__x10::FinishMessage*) malloc(sizeof(__x10::FinishMessage));
  header->header = __x10::FinishHandler;
  header->headerlen = sizeof(*header);
  header->usize = non_zeros * sizeof(__x10::Tuple);
  header->finish_id = frecord->finish_id;
  
  void* req = __upcrt_distr_amsend_post (frecord->finish_root,
					 (__upcrt_AMHeader_t*) header,
					 (__xlupc_local_addr_t) tuples,
					 non_zeros*sizeof(__x10::Tuple));
  
  __x10::AsyncCounts[ID(frecord)][__x10::here] = 0;
  
  __upcrt_distr_wait (req);
  
  free(tuples);
}

void
__x10::FinishBookeepingOutgoing(const x10_finish_record_t* frecord, x10_place_t place)
{
  __x10::AsyncCounts[ID(frecord)][place]++;
}

void
__x10::FinishBookeepingIncoming(x10_finish_record_t* frecord)
{
  __x10::AsyncSpawned[ID(frecord)]++;
}
