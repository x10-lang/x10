/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: array.tcc,v 1.1 2007-12-07 11:25:48 ganeshvb Exp $ */

#include "array.h"
#include <x10/alloc.h>
#include "x10/handlers.h"
#include "x10/gas.h"
#include "x10/xassert.h"
#include <x10/xmacros.h>
#include <x10/clock.h>

namespace x10lib {
  typedef void (*void_func_t)();
  
  typedef void* (*lapi_header_t) (lapi_handle_t,  void*, uint*, ulong*, compl_hndlr_t**, void**);
 

template <class T, int RANK>
x10_err_t
asyncArrayCopy (Array<T, RANK>* src, int srcOffset,
		Array<T, RANK>* dest, int dstOffset,
		int target, int len, Clock* c)
{
  int tmp;
  lapi_cntr_t origin_cntr;

  //LRC(LAPI_Setcntr(__x10_hndl, &origin_cntr, 0));
  
  LAPI_Put (__x10_hndl,
	    target,
	    len * sizeof(T), 
	    (void*) ((char*) dest  + sizeof (Array<T, RANK>) + sizeof(T) * dstOffset),
	    (void*) ((char*) src->raw() + sizeof(T) * srcOffset),	    
	    NULL,
	    NULL, //&origin_cntr,
	    NULL);
  //LRC(LAPI_Waitcntr(__x10_hndl, &origin_cntr, 1, &tmp));
  
  return X10_OK;
}


template <class T, int RANK>
x10_err_t
asyncArrayCopy (Array<T, RANK>* src, Point <RANK> srcOffset,
		Array<T, RANK>* dest, Point <RANK> dstOffset,
		int target, int len, Clock* c)
{
  int tmp;
  lapi_cntr_t origin_cntr;
  
  LRC(LAPI_Setcntr(__x10_hndl, &origin_cntr, 0));
  
  lapi_header_t array_copy_handler = (lapi_header_t) remoteArrayCopy<T, RANK>;
  LAPI_Addr_set (__x10_hndl, (void*) array_copy_handler, ARRAY_COPY_HANDLER);
   
  remoteArrayElementDescr header;
  header.addr = (void*) dest;
  for (int i = 0; i < RANK; i++)
    header.point[i] = dstOffset.value(i);
 
  assert (dest);
  
  if (target == __x10_my_place) 
    {
      memcpy (&dest->getLocalElementAt(dstOffset), &src->getLocalElementAt (srcOffset), len*sizeof(T));
    } else {
      LAPI_Amsend (__x10_hndl, 
		   target,
		   (void*) ARRAY_COPY_HANDLER, 
		   (void*) &header,
		   sizeof(header),
		   (void*) &src->getLocalElementAt (srcOffset), 
		   len * sizeof (T),
		   NULL,
		   &origin_cntr,
		   NULL);
      
      LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp);
    }
  
  return X10_OK;
}


//================= Local Arrays ====================================
template <typename T, int RANK>
T*
makeLocalArray (int size) 
{
   T* ret = new T [size];
   memset (ret, 0, sizeof(T) * size);
   return ret;  
}

//================ Distributed Arrays =============================

//================ Local creation of equi-distributed arrays =============================

template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArrayLocalHeap (const Region<RANK>* region, const Dist<RANK>* dist)
{  
  assert (GlobalSMAlloc);

  void** addrTable = NULL;
  Array<T, RANK>* ret = NULL;
  
  uint64_t local_size = dist ? dist->card() : region->card();
    
  addrTable = new void* [__x10_num_places];
  for (x10_place_t p = 0; p < __x10_num_places; p++)
    addrTable[p] =  NULL;

  void* arraySpace = new char[(uint64_t) sizeof(Array<T, RANK>) + local_size * (uint64_t) sizeof(T)];
  T* data = (T*) ((char*) arraySpace + sizeof(Array<T, RANK>)); 
  ret = new(arraySpace) Array<T, RANK>(region, dist, local_size, data, addrTable);

  delete [] addrTable;

  return ret;
}

