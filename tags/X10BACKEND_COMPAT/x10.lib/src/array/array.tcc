/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: array.tcc,v 1.7 2007-05-31 11:25:57 ganeshvb Exp $ */

#include "array.h"
#include <x10/alloc.h>
#include "x10/handlers.h"
#include "x10/gas.h"
#include "x10/xassert.h"

using namespace x10lib;

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
template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArrayLocal (const Region<RANK>* region, const Dist<RANK>* dist)
{  
  assert (GlobalSMAlloc);

  uint64_t local_size = region->card() / numPlaces();

  void * addrTable[numPlaces()];
  for (place_t p = 0; p < numPlaces(); p++)
      addrTable[p] = GlobalSMAlloc->addrTable (p);
  void* arraySpace = x10lib::GlobalSMAlloc->chunk (sizeof(Array<T, RANK>));

  T* data = (T*) x10lib::GlobalSMAlloc->chunk (local_size * sizeof(T));
  Array<T, RANK>* ret = new(arraySpace) Array<T, RANK>(region, dist, local_size, data, addrTable);

  return ret;
}

template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
void
makeArrayRemote (const Region<RANK>* region, const Dist<RANK>* dist)
{

 unsigned int buf_size = sizeof (metaDataDescr<RANK, REGION, DIST>); 
 metaDataDescr<RANK, REGION, DIST>* buf = new metaDataDescr<RANK, REGION, DIST>;
 memcpy (&(buf->region), region, sizeof(REGION<RANK>));
 
 void_func_t construction_handler = (void_func_t) arrayConstructionGlobalSM <T, RANK, REGION, DIST>;

 lapi_cntr_t completion_cntr;
 int tmp;

 LAPI_Setcntr (GetHandle(), &completion_cntr, 0);

 for (place_t target = 0; target < numPlaces(); target++)
   if (target != here())
     LAPI_Amsend (GetHandle(), 
               target,
               construction_handler, 
               NULL,
               0,
               buf, 
               buf_size,
               NULL,
               NULL,
               &completion_cntr);
  LAPI_Waitcntr (GetHandle(), &completion_cntr, numPlaces() - 1, &tmp); 

  delete buf;

}

template <typename T, int RANK, template <int N> class REGION, template <int N> class DIST>
Array<T, RANK>*
makeArray (const Region<RANK>* region, const Dist<RANK>* dist)
{
  Array<T, RANK>* ret = makeArrayLocal<T, RANK, REGION, DIST> (region, dist);
  makeArrayRemote<T, RANK, REGION, DIST> (region, dist); 
  return ret;
}

template <typename T, int RANK>
void 
Array <T, RANK> :: putElementAt (const Point<RANK>& p, const T& val) 
{

  assert (false);

  const Point<RANK> index = region_->indexOf (p);

  const Region<RANK>* r = region_->regionAt (index);

  assert (r->contains(p));

  assert (this->dist_->place(index) == here());
 
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
Array<T, RANK> :: putElementAtRemote (const Point<RANK>& p, const T& val)
{
  const Point<RANK> index =  this->region_->indexOf (p);
  int target = this->dist_->place(index);

  const Region<RANK>* r = region_->regionAt (index);

  uint64_t n = r->ord (p);

  if (target == here()) {
    putLocalElementAt (n, val);
  } else {
    assert (n >=0 && n < this->region_->totalCard());
    assert (target >=0 && target < numPlaces());
    assert (target != here());

    uint64_t offset = (char*) this->raw() - GlobalSMAlloc->addr() + n; 
    char* buf =  new char [sizeof(uint64_t) + sizeof(T)];
    memcpy (buf, &offset, sizeof(uint64_t));
    memcpy (buf+sizeof(uint64_t), &val, sizeof(T));

    lapi_cntr_t origin_cntr;

    int tmp;

    LAPI_Setcntr (GetHandle(), &origin_cntr, 0);

    LAPI_Amsend (GetHandle(), 
               target,
               arrayElementUpdate<T>,
               buf,
               sizeof (T) + sizeof(uint64_t), 
               NULL,
               0,
               NULL,
               &origin_cntr, 
               NULL);

    LAPI_Waitcntr (GetHandle(), &origin_cntr, 1, &tmp);

    delete [] buf;

  }
}

template<typename T, int RANK>
T&
Array <T, RANK> :: getElementAt (const Point<RANK>& p) const
{
  assert (false);
  const Point<RANK> index = region_->indexOf (p);

  const Region<RANK>* r = region_->regionAt (index);

  assert (r->contains(p));

  assert (this->dist_->place(index) == here());

  uint64_t n = r->ord(p);
  
  return getLocalElementAt (n);
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

// Local Variables:
// mode: C++
// End:
