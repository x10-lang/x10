/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: dist_array.h,v 1.1 2007-12-07 11:12:48 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_DIST_ARRAY_H__
#define __X10_DIST_ARRAY_H__

#include "dist.h"
#include "array_table.h"
    
namespace x10lib {
  
  template <typename T, int RANK>
  class DistArray 
  {
      
  public:
      
    DistArray (Dist<RANK>* dist, int handle) :
      _dist (dist->clone()),
      _handle (handle)
    {}
      
    ~DistArray ()
    {
      delete _dist;
    }

    Dist<RANK>* dist() 
    {
      return _dist;
    }
      
    int handle()
    {
      return _handle;
    }

  private:      

    Dist<RANK>* _dist;
    int _handle;
  };
  
  template <typename T, int RANK>
  DistArray<T, RANK>* 
  MakeDistArray (Dist<RANK>* dist, int handle)
  {       
    lapi_cntr_t completion_cntr;
    lapi_cntr_t origin_cntr;
      
    int tmp;

    LAPI_Setcntr (__x10_hndl, &completion_cntr, 0);
      
    for (int p = 0; p < dist->nplaces(); ++p) {
	
      RectangularRegion<RANK> local_region = dist->restrict (p);
	
      ByteArray* local_array = new ByteArray;
	
      local_array->_nelements = local_region.card();
      local_array->_elsize = sizeof (T);
      local_array->_rank = RANK;
      memcpy (local_array->_origin, &(local_region.origin()), RANK * sizeof (int));
      memcpy (local_array->_diagonal, &(local_region.diagonal()), RANK * sizeof (int));
      memcpy (local_array->_stride, &(local_region.stride()), RANK * sizeof (int));
	
      if (p != __x10_my_place)
	{	      
	  local_array->_data = NULL;
	    
	  LAPI_Setcntr (__x10_hndl, &origin_cntr, 0);

	  LAPI_Amsend (__x10_hndl, 
			    p,
			    (void*) ARRAY_CONSTRUCTION_HANDLER,
			    &handle,
			    sizeof(int),
			    local_array,
			    sizeof (ByteArray),
			    NULL,
			    &origin_cntr,
			    &completion_cntr);
	    
	  LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp); 
	  delete local_array;
	}	
      else {	  
	local_array->_data = (char*) new T[local_region.card()];	    
	registerLocalSection (local_array, handle);
      }
	
    }
    //Wait for completion
    LAPI_Waitcntr (__x10_hndl, &completion_cntr, dist->nplaces()-1, &tmp); 
      
    return new DistArray<T, RANK> (dist, handle);
  }
  
  
  template <typename T, int RANK>
  x10_err_t
  FreeDistArray (DistArray<T, RANK>* array)
  {       
    lapi_cntr_t completion_cntr;
    lapi_cntr_t origin_cntr;
    int tmp;

    LRC (LAPI_Setcntr (__x10_hndl, &completion_cntr, 0));
      
    for (int p = 0; p < array->dist()->nplaces(); ++p) {
	
      if (p != __x10_my_place)
	{	    
	    
	  int handle = array->handle();
	  LRC (LAPI_Setcntr (__x10_hndl, &origin_cntr, 0));
	  LRC (LAPI_Amsend (__x10_hndl, 
			    p,
			    (void*) ARRAY_DELETION_HANDLER,
			    &handle,
			    sizeof(int),
			    NULL,
			    0,
			    NULL,
			    &origin_cntr,
			    &completion_cntr));
	    
	  LRC (LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp)); 
	}	
      else {
	ByteArray* local_array = (ByteArray*) getLocalSection (array->handle());
	delete local_array;
	freeLocalSection (array->handle());
      }
		
    }
      
    //Wait for completion
    LRC (LAPI_Waitcntr (__x10_hndl, &completion_cntr, array->dist()->nplaces()-1, &tmp)); 
      
    return X10_OK;
  }
};

#endif /*__X10_ARRAY_H__ */


// Local Variables:
// mode: C++
// End:

