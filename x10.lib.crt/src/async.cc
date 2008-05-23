#include <assert.h>

#include "rts_messaging.h"

#include "queue.h"
#include "x10.h"

extern x10_place_t __x10_here;

extern x10_place_t __x10_numplaces;

static x10_finish_record_t __x10_global_frecord = {0, 0};

/* normal async descriptor */
typedef struct 
{
  x10_finish_record_t finish_record; /* finish record of the async */
  x10_place_t parent;   /* parent of the async */
  size_t cl_size; /* size of the closure (will be removed later)*/   
} __x10_normal_async_descr_t;


/* global async descriptor */
typedef struct
{
  size_t cl_size; /* size of the closure (will be removed later) */
} __x10_global_async_descr_t;


/* kinds of asyncs */
typedef enum 
{
  NORMAL_ASYNC,
  GLOBAL_ASYNC,
  CLOCKED_NORMAL_ASYNC,
  CLOCKED_GLOBAL_ASYNC
} __x10_async_type_t;

/* async descriptor */
typedef struct
{
  __x10_async_type_t _async_type;
  
  x10_async_closure_t* closure;

  union {    
    
    __x10_normal_async_descr_t _normal_async_descr;
    
    __x10_global_async_descr_t _global_async_descr;
  }u;
} __x10_async_descr_t;


EXTERN void __x10_callback_asyncswitch (x10_async_closure_t* closure, x10_finish_record_t* frecord, x10_clock_t* clocks, int num_clocks);

extern void __x10_finish_bookeeping_outgoing(const x10_finish_record_t* finish_record, x10_place_t tgt);

extern void __x10_finish_bookeeping_incoming(x10_finish_record_t* finish_record);

void __x10_async_dispatch(__x10_async_descr_t*);

void __x10_flush();

x10_async_queue_t __x10_async_queue;

///AM handlers (internal)

static void
__x10_async_queue_add(void* async_descr)
{  
  PushQueue (__x10_async_queue, async_descr);
}

static
__xlupc_local_addr_t __x10_normal_async_handler (const __upcrt_AMHeader_t* header, 
						 __upcrt_AMComplHandler_t** comp_h, 
						 void** arg)  
{
  printf ("NORMAL async handler\n");

  __x10_async_descr_t* async_descr = (__x10_async_descr_t*) malloc (sizeof(__x10_async_descr_t));
  
  async_descr->_async_type = NORMAL_ASYNC;
  
  async_descr->u._normal_async_descr = *((__x10_normal_async_descr_t*) header->data);
  
  __x10_finish_bookeeping_incoming(&(async_descr->u._normal_async_descr.finish_record));
  
  async_descr->closure = (x10_async_closure_t*) malloc(async_descr->u._normal_async_descr.cl_size);
  
  *arg = (void*) async_descr;
  
  *comp_h = __x10_async_queue_add;
  
  return (__xlupc_local_addr_t) async_descr->closure;  
}

static
__xlupc_local_addr_t __x10_global_async_handler (const __upcrt_AMHeader_t* header,
						 __upcrt_AMComplHandler_t** comp_h,
						 void** arg)
{
  printf ("GLOBAL async handler\n");
  
  __x10_finish_bookeeping_incoming(&__x10_global_frecord);
  
  __x10_async_descr_t* async_descr = (__x10_async_descr_t*) malloc (sizeof(__x10_async_descr_t));
  
  async_descr->_async_type = GLOBAL_ASYNC;
  
  async_descr->u._global_async_descr = *((__x10_global_async_descr_t*) header->data);
  
  async_descr->closure = (x10_async_closure_t*) malloc(async_descr->u._global_async_descr.cl_size);
  
  *arg = (void*) async_descr; 
  
  *comp_h = __x10_async_queue_add;
  
  return (__xlupc_local_addr_t) async_descr->closure;
}


void
__x10_async_init()
{
  __x10_async_queue = CreateQueue();
}


/* re-entrant */
x10_comm_handle_t
x10_async_spawn(const x10_place_t tgt, 
		const x10_async_closure_t* closure,
		const size_t cl_size,
		const x10_finish_record_t* frecord,
		const x10_clock_t* clocks, 
		const int num_clocks)
{
  assert (num_clocks == 0); //clocks not handled yet
 	
  x10_comm_handle_t comm_handle;  
  
  size_t header_size;
  
  __upcrt_AMHeader_t* header;
  
  if (frecord->finish_id != 0) 
    {      
      //TODO: see if padding is required
      header_size = sizeof(__upcrt_AMHeader_t) + sizeof(__x10_normal_async_descr_t);

      header = (__upcrt_AMHeader_t*) malloc(header_size);
      
      __x10_normal_async_descr_t* async_descr = (__x10_normal_async_descr_t*) header->data;
      
      async_descr->finish_record = *frecord;
      
      async_descr->parent = __x10_here;
      
      async_descr->cl_size = cl_size;
      
      header->handler = __x10_normal_async_handler;
	
      header->headerlen = header_size;
      
      __x10_finish_bookeeping_outgoing (frecord, tgt);  
    
    } else {
      
      header_size = sizeof(__upcrt_AMHeader_t) + sizeof(__x10_global_async_descr_t);
      
      header = (__upcrt_AMHeader_t*) malloc(header_size);
      
      __x10_global_async_descr_t* async_descr = (__x10_global_async_descr_t*) header->data;
      
      header->handler = __x10_global_async_handler;
      
      header->headerlen = header_size;
      
      async_descr->cl_size = cl_size;
      
      __x10_finish_bookeeping_outgoing (&__x10_global_frecord, tgt);  
    }
  
  
  comm_handle.rts_handle =  __upcrt_distr_amsend_post (tgt,
						       header,
						       (__xlupc_local_addr_t) closure,
						       cl_size);  
  comm_handle.header_buf = (void*) header;
  
  return comm_handle;
}

x10_err_t
x10_async_spawn_wait (x10_comm_handle_t req)
{
  __upcrt_distr_wait (req.rts_handle);
  
  if (req.header_buf)
    free (req.header_buf);
  
  return X10_OK;
}

x10_err_t
x10_probe()
{
  __upcrt_distr_wait(NULL);
  
  __x10_flush();

  return X10_OK;
}

void
__x10_flush()
{
  x10_async_queue_el_t cur;
  
  for(cur = __x10_async_queue->_head; cur != NULL;)
    {	
      RemoveQueue(__x10_async_queue, cur);      
      
      __x10_async_descr_t* async_descr = (__x10_async_descr_t*) cur->_el;
      
      __x10_async_dispatch(async_descr);
      
      free(async_descr->closure);
      
      free(async_descr);
      
      x10_async_queue_el_t prev = cur;
      
      cur = cur->_next;
      
      free(prev);
    }  
}

void __x10_async_dispatch(__x10_async_descr_t* async_descr)
{  
  switch (async_descr->_async_type)
    {      
      
    case NORMAL_ASYNC :      
      __x10_callback_asyncswitch (async_descr->closure,
				  &async_descr->u._normal_async_descr.finish_record, NULL, 0);
      break;
      
    case GLOBAL_ASYNC:
      __x10_callback_asyncswitch (async_descr->closure, &__x10_global_frecord, NULL, 0);
      
      break;
      
    default :
      
      printf ("other cases not handled\n");
    }
}