template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArrayLocal (const Region<RANK>* region, const Dist<RANK>* dist)
{  
  assert (GlobalSMAlloc);

  void** addrTable = NULL;
  Array<T, RANK>* ret = NULL;
  
  //should move to distribution
  uint64_t local_size = dist ? dist->card() : region->card();
  //uint64_t local_size = region->card();
  
  addrTable = new void* [__x10_num_places];
  for (x10_place_t p = 0; p < __x10_num_places; p++)
    addrTable[p] = (char*) GlobalSMAlloc->addrTable (p) + GlobalSMAlloc->offset();

  void* arraySpace = x10lib::GlobalSMAlloc->chunk ((uint64_t) sizeof(Array<T, RANK>) + local_size * (uint64_t) sizeof(T));
  T* data = (T*) ((char*) arraySpace + sizeof(Array<T, RANK>)); 
  ret = new(arraySpace) Array<T, RANK>(region, dist, local_size, data, addrTable);

  delete [] addrTable;

  return ret;
}

template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArrayRemote (const Region<RANK>* region, const Dist<RANK>* dist, const x10_place_t target)
{
  unsigned int buf_size = sizeof (metaDataDescr<RANK, REGION, DIST>); 
  metaDataDescr<RANK, REGION, DIST>* buf = new metaDataDescr<RANK, REGION, DIST>;
  memcpy (&(buf->region), region, sizeof(REGION<RANK>));
  
  lapi_header_t construction_handler = (lapi_header_t) arrayConstructionGlobalSM <T, RANK, REGION, DIST>;
  LAPI_Addr_set (__x10_hndl, (void*) construction_handler, ARRAY_CONSTRUCTION_HANDLER);
  
  lapi_cntr_t completion_cntr;
  int tmp;
  
  LAPI_Setcntr (__x10_hndl, &completion_cntr, 0);
  
  if (target != __x10_my_place)
    {
      LAPI_Amsend (__x10_hndl, 
		   target,
		   (void*) construction_handler,
		   NULL,
		   0,
		   buf, 
		   buf_size,
		   NULL,
		   NULL,
		   &completion_cntr);
      
      //Wait for completion
      LAPI_Waitcntr (__x10_hndl, &completion_cntr, 1, &tmp); 
      
      delete buf;      
      return (Array<T, RANK>*) ((char*) GlobalSMAlloc->addrTable (target) + GlobalSMAlloc->prev_offset());
    }
  else    
    return makeArrayLocal<T, RANK, REGION, DIST> (region, dist);  
}


template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArray (const Region<RANK>* region, const Dist<RANK>* dist)
{
  //create the array locally
  Array<T, RANK>* ret = makeArrayLocal<T, RANK, REGION, DIST> (region, dist);
  
  //create the array remotely
  for (x10_place_t target = 0; target < __x10_num_places; target++)    
    if (target != __x10_my_place)
      makeArrayRemote<T, RANK, REGION, DIST> (region, dist, target); 
  
  return ret;
}

template <typename T, int RANK>
void 
Array <T, RANK> :: putLocalElementAt (const Point<RANK>& p, const T& val) 
{

  assert (false);

  const Point<RANK> index = region_->indexOf (p);

  const Region<RANK>* r = region_->regionAt (index);

  assert (r->contains(p));

  assert (this->dist_->place(index) == __x10_my_place);
 
  int n = r->ord(p);
  
  putLocalElementAt (n, val);
}

template <typename T, int RANK>
void 
Array <T, RANK> :: putLocalElementAt (const uint64_t n, const T& val)
{
  assert (data_ != NULL);

  assert (n >=0 && n < this->region_->regionAt(Point<RANK>(0))->card());

    data_[n] = val;
}

