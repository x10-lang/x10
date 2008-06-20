#include <assert.h>
#include <string.h>

#include "rts_messaging.h"

#include "x10_internal.h"

extern x10_place_t __x10_here;

extern unsigned int __x10_numplaces;

EXTERN x10_addr_t __x10_callback_array_copy_switch (x10_async_closure_t*);

///AM handlers (internal)
static
__xlupc_local_addr_t __x10_async_array_copy_handler (const __upcrt_AMHeader_t* header, 
						     __upcrt_AMComplHandler_t** comp_h, 
						     void** arg)  
{
  *comp_h  = NULL;
  
  return __x10_callback_array_copy_switch ((x10_async_closure_t*) (header->data));
}

EXTERN x10_comm_handle_t
x10_array_put(const x10_addr_t src, 
	      const x10_addr_t dst, 
	      const size_t offset, 
	      const size_t nbytes)
{
  __x10::CommHandle* handle = new __x10::CommHandle;
  
  printf ("%d\n", x10_ref_get_loc(dst));
  printf ("%x\n", x10_ref_get_addr(dst));
  handle->async_handle = __upcrt_distr_direct_put_post (0,
						     x10_ref_get_loc(dst),
						     x10_ref_get_addr(dst),
						     offset,
						     src,
						     nbytes);  
  handle->header_buf = NULL;
  
  return handle;
}

EXTERN x10_comm_handle_t
x10_array_get(const x10_addr_t dst, 
	      const x10_addr_t src, 
	      const size_t offset,
	      const size_t nbytes)
{
  __x10::CommHandle* handle = new __x10::CommHandle;

  printf ("%d\n", x10_ref_get_loc(src));
  printf ("%d\n", x10_ref_get_addr(src));
  
  handle->async_handle =   __upcrt_distr_direct_get_post (0,
						       x10_ref_get_loc(src),
						       dst,
						       x10_ref_get_addr(src),
						       offset,
						       nbytes);
  handle->header_buf = NULL;
  
  return handle;
}

EXTERN x10_comm_handle_t
x10_async_array_put(const x10_place_t tgt, 
		    const x10_addr_t src, 
		    const size_t nbytes,
		    const x10_async_closure_t* dst_closure, 
		    const size_t dst_cl_size)
{

  __x10::CommHandle* comm_handle = new __x10::CommHandle;

  size_t header_size = sizeof(__upcrt_AMHeader_t) + dst_cl_size;
  
  __upcrt_AMHeader_t* header = (__upcrt_AMHeader_t*) malloc(header_size);
  
  header->headerlen = header_size;
  header->handler = __x10_async_array_copy_handler;

  memcpy (header->data, dst_closure, dst_cl_size);
  
  comm_handle->async_handle =  __upcrt_distr_amsend_post(tgt,
							header,
							src,
							nbytes);  
  comm_handle->header_buf = (void*) header;
  
  return comm_handle;  
}
