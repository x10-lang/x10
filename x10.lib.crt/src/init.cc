#include <assert.h>

#include "rts_messaging.h"

#include "x10.h"

x10_place_t __x10_here;

unsigned int __x10_numplaces;

bool __x10_terminate_program = 0;

extern void __x10_finish_init();

extern void __x10_async_init();

static
__xlupc_local_addr_t __x10_termination_handler (const __upcrt_AMHeader_t* header, 
						__upcrt_AMComplHandler_t** comp_h, 
						void** arg)  
{ 
  __x10_terminate_program = true;
  
  *comp_h = NULL;
  
  return NULL;
}

/* not re-entrant */
x10_err_t
x10_init ()
{
  __x10_finish_init();
  
  __x10_async_init();

  __upcrt_distr_setup(0, &__x10_here, &__x10_numplaces);

  return X10_OK;
}

/* not re-entrant */
x10_err_t
x10_finalize()
{
  if (__x10_here==0) {
 
   __upcrt_AMHeader_t header;
        
    header.handler = __x10_termination_handler;
    
    header.headerlen = sizeof(header);
    for (unsigned i = 0; i  < __x10_numplaces; ++i)
      if (i != __x10_here) {
	
	void* req = __upcrt_distr_amsend_post (i,
					       &header,
					       NULL,
					       0);
	
	__upcrt_distr_wait (req);
      }
  }
  
  __upcrt_distr_finish();
  
  return X10_OK;
}

x10_err_t
x10_infinite_poll()
{
  while (!__x10_terminate_program)
    x10_probe();
  
  return X10_OK;
}
