#include <x10/xmacros.h>
#include <x10/types.h>
#include <stdarg.h>

using namespace x10lib;

async_arg_t argbuf[MAX_AGG_HANDLERS][MAX_AGG_TASKS][MAX_ASYNC_ARGS*MAX_AGG_SIZE];
ulong counter[MAX_AGG_HANDLERS][MAX_AGG_TASKS];
int maxCounter[MAX_AGG_HANDLERS];
int total [MAX_AGG_HANDLERS];

template <int N, typename F>
inline void
batchAsyncDispatch (async_arg_t *a, ulong len) {
    F f;
    for (int i = 0; i < len; i += N * sizeof(async_arg_t)) {
      f(*a);
      a = a + N;
    }
}

struct param 
{
  int handle;
  ulong len;		
};

struct comp
{
  ulong len;
  void* buf;
};

template <int N, typename F>
void
asyncSpawnCompHandlerAgg (lapi_handle_t *handle, void* a) 
{
  comp* c = (comp*) a; 
  batchAsyncDispatch<N, F> ((async_arg_t*) (c->buf), c->len);
  delete [] c->buf;
  delete c;
}

template<int N>
error_t
asyncFlush_t (async_handler_t handler)
{
 	   lapi_cntr_t origin_cntr;
    int tmp;
    for (int j =0; j < MAX_PLACES; j++) {
      if ( j != here() && counter[handler][j] != 0) {
        LRC (LAPI_Setcntr (GetHandle(), &origin_cntr, 0)); 
        LRC (LAPI_Amsend (GetHandle(),
                     j,
	             (void*) (5+handler),
                     NULL, 
                     0, 
                     (void*) argbuf[handler][j],
                     counter[handler][j] * N * sizeof(async_arg_t),
                     NULL,
                     &origin_cntr,
                     NULL));
         LRC (LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp));
         total[handler] -= counter[handler][j];
         counter[handler][j] =0;
      }
    }

    return X10_OK;
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
    //cout << "here " << *msg_len << endl;
    batchAsyncDispatch<N, F> ((async_arg_t*)ret_info->udata_one_pkt_ptr, *msg_len);
    ret_info->ctl_flags = LAPI_BURY_MSG;
    *comp_h = NULL;
    return NULL;
  } else {
    ret_info->ret_flags = LAPI_LOCAL_STATE; 
    *comp_h = asyncSpawnCompHandlerAgg <N, F>;
    comp* c = new comp;
    c->len = *msg_len;
    c->buf = (void*) new char[*msg_len];
    *user_info = (void*) c;
    return c->buf;
  }
}

template <int N, typename F>
error_t
asyncRegister_t (async_handler_t handle)
{
  LAPI_Addr_set (GetHandle(), (void*) asyncSpawnHandlerAgg<N, F>, 5+handle);

  return X10_OK;
}

template <int N, typename F>
inline error_t
asyncSpawnInlineAgg_i (place_t target, async_handler_t handler)
{
 
 lapi_cntr_t origin_cntr;

 counter[handler][target]++;
 total[handler]++;

 if (total[handler] >= MAX_AGG_SIZE)
  {
    ulong max = 0;
    int task = 0;
    for (place_t i = 0; i < MAX_AGG_TASKS; i++) {
      task = counter[handler][i] > max ? i :  task;
      max = counter[handler][i] > max ? counter[handler][i] : max; 
    }

    int h = handler;
 
    lapi_cntr_t origin_cntr;
    int tmp;
    LRC (LAPI_Setcntr (GetHandle(), &origin_cntr, 0)); 
    LRC (LAPI_Amsend (GetHandle(),
                 task,
                 (void*) (5+handler),  
                 NULL,
                 0,
                 (void*) argbuf[handler][task],
                 max * N * sizeof(async_arg_t),
                 NULL,
                 &origin_cntr,
                 NULL));
    LRC (LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp));
    total[handler] -= max;
    counter[handler][task] = 0;
  } 

  return X10_OK;
}
template <int N, typename F>
error_t
asyncSpawnInlineAgg_t (place_t target, async_handler_t handler,...)
{
 va_list  list;
 
 va_start (list, handler);
 
 ulong count = counter [handler][target];
 for (int i =0; i < N; i++)
   argbuf[handler][target][N*count+i] = va_arg (list, async_arg_t); 


 va_end (list);

 return asyncSpawnInlineAgg_i<N, F> (target, handler);
}


template <typename F>
error_t 
asyncSpawnInlineAgg_t (place_t target, async_handler_t handler, async_arg_t arg0)
{
 ulong& count = counter [handler][target];
 argbuf[handler][target][count] = arg0;
 return asyncSpawnInlineAgg_i<1, F> (target, handler);
}

// Local Variables:
// mode: C++
// End:
