#include <x10/aggregate.h>
#include <stdarg.h>

using namespace x10lib;

async_arg_t argbuf[MAX_HANDLERS][MAX_TASKS][MAX_ARGS*AGG_LIMIT];
ulong counter[MAX_HANDLERS][MAX_TASKS];
int maxCounter[MAX_HANDLERS];
int total [MAX_HANDLERS];


inline void
batchAsyncDispatch (async_arg_t *a, ulong len, async_handler_t handle, int N) { 
   int niter = len / (N * sizeof(async_arg_t));  
   asyncSwitch (handle, a, niter);
}

struct comp 
{
  ulong len;
  int N;
  int handler; 
  void* buf;
};

struct header
{
  int N;
  int handler;
};

void
asyncSpawnCompHandlerAgg (lapi_handle_t *handle, void* a)
{
  comp* c = (comp*) a;	
  batchAsyncDispatch ((async_arg_t*) (c->buf), c->len, c->handler, c->N);
  delete[] c->buf;
  delete c;
}

error_t
x10lib::asyncFlush (async_handler_t handler, int N)
{
  lapi_cntr_t cntr;
  int tmp;
  header buf;
  buf.handler = handler;
  buf.N = N;
  for (int j =0; j < MAX_PLACES; j++) {
    if ( j!= here() && counter[handler][j] != 0) {
      LAPI_Setcntr (GetHandle(), &cntr, 0);
      LAPI_Amsend (GetHandle(),
		   j,
                   (void*) 1,
		   &buf,
		   sizeof(header),
		   (void*) argbuf[handler][j],
		   counter[handler][j] * N * sizeof(async_arg_t),
		   NULL,
		   &cntr, //NULL,
		   NULL);
    LAPI_Waitcntr (GetHandle(), &cntr, 1, &tmp);
    }
    total[handler] -= counter[handler][j];
    counter[handler][j] =0;
  }

  return X10_OK;
}

 
void*
asyncSpawnHandlerAgg (lapi_handle_t handle, void* uhdr,
		   uint *uhdr_len, ulong* msg_len, 
		      compl_hndlr_t**  comp_h,
		      void** user_info)
{
  struct header buf = *((header*) uhdr);
  lapi_return_info_t *ret_info = (lapi_return_info_t *) msg_len;
  if (ret_info->udata_one_pkt_ptr) {
    batchAsyncDispatch ((async_arg_t*)ret_info->udata_one_pkt_ptr, *msg_len, buf.handler, buf.N);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {
     comp* c = new comp;
     c->len =*msg_len;
     c->handler = buf.handler;
     c->N = buf.N;
     c->buf = (void*) new char[*msg_len];
    *comp_h = asyncSpawnCompHandlerAgg ;
    ret_info->ret_flags = LAPI_LOCAL_STATE; 
    *user_info = (void*) c;    
    return c->buf;
  }

}
error_t
x10lib::asyncRegisterAgg()
{
  LAPI_Addr_set (GetHandle(), (void*) asyncSpawnHandlerAgg, 1);
  return X10_OK;
}


error_t
asyncSpawnInlineAgg_i (place_t target, async_handler_t handler, int N)
{
 counter[handler][target]++;
 total[handler]++;

 if (total[handler] >= AGG_LIMIT)
  {
    ulong max = 0;
    int task = 0;
    for (place_t i = 0; i < MAX_PLACES; i++) {
      if (counter[handler][i] > max) {
        max = counter[handler][i];
        task = i;
      }
    }
  
      struct header buf;
      buf.handler = handler;
      buf.N = N;
      lapi_cntr_t cntr;
      int tmp;
      LAPI_Setcntr (GetHandle(), &cntr, 0);
      LAPI_Amsend (GetHandle(),
                   task,
                   (void*) 1, 
                   &buf,
                   sizeof(header),
                   (void*) argbuf[handler][task],
                   max * N * sizeof(async_arg_t),
                   NULL,
                   &cntr, //NULL,
                   NULL);
    LAPI_Waitcntr (GetHandle(), &cntr, 1, &tmp);
    total[handler] -= max;
    counter[handler][task] = 0;
  } 

   return X10_OK;

}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, int N ...)
{
 va_list  list;
 
 va_start (list, N);
 
 lapi_cntr_t origin_cntr;
 
 ulong& count = counter [handler][target];
 for (int i =0; i < N; i++)
   argbuf[handler][target][N*count+i] = va_arg (list, async_arg_t); 
 
 va_end (list);
  return asyncSpawnInlineAgg_i (target, handler, N);
}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0)
{
  ulong& count = counter [handler][target];
  argbuf[handler][target][count] =  arg0;
  return asyncSpawnInlineAgg_i (target, handler, 1);
}

error_t
x10lib::asyncSpawnInlineAgg (place_t target, async_handler_t handler, async_arg_t arg0, async_arg_t arg1)
{
  ulong& count = counter [handler][target];
  argbuf[handler][target][2*count] =  arg0;
  argbuf[handler][target][2*count+1] =  arg1;
  return asyncSpawnInlineAgg_i (target, handler,2);
}


// Local Variables:
// mode: C++
// End:

