#ifndef x10ARRAY_H_
#define x10ARRAY_H_

#include "x10-dist.h"

namespace x10lib{

typedef enum {ROW_MAJOR, COLUMN_MAJOR, TILE_MAJOR} order_t; 

/** 
  * x10_Array Class
  */
template <typename T, int RANK>
class Array
{
	Array (Dist<RANK>& dist);
	
	Array (Array<T, RANK>& A, Region<RANK>& R);

	Array<T, RANK>* clone();

	Dist<RANK> dist() const;
		
    
    /** The number of elements in the array that are allocated in the current place.
     */
	int localSize() const;
	
	/** A pointer to the local chunk of memory used to store the elements of the array.
	 */
	T* localChunk();
	
        void putScalarAt (const Point<RANK>& P, const T& val);
	
	void putScalarAt (const int n, const T& val);
	
	T& getScalarAt (const Point<RANK>& P) const;
	
	T& getScalarAt (const int n) const;
	
	/** Return the array obtained from this by restricting its region to
	 * this.region intersected with subRegion.
	 */
	Array<T, RANK>& view(const Region<RANK>& subRegion) const;
	
	/** regionMap must be a region transformation, i.e. a tiled region
	 * whose base region and index region are identical. 
	 * Return the array obtained from this by restricting its region to
	 * this.region intersected with regionMap.base, and applying 
	 * regionMap. For instansce, let A be an array defined over the region
	 * [0:1,0:1], and R be the tiled region on [0:0,0:1] that
	 * maps [0,0] to [0,0] and [1,1] to [1,1] and [0,1] to [1,0]. Then
	 * A.view(R) is the array B defined over [0:0,0:1] s.t. 
	 * B[0,0] maps to A[0,0], B[0,1] maps to A[1,0] and B[1,1] maps to A[1,1].
	 * Any gets/puts on the view read/modify the underlying array.
	 */
	 Array<T, RANK>& view(const Region<RANK>& regionMap) const;
	
	~Array () { delete [] data_; }
 				
	private:

	T* data_;

	protected:
	
        Dist<RANK> dist_;	
}; 

// Useful for casting a scalar to Array 
// (Immutable)

template <typename T, int RANK>
class UnitArray : public Array<T, RANK>
{
   UnitArray (int value);
	
   T& operator[] (const Point<RANK>& P);
   
   UnitArray<T, RANK>& operator[] (const Region<RANK>& R);

         //the same value is replicated in the array_t
	     //upon a write, create a new data_ for this array_t
};


// vjQ: Why are these methods outside a class?
// Answer: There is no specific reason. Only that all the methods are like iterators and algorithms; writing it like this make them clear and seperate them from 
//   the types. This is something along the lines of STL, but not quite close to it. I can put these in to Array class, if you want. 

//initialization routines
template <typename T, int RANK, typename CONST_INIT>
void initialize (Array<T, RANK>& arr, CONST_INIT op);


template <typename T, int RANK, template <int RANK> class POINT_INIT> 
void initialize (Array<T, RANK>& arr, POINT_INIT<RANK> op);

//pointwise routines for standard operators
template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP& op);

template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (Array<T, RANK> (&args) [N], order_t order,  const SCALAR_OP (&op)[N-1]);

//reduce
template <typename T, int RANK, typename SCALAR_OP>
T reduce (Array<T, RANK> &arg, int dim, const SCALAR_OP& op);

//scan
template <typename T, int RANK, typename SCALAR_OP>
Array<T, RANK> scan (Array<T, RANK> &arg, int dim, const SCALAR_OP& op);

//restriction
template <typename T, int RANK>
Array<T, RANK>& restriction (const Dist<RANK>& R);	


//assembling
template <typename T, int RANK>
Array<T, RANK>& assemble (const Array<T, RANK>& a1, const Array<T, RANK>& a2);	

template <typename T, int RANK>
Array<T, RANK>& overlay (const Array<T, RANK>& a1, const Array<T, RANK>& a2);	

template <typename T, int RANK>
Array<T, RANK>& update (const Array<T, RANK>& a1, const Array<T, RANK>& a2);	

}
#endif /*X10ARRAY_H*/
