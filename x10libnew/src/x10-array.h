#ifndef x10ARRAY_H_
#define x10ARRAY_H_

namespace x10lib{

typedef Array& x10_array_t;
/** 
  * x10_array_t Class
  */
template <typename T, int RANK>
class Array<T, RANK>
{
	x10_array_t (x10_dist_t<RANK> dist);
	
	x10_array_t (x10_array_t<T, RANK>& A, x10_region_t<Rank>& R);

	x10_array_t<T, RANK>* clone();

	x10_dist_t<RANK> dist() const;
		
    

    /** The number of elements in the array that are allocated in the current place.
     */
	int localSize() const;
	
	/** A pointer to the local chunk of memory used to store the elements of the array.
	 */
	T* localChunk();
	
    void putScalarAt (const x10_point_t<RANK>& P, T val);
	
	void putScalarAt (int n, T val);
	
	T getScalarAt (const x10_point_t<RANK>& P) const;
	
	T getScalarAt (int n) const;
	
	/** Return the array obtained from this by restricting its region to
	 * this.region intersected with subRegion.
	 */
	x10_array_t view(const x10_region_t<RANK>& subRegion) const;
	
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
	 x10_array_t view(const x10_tiled_region_t<RANK>& regionMap) const;
	
	~x10_array_t(); 
 				
	private:

	T* data_;

	protected:
	
        x10_dist_t <RANK> dist_;	
}; 

// Useful for casting a scalar to x10_array_t 
// (Immutable)

template <typename T, int RANK>
class x10_unit_array_t : public x10_array_t <T, RANK>
{
   x10_unit_array_t (int value);
	
   T& operator[] (const Point<RANK>& P);
   
   x10_unit_array_t<T, RANK>& operator[] (const x10_region_t<RANK>& R);

         //the same value is replicated in the x10_array_t
	     //upon a write, create a new data_ for this x10_array_t
};

// vjQ: Why are these methods outside a class?

//initialization routines
template <typename T, int RANK, typename CONST_INIT>
void initialize (x10_array_t<T, RANK>& arr, CONST_INIT op);

template <typename T, int RANK, typename POINT_INIT> //(check if this is valid)
void initialize (x10_array_t<T, RANK>& arr, POINT_INIT<RANK> op);

//pointwise routines for standard operators
template <typename T, int RANK>
void iterate (x10_array_t<T, RANK>& arr, order_t order, x10_op_t op);

template <typename T, int RANK, int N>
void iterate (x10_array_t<T, RANK> (&args) [N], order_t order, x10_op_t (&op)[N]);

//pointwise routines for "lift"ed operators
template <typename T, int RANK, typename SCALAR_OP>
void iterate (x10_array_t<T, RANK>& arr, order_t order, SCALAR_OP op);

template <typename T, int RANK, int N, typename SCALAR_OP>
void iterate (x10_array_t<T, RANK> (&args) [N], order_t order,  SCALAR_OP op);

//reduce
template <typename T, int RANK-1>
void reduce (x10_array_t<T, RANK> &arg, int dim, x10_op_t op);

//scan
template <typename T, int RANK-1>
void scan (x10_array_t<T, RANK> &arg, int dim, x10_op_t op);


//restriction
x10_array_t<T, RANK>& restriction (const x10_dist_t<RANK>& R);	


//assembling

x10_array_t<T, RANK>& assemble (const x10_array_t<T, RANK>& a1, const x10_array_t<T, RANK>& a2);	

x10_array_t<T, RANK>& overlay (const x10_array_t<T, RANK>& a1, const x10_array_t<T, RANK>& a2);	

x10_array_t<T, RANK>& update (const x10_array_t<T, RANK>& a1, const x10_array_t<T, RANK>& a2);	

}
#endif /*X10ARRAY_H*/