template <typename T, int RANK>
void 
Array<T, RANK> :: putRemoteElementAt (const Point<RANK>& p, const T& val)
{
  const Point<RANK> index =  this->region_->indexOf (p);
  int target = this->dist_->place(index);

  const Region<RANK>* r = region_->regionAt (index);

  uint64_t n = r->ord (p);

  if (target == __x10_my_place) {
    putLocalElementAt (n, val);
  } else {
    assert (n >=0 && n < this->region_->totalCard());
    assert (target >=0 && target < __x10_num_places);
    assert (target != __x10_my_place);

    uint64_t offset = (char*) this->raw() - GlobalSMAlloc->addr() + n; 
    char* buf =  new char [sizeof(uint64_t) + sizeof(T)];
    memcpy (buf, &offset, sizeof(uint64_t));
    memcpy (buf+sizeof(uint64_t), &val, sizeof(T));

    lapi_cntr_t origin_cntr;

    int tmp;

    lapi_header_t array_update_handler = (lapi_header_t) arrayElementUpdate<T>;
    LAPI_Addr_set (__x10_hndl, (void*) array_update_handler, ARRAY_ELEMENT_UPDATE_HANDLER);

    LAPI_Setcntr (__x10_hndl, &origin_cntr, 0);

    LAPI_Amsend (__x10_hndl, 
               target,
               (void*) ARRAY_ELEMENT_UPDATE_HANDLER,
               buf,
               sizeof (T) + sizeof(uint64_t), 
               NULL,
               0,
               NULL,
               &origin_cntr, 
               NULL);

    LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp);

    delete [] buf;

  }
}

template <typename T, int RANK>
T
Array<T, RANK> :: getRemoteElementAt (Point<RANK> p) const
{
  assert (dist_);
  
  x10_place_t target = dist_->place (p);
  
  uint64_t offset = (char*) this->raw() - GlobalSMAlloc->addr(); 

  char* remoteBuf = (char*) GlobalSMAlloc->addrTable (target) + 
    offset + sizeof(T) * region_->ord(p);
  
  T buf;
  
  lapi_cntr_t origin_cntr;
  
  int tmp;
  
  LAPI_Setcntr (__x10_hndl, &origin_cntr, 0);

  LAPI_Get (__x10_hndl,
	    target,
	    sizeof (T),
	    remoteBuf,
	    (void*) &buf,
	    NULL,
	    &origin_cntr);
  
  LAPI_Waitcntr (__x10_hndl, &origin_cntr, 1, &tmp);    

  return buf;
}

template<typename T, int RANK>
T&
Array <T, RANK> :: getLocalElementAt (const Point<RANK>& p) const
{

  //  const Point<RANK> index = region_->indexOf (p);


  //const Region<RANK>* r = region_->regionAt (index);


  //assert (r->contains(p));

  //assert (this->dist_->place(index) == __x10_my_place);

  
  uint64_t n = region_->ord(p);

  
  T& ret = getLocalElementAt (n);
 

 return ret;
}

template <typename T, int RANK>
T& 
Array <T, RANK> :: getLocalElementAt (const uint64_t n) const
{
  assert (data_ != NULL);

  assert (n >=0 && n < this->region_->totalCard());
 
  return data_[n];
}



template <typename T, int RANK>
Array<T, RANK>& 
Array <T, RANK> :: view (const Region<RANK>& subRegion) const
{
  assert (false);
}


//initialization routines
template <typename T, int RANK, typename CONST_INIT>
void 
initialize (Array<T, RANK>& arr, CONST_INIT op)
{
  assert (false);
}

template <typename T, int RANK, template <int N> class POINT_INIT> 
void initialize (Array<T, RANK>& arr, POINT_INIT<RANK> op);

//pointwise routines for standard operators
template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP& op)
{
  assert (false);
}

template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP (&op)[N-1])
{
  assert (false);
}

//reduce
template <typename T, int RANK, typename SCALAR_OP>
T reduce (Array<T, RANK> &arg, int dim, const SCALAR_OP& op)
{
  assert (false);
}

//scan
template <typename T, int RANK, typename SCALAR_OP>
Array<T, RANK> scan (Array<T, RANK> &arg, int dim, const SCALAR_OP& op)
{
  assert (false);
}

//restriction
template <typename T, int RANK>
Array<T, RANK>& restriction (const Dist<RANK>& R)
{
  assert (false);
}


//assembling
template <typename T, int RANK>
Array<T, RANK>& assemble (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}

template <typename T, int RANK>
Array<T, RANK>& overlay (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}

template <typename T, int RANK>
Array<T, RANK>& update (const Array<T, RANK>& a1, const Array<T, RANK>& a2)
{
  assert (false);
}
}

// Local Variables:
// mode: C++
// End:
