#include <assert.h>

#include "rts_messaging.h"

#include "xqueue.h"

#include "x10_internal.h"

static x10_finish_record_t __x10_global_frecord = {0, 0};

EXTERN void __x10_callback_asyncswitch(x10_async_closure_t* closure, x10_finish_record_t* frecord, 
				       x10_clock_t* clocks, int num_clocks);

x10_async_queue_t __x10_async_queue;

/* re-entrant */
X10::CommHandle
X10::Acts::AsyncSpawn(const Place tgt,
		      const AsyncClosure* closure,
		      const size_t cl_size,
		      const FinishRecord* frecord,
		      const Clock* clocks,
		      const int num_clocks)
{  
  assert(num_clocks == 0); //clocks not handled yet
    
  size_t header_size;
  
  __upcrt_AMHeader_t* header;
  
  if (frecord->finish_id != 0) 
    {      
      //TODO: see if padding is required
      header_size = sizeof(__upcrt_AMHeader_t) + sizeof(__x10::NormalAsyncDescr);
      
      header = (__upcrt_AMHeader_t*) malloc(header_size);
      
      __x10::NormalAsyncDescr* async_descr = (__x10::NormalAsyncDescr*) header->data;
      
      async_descr->finish_record = *frecord;
      
      async_descr->parent = __x10::here;
      
      async_descr->cl_size = cl_size;
      
      header->handler = __x10::NormalAsyncHandler;
      
      header->headerlen = header_size;
      
      __x10::FinishBookeepingOutgoing (frecord, tgt);  
      
    } else {
      
      header_size = sizeof(__upcrt_AMHeader_t) + sizeof(__x10::GlobalAsyncDescr);
      
      header = (__upcrt_AMHeader_t*) malloc(header_size);
      
      __x10::GlobalAsyncDescr* async_descr = (__x10::GlobalAsyncDescr*) header->data;
      
      header->handler = __x10::GlobalAsyncHandler;
      
      header->headerlen = header_size;
      
      async_descr->cl_size = cl_size;
      
      __x10::FinishBookeepingOutgoing (&__x10_global_frecord, tgt);  
    }
    
  __x10::CommHandle* comm_handle = new __x10::CommHandle;  
  
  comm_handle->async_handle =  __upcrt_distr_amsend_post (tgt,
							  header,
							  (__xlupc_local_addr_t) closure,
							  cl_size);  
  
  comm_handle->header_buf = (void*) header;
  
  return comm_handle;
}

X10::Err
X10::Acts::AsyncSpawnWait (CommHandle req)
{
  __x10::CommHandle* handle = (__x10::CommHandle*) req;

  __upcrt_distr_wait (handle->async_handle);
  
  if (handle->header_buf)
    free (handle->header_buf);
  
  free(req);

  return X10_OK;
}

X10::Err
X10::Acts::Probe()
{
  __upcrt_distr_wait(NULL);
  
  __x10::Flush();

  return X10_OK;
}

X10::Err
X10::Acts::Wait()
{
  do {
    x10_probe();
  }while (!__x10::terminate_program);

  return X10_OK;
}

//C Bindings
x10_comm_handle_t
x10_async_spawn(const x10_place_t tgt, 
		const x10_async_closure_t* closure,
		const size_t cl_size,
		const x10_finish_record_t* frecord,
		const x10_clock_t* clocks, 
		const int num_clocks)
{
  return X10::Acts::AsyncSpawn(tgt, (X10::AsyncClosure*) closure, cl_size,frecord, clocks, num_clocks);
}

x10_err_t
x10_async_spawn_wait (x10_comm_handle_t req)
{
  return X10::Acts::AsyncSpawnWait(req);
}

x10_err_t
x10_probe()
{
  return X10::Acts::Probe();
}

x10_err_t
x10_wait()
{
  return X10::Acts::Wait();
}

// __x10

void
__x10::AsyncQueueAdd(void* async_descr)
{  
  PushQueue (__x10_async_queue, async_descr);
}

__xlupc_local_addr_t __x10::NormalAsyncHandler(const __upcrt_AMHeader_t* header, 
						 __upcrt_AMComplHandler_t** comp_h, 
						 void** arg)  
{
  //  printf ("NORMAL async handler\n");

  __x10::AsyncDescr* async_descr = (__x10::AsyncDescr*) malloc (sizeof(__x10::AsyncDescr));
  
  async_descr->_async_type = __x10::NORMAL_ASYNC;
  
  async_descr->u._normal_async_descr = *((__x10::NormalAsyncDescr*) header->data);
  
  __x10::FinishBookeepingIncoming(&(async_descr->u._normal_async_descr.finish_record));
  
  async_descr->closure = (x10_async_closure_t*) malloc(async_descr->u._normal_async_descr.cl_size);
  
  *arg = (void*) async_descr;
  
  *comp_h = __x10::AsyncQueueAdd;
  
  return (__xlupc_local_addr_t) async_descr->closure;  
}

__xlupc_local_addr_t __x10::GlobalAsyncHandler(const __upcrt_AMHeader_t* header,
						 __upcrt_AMComplHandler_t** comp_h,
					       void** arg)
{
  //  printf ("GLOBAL async handler\n");
  
  __x10::FinishBookeepingIncoming(&__x10_global_frecord);
  
  __x10::AsyncDescr* async_descr = (__x10::AsyncDescr*) malloc (sizeof(__x10::AsyncDescr));
  
  async_descr->_async_type = __x10::GLOBAL_ASYNC;
  
  async_descr->u._global_async_descr = *((__x10::GlobalAsyncDescr*) header->data);  

  async_descr->closure = (x10_async_closure_t*) malloc(async_descr->u._global_async_descr.cl_size);
  
  *arg = (void*) async_descr; 
  
  *comp_h = __x10::AsyncQueueAdd;
  
  return (__xlupc_local_addr_t) async_descr->closure;
}


void
__x10::AsyncInit()
{
  __x10_async_queue = CreateQueue();
}


void
__x10::Flush()
{
  x10_async_queue_el_t cur;
  
  for(cur = __x10_async_queue->_head; cur != NULL;)
    {	
      RemoveQueue(__x10_async_queue, cur);      
      
      __x10::AsyncDescr* async_descr = (__x10::AsyncDescr*) cur->_el;
      
      __x10::AsyncDispatch(async_descr);
            
      x10_async_queue_el_t prev = cur;
      
      cur = cur->_next;

      free(async_descr->closure);
      
      free(async_descr);
      
      free(prev);
    }  
}

void __x10::AsyncDispatch(__x10::AsyncDescr* async_descr)
{  

  x10_finish_record_t* __x10_tmp_frecord = __x10::CurFinishRecord;
  
  switch (async_descr->_async_type)
    {                 
    case __x10::NORMAL_ASYNC :      

      __x10::CurFinishRecord = &async_descr->u._normal_async_descr.finish_record;

      __x10_callback_asyncswitch (async_descr->closure,
				  &async_descr->u._normal_async_descr.finish_record, NULL, 0);
      break;
      
    case __x10::GLOBAL_ASYNC:

      __x10::CurFinishRecord = &__x10_global_frecord;

      __x10_callback_asyncswitch (async_descr->closure, &__x10_global_frecord, NULL, 0);
      
      break;
      
    default :
      
      printf ("other cases not handled\n");
    }

  __x10::CurFinishRecord = __x10_tmp_frecord;
}
