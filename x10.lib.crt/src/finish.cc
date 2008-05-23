#include <assert.h>

#include "rts_messaging.h"

#include "x10.h"

#define X10_MAX_FINISH_ID 100

#define X10_MAX_PLACES 1024

extern x10_place_t __x10_here;

extern unsigned int __x10_numplaces;

static int __x10_finish_counter = 1;

typedef struct
{
  __upcrt_AMHeaderHandler_t header; 
  int headerlen;
  int finish_id;
  int usize;
}finish_message_t;

typedef struct 
{
  int count;  
  int place;
}tuple;

typedef struct
{
  tuple* tuples;
  int num_tuples;
  int finish_id;
} finish_compl_message_t;

int __x10_async_counts[X10_MAX_FINISH_ID][X10_MAX_PLACES];

int __x10_async_spawned[X10_MAX_FINISH_ID];

static tuple* construct_tuples (int* size, int finish_id);

extern void
 __x10_finish_init ()
{
  int i, j;

  for (i = 0; i < X10_MAX_FINISH_ID; i++)
    __x10_async_spawned[i] = 0;

  for (i = 0; i < X10_MAX_FINISH_ID; ++i)
    for (j  = 0; j < X10_MAX_PLACES; ++j)
      __x10_async_counts[i][j] = 0;

}

static
void __x10_finish_compl_handler (void* arg)
{
  finish_compl_message_t* tmp = (finish_compl_message_t*) arg;
  int i;
  for (i = 0; i < tmp->num_tuples; i++)
    {
      __x10_async_counts[tmp->finish_id][tmp->tuples[i].place] += tmp->tuples[i].count;
    }  

  printf ("%d :\n", tmp->tuples[0].count);
}

static
__xlupc_local_addr_t __x10_finish_handler (const __upcrt_AMHeader_t* header, 
					   __upcrt_AMComplHandler_t** comp_h, 
					   void** arg)  
{
  finish_message_t* message = (finish_message_t*) header;
  
  tuple* tuples = (tuple*) malloc(message->usize);
  finish_compl_message_t* tmp = (finish_compl_message_t*) malloc(sizeof(finish_compl_message_t));
  tmp->tuples = tuples;
  tmp->num_tuples = message->usize / sizeof(tuple);
  tmp->finish_id = message->finish_id;
  
  *comp_h = __x10_finish_compl_handler;
  
  *arg = (void*) tmp;
  
  return (__xlupc_local_addr_t) tuples;  
}

tuple*
construct_tuples (int* size, int finish_id)
{
  tuple* tuples = (tuple*) malloc(sizeof(tuple)* X10_MAX_FINISH_ID);
  int non_zeros= 0;
  int i;
  for (i = 0; i < X10_MAX_PLACES; i++)
    if(__x10_async_counts[finish_id][i] != 0) {
      tuples[non_zeros].count = __x10_async_counts[finish_id][i] ;
      tuples[non_zeros].place = i;
      non_zeros++;
    }

  *size = non_zeros;

  return tuples;
}

int
__x10_is_place_quiescent(const x10_finish_record_t* frecord)
{
  return --__x10_async_spawned[frecord->finish_id] == 0 && frecord->finish_root != __x10_here;
}

void
__x10_propagate_credits(const x10_finish_record_t* frecord)
{
  int non_zeros;
  
  tuple* tuples = construct_tuples (&non_zeros, frecord->finish_id);
  
  finish_message_t* header = (finish_message_t*) malloc(sizeof(finish_message_t));
  header->header = __x10_finish_handler;
  header->headerlen = sizeof(*header);
  header->usize = non_zeros * sizeof(tuple);
  header->finish_id = frecord->finish_id;
  
  void* req = __upcrt_distr_amsend_post (frecord->finish_root,
					 (__upcrt_AMHeader_t*) header,
					 (__xlupc_local_addr_t) tuples,
					 non_zeros*sizeof(tuple));
  
  __x10_async_counts[frecord->finish_id][__x10_here] = 0;
  
  __upcrt_distr_wait (req);
  
  free(tuples);
}

x10_err_t
x10_finish_child (const x10_finish_record_t* frecord, void* ex_buf, int ex_buf_size)
{

  __x10_async_counts[frecord->finish_id][__x10_here]--;
  
  if (__x10_is_place_quiescent(frecord))
    {
      __x10_propagate_credits(frecord);
    }
  
  return X10_OK;
}

x10_err_t
x10_finish_begin(x10_finish_record_t* frecord, void* multi_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions)
{
  frecord->finish_root = __x10_here;
  frecord->finish_id = __x10_finish_counter++;

  return X10_OK;
}

x10_err_t
x10_finish_begin_global (x10_finish_record_t* frecord, void* multi_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions)
{
  assert (__x10_here == 0);
  frecord->finish_root = 0;
  frecord->finish_id = 0;

  return X10_OK;
}

x10_err_t
x10_finish_end(const x10_finish_record_t* frecord, int* num_exceptions)
{  
  int cnt = 0;
  do {
    cnt = 0;
    int i;
    for (i = 0; i < X10_MAX_PLACES; i++)
      if (__x10_async_counts[frecord->finish_id][i] == 0)
	cnt++;
    x10_probe();
  }while (cnt != X10_MAX_PLACES) ;
  
  __upcrt_distr_fence(0);
  
  return X10_OK;
}

void
__x10_finish_bookeeping_outgoing (const x10_finish_record_t* frecord, x10_place_t place)
{
  __x10_async_counts[frecord->finish_id][place]++;
}

void
__x10_finish_bookeeping_incoming (x10_finish_record_t* frecord)
{
  __x10_async_spawned[frecord->finish_id]++;
}
