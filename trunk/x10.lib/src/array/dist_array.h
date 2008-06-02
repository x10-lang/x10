/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: dist_array.h,v 1.5 2008-06-02 16:08:24 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_DIST_ARRAY_H__
#define __X10_DIST_ARRAY_H__

#include <x10/dist.h>
#include <x10/array_table.h>
#include <x10/am.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
  extern int __x10_my_place;

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
    lapi_handle_t x10_handle = x10lib::GetHandle();

    LRC (LAPI_Setcntr (x10_handle, &completion_cntr, 0));

    for (int p = 0; p < dist->nplaces(); ++p) {

      RectangularRegion<RANK> local_region = dist->restriction (p);

      GenericArray* local_array = new GenericArray;

      local_array->_nelements = local_region.card();
      local_array->_elsize = sizeof (T);
      local_array->_rank = RANK;

      Point<RANK> origin = local_region.origin();
      Point<RANK> diagonal = local_region.diagonal();
      Point<RANK> stride = local_region.stride();

      memcpy (local_array->_origin, &origin, RANK * sizeof (int));
      memcpy (local_array->_diagonal, &diagonal, RANK * sizeof (int));
      memcpy (local_array->_stride, &stride, RANK * sizeof (int));

      if (p != __x10_my_place)
	{
	  local_array->_data = NULL;

	  LRC (LAPI_Setcntr (x10_handle, &origin_cntr, 0));

	  LRC (LAPI_Amsend (x10_handle,
			    p,
			    (void*) ARRAY_CONSTRUCTION_HANDLER,
			    &handle,
			    sizeof(int),
			    local_array,
			    sizeof (GenericArray),
			    NULL,
			    &origin_cntr,
			    &completion_cntr));

	  LRC (LAPI_Waitcntr (x10_handle, &origin_cntr, 1, &tmp));
	  delete local_array;
	}
      else {
	local_array->_data = (char*) new T[local_region.card()];
	registerLocalSection (local_array, handle);
      }

    }
    //Wait for completion
    LRC (LAPI_Waitcntr (x10_handle, &completion_cntr, dist->nplaces()-1, &tmp));

    return new DistArray<T, RANK> (dist, handle);
  }

  template <typename T, int RANK>
  x10_err_t
  FreeDistArray (DistArray<T, RANK>* array)
  {
    lapi_cntr_t completion_cntr;
    lapi_cntr_t origin_cntr;
    int tmp;
    lapi_handle_t x10_handle = x10lib::GetHandle();

    LRC (LAPI_Setcntr (x10_handle, &completion_cntr, 0));

    for (int p = 0; p < array->dist()->nplaces(); ++p) {

      if (p != __x10_my_place)
	{
	  int handle = array->handle();
	  LRC (LAPI_Setcntr (x10_handle, &origin_cntr, 0));
	  LRC (LAPI_Amsend (x10_handle,
			    p,
			    (void*) ARRAY_DELETION_HANDLER,
			    &handle,
			    sizeof(int),
			    NULL,
			    0,
			    NULL,
			    &origin_cntr,
			    &completion_cntr));

	  LRC (LAPI_Waitcntr (x10_handle, &origin_cntr, 1, &tmp));
	}
      else {
	GenericArray* local_array = (GenericArray*) getLocalSection (array->handle());
	delete local_array;
	freeLocalSection (array->handle());
      }

    }

    //Wait for completion
    LRC (LAPI_Waitcntr (x10_handle, &completion_cntr, array->dist()->nplaces()-1, &tmp));

    return X10_OK;
  }

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_DIST_ARRAY_H */
