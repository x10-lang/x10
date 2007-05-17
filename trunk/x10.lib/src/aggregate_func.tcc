#include <x10/types.h>
#include <x10/aggregate.h>
#include <stdarg.h>

using namespace x10lib;

int search = 0;
async_arg_t argbuf[MAX_HANDLERS][MAX_TASKS][MAX_ARGS*AGG_LIMIT];
ulong counter[MAX_HANDLERS][MAX_TASKS];
int maxCounter[MAX_HANDLERS];
int total [MAX_HANDLERS];
async_arg_t  inbuf[MAX_ARGS*AGG_LIMIT];

template <int N, typename F>
inline void
batchAsyncDispatch (async_arg_t *a, ulong len) { __attribute__ ((__always_inline__))
  F f;
  for (int i = 0; i < len; i += N * sizeof(async_arg_t)) {
    f(a, N);
    a = a + N;
  }
}

template <int N, typename F>
void
asyncSpawnCompHandlerAgg (lapi_handle_t *handle, void* argbuf)
{
   batchAsyncDispatch<N, F> (inbuf, (ulong) argbuf);
   //batchAsyncDispatch<N, F> b;
   //b(inbuf, (ulong) argbuf);
}

template <int N, typename F>
inline error_t
x10lib::flush (async_handler_t handler)
{
    for (int j =0; j < MAX_PLACES; j++) {
      if ( j != here() && counter[handler][j] != 0) {
        LAPI_Amsend (GetHandle(),
                     j,
                     asyncSpawnHandlerAgg <N, F>,
                     NULL,
                     0,
                     (void*) argbuf[handler][j],
                     counter[handler][j] * N * sizeof(async_arg_t),
                     NULL,
                     NULL,
                     NULL);
      }
      total[handler] -= counter[handler][j];
      counter[handler][j] =0;
    }

     return X10_OK;
  //cout << "Search " << search << endl;
}

template <int N, typename F> 
void*
asyncSpawnHandlerAgg (lapi_handle_t handle, void* uhdr,
		   uint *uhdr_len, ulong* msg_len, 
		   compl_hndlr_t**  comp_h,
		   void** user_info)
{
  lapi_return_info_t *ret_info = (lapi_return_info_t *) msg_len;
  if (ret_info->udata_one_pkt_ptr) {
    batchAsyncDispatch<N, F> ((async_arg_t*)ret_info->udata_one_pkt_ptr, *msg_len);
    //batchAsyncDispatch<N, F> b;
    //b ((async_arg_t*)ret_info->udata_one_pkt_ptr, *msg_len);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {
    *comp_h = asyncSpawnCompHandlerAgg <N, F>;
    ret_info->ret_flags = LAPI_LOCAL_STATE; 
    *user_info = (void*) *msg_len;
    return inbuf; 
  }
}

#if (VARARGS==1)
template <int N, typename F>
inline error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, ...)
{
 va_list  list;
 
 va_start (list, handler);
 
 lapi_cntr_t origin_cntr;
 
 ulong& count = counter [handler][target];
 for (int i =0; i < N; i++)
   argbuf[handler][target][N*count+i] = va_arg (list, async_arg_t); 
 
 va_end (list);

  if (target == here()) {
     batchAsyncDispatch<N, F> (argbuf[handler][target], 1 * N * sizeof (async_arg_t));
     //batchAsyncDispatch<N, F> b;
     //b (argbuf[handler][target], 1 * N * sizeof(async_arg_t));
     return X10_OK;
  }
 
 count++;
 total[handler]++;

 if (total[handler] >= AGG_LIMIT)
  {
    ulong max = 0;
    int task = 0;
    for (place_t i = 0; i < MAX_TASKS; i++) {
      task = counter[handler][i] > max ? i :  task;
      max = counter[handler][i] > max ? counter[handler][i] : max; 
    }
 
    //cout << " Sending " << here() << " to " << task << " " << max << endl; 
    //cout << "size " << counter[handler][task] * MAX_ARGS * sizeof(async_arg_t) <<endl;
      LAPI_Amsend (GetHandle(),
                   task,
                   asyncSpawnHandlerAgg <N, F>,
                   NULL,
                   0,
                   (void*) argbuf[handler][task],
                   max * N * sizeof(async_arg_t),
                   NULL,
                   NULL,
                   NULL);
    total[handler] -= max;
    counter[handler][task] = 0;
  } 

  return X10_OK;
}
#else
template <int N, typename F>
inline error_t 
asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0)
{
  if (target == here()) {
     batchAsyncDispatch<N, F> (&arg0, 1 * N * sizeof (async_arg_t));
     //batchAsyncDispatch<N, F> b;
     //b (&arg0, 1 * N * sizeof(async_arg_t));
     return X10_OK;
  }
 
 ulong& count = counter [handler][target];
 argbuf[handler][target][count] = arg0;
 count++;
 total[handler]++;
 if (total[handler] >= AGG_LIMIT)
  {
    ulong max = 0;
    int task = 0;
    for (place_t i = 0; i < MAX_PLACES; i++) {
      //search++;
      if (counter[handler][i] > max ) {
         task = i; 
         max = counter[handler][i]; 
      }
    }
    //cout << " Sending " << here() << " to " << task << " " << max << endl; 
  
      LAPI_Amsend (GetHandle(),
                   task,
                   asyncSpawnHandlerAgg <N, F>,
                   NULL,
                   0,
                   (void*) argbuf[handler][task],
                   max * N * sizeof(async_arg_t),
                   NULL,
                   NULL,
                   NULL);
    total[handler] -= max;
    counter[handler][task] = 0;
  } 
  return X10_OK;
}
#endif

