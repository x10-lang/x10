#include <assert.h>

#include "rts_messaging.h"

#include "x10_internal.h"

x10_place_t __x10::here;

x10_place_t __x10::nplaces;

bool __x10::terminate_program = false;

__xlupc_local_addr_t __x10::TerminationHandler (const __upcrt_AMHeader_t* header, 
						__upcrt_AMComplHandler_t** comp_h, 
						void** arg)  
{ 
  __x10::terminate_program = true;
  
  *comp_h = NULL;
  
  return NULL;
}

/* not re-entrant */
x10_err_t
X10::Init()
{
  __x10::FinishInit();
  
  __x10::AsyncInit();

  __upcrt_distr_setup(0, (unsigned int*) &__x10::here, (unsigned int*) &__x10::nplaces);
  
  return X10_OK;
}

/* not re-entrant */
x10_err_t
X10::Finalize()
{
  if (__x10::here==0) {
    
    __upcrt_AMHeader_t header;
    
    header.handler = __x10::TerminationHandler;
    
    header.headerlen = sizeof(header);
    
    for (unsigned i = 0; i  < __x10::nplaces; ++i)
      
      if (i != __x10::here) {
	
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
x10_init()
{
  return X10::Init();
}

x10_err_t
x10_finalize()
{
  return X10::Finalize();
}


x10_place_t
x10_nplaces()
{
  return __x10::nplaces;
}

x10_place_t
x10_here()
{
  return __x10::here;
}
