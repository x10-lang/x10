#ifndef __X10_ACTS_H__
#define __X10_ACTS_H__

#include <stdio.h>
#include <sys/types.h>
#include <stdint.h>

#ifdef __cplusplus
#define EXTERN extern "C"
#else
#define EXTERN 
#endif

#ifndef __cplusplus
#define bool char
#endif

/* primitive types */
typedef bool     x10_boolean_t;
typedef int8_t   x10_byte_t;
typedef uint16_t x10_char_t;
typedef int16_t  x10_short_t;
typedef int32_t  x10_int_t;
typedef long     x10_long_t;
typedef float    x10_float_t;
typedef double   x10_double_t;

/* address type */
typedef unsigned char* x10_addr_t;

/*	place  type	*/
typedef x10_long_t	x10_place_t;

/* error type */
typedef enum {X10_OK, X10_NOT_OK} x10_err_t;

typedef struct
{
  x10_long_t finish_id; /* a dynamic UNIQUE number for every finish scope */
  x10_place_t finish_root; /* root of the async */
} x10_finish_record_t;

typedef struct
{
  x10_long_t handler;
} x10_async_closure_t;

typedef void* x10_comm_handle_t;

/*	clock	*/
typedef struct
{
   /* not defined yet */
} x10_clock_t;

typedef struct
{
  x10_place_t loc;
  x10_addr_t addr;
} x10_remote_ref_t;

/*	condition variables	*/
typedef  unsigned 	x10_condition_t;
  
/* init/finalize */

EXTERN x10_err_t
x10_init();

EXTERN x10_err_t
x10_finalize();

EXTERN x10_place_t
x10_nplaces();

EXTERN x10_place_t
x10_here();

/* async */

/**
 * \brief spawn an async on given target (NON-BLOCKING).
 *         x10lib assumes that code (atleast the __x10_callback_asynswitch) is replicated everywhere 
 
 * \param tgt 			target place
 * \param closure               pointer to async closure (see x10_types.h)
 * \param cl_size               size of the async closure
 * \param frecord               pointer to the finish record (see x10_types.h)
 * \param clocks                clock set for the async (see x10_types.h)
 * \param num_clocks            number of clocks in the clock set

 *  \return			handle to wait for
 */

EXTERN x10_comm_handle_t
x10_async_spawn(const x10_place_t tgt, const x10_async_closure_t* closure, const size_t cl_size, 
		const x10_finish_record_t* frecord, const x10_clock_t* clocks, const int num_clocks);

EXTERN x10_err_t
x10_async_spawn_imm(const x10_place_t tgt, const x10_async_closure_t* closure, const size_t cl_size, 
		const x10_finish_record_t* frecord, const x10_clock_t* clocks, const int num_clocks);

/**
 * \brief wait for the async_spawn to complete locally  (BLOCKING)
 
 * \param handle                handle returned by x10_async_spawn (see x10_types.h)

 * \return                       returns an error or success
 */

EXTERN x10_err_t
x10_async_spawn_wait(x10_comm_handle_t handle);

/**
 * \brief check for any asyncs in the internal async queue and execute them.
 *  This method should be used on the receiver side to make progress (NON-BLOCKING)
 */
EXTERN x10_err_t
x10_probe();

/**
 * \brief performs x10_probe infinitely, until a termination message is received(BLOCKING)
 */
EXTERN x10_err_t
x10_wait();

/**
 * \brief  array put(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_array_put(const x10_addr_t src, 
	      const x10_addr_t dst, const size_t offset, const size_t nbytes);

/**
 * \brief array get(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_array_get(const x10_addr_t dst, 
	      const x10_addr_t src, const size_t offset, const size_t nbytes);

/**
 * \brief async array put(NON-BLOCKING)
 */
EXTERN x10_comm_handle_t
x10_async_array_put(const x10_place_t tgt, const x10_addr_t src, const size_t nbytes,
		    const x10_async_closure_t* dst_closure, const size_t dst_cl_size);

/* finish */

EXTERN x10_finish_record_t*
x10_get_cur_frecord();

#define X10_FINISH_BEGIN \
{\
  x10_finish_record_t* __x10_prev_finish_record;\
  x10_finish_begin(__x10_prev_finish_record, NULL, NULL, 0, 0);					

#define X10_FINISH_BEGIN_GLOBAL \
{\
  x10_finish_record_t* __x10_prev_finish_record;\
  x10_finish_begin_global(__x10_prev_finish_record, NULL, NULL, 0, 0);

#define X10_FINISH_END\
  int __x10_tmp;\
  x10_finish_end(__x10_prev_finish_record, &__x10_tmp);\
}

/**
 * \brief start the finish_scope (called by root activity only)

 * \param frecord	           the current finish record (output)

 * \param multi_ex_buf             buffer for the resulting multi_exceptions (if any)
 * \param ex_offsets		   offsets array for individual exceptions 
 * \param max_ex_buf_size          maximum size of the multi_ex_buf 
 * \param max_num_exceptions       maximum number of individual exceptions 
 */

EXTERN x10_err_t
x10_finish_begin(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);

EXTERN x10_err_t
x10_finish_begin_global(x10_finish_record_t* frecord, void* mult_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);

/**
 * \brief end the finish_scope (called by root activity only).
                       Waits for global termination of all the activities (BLOCKING)

 * \param finish_record           the saved finish record, returned by the lexically preceeding finish_begin[_global]
 * \param  num_exceptions         total number of exceptions
 */
EXTERN x10_err_t
x10_finish_end(const x10_finish_record_t* finish_record, int* num_exceptions);

/**
 * \brief notify the "root" that I have finished (called by children activity only)

 * \param frecord 		current finish record 
 * \param ex_buf		exception buffer 
 * \param ex_buf_size		size of the exception buffer
 */
EXTERN x10_err_t
x10_finish_child(const x10_finish_record_t* frecord, void* ex_buf, int ex_buf_size);

/* clocks  */

/**
 * \brief initialize a clock c (see x10_types.h for x10_clock_t)
 */
EXTERN x10_err_t
x10_clock_init(x10_clock_t* c);

EXTERN x10_err_t
x10_clock_free (x10_clock_t* c);

/**
 * \brief perform a resume operation on clock c
 */
EXTERN x10_err_t
x10_clock_resume(x10_clock_t* c);

/**
 * \brief drop a clock c
 */
EXTERN x10_err_t
x10_clock_drop(x10_clock_t* c);

/**
 * \brief perform a next operation
 */
EXTERN x10_err_t
x10_next(x10_clock_t* c);

EXTERN x10_err_t
x10_next_all();

/* remote reference  */

/**
 * \brief serialize a reference (local or remote)
 */
EXTERN x10_remote_ref_t 
x10_ref_serialize(x10_addr_t ref);

/**
 * \brief deserialize a remote reference
 */
EXTERN x10_addr_t
x10_ref_deserialize(x10_remote_ref_t ref);

/**
 *\brief get the location of a reference
 */
EXTERN x10_place_t
x10_ref_get_loc(x10_addr_t ref);

/**
 * \brief check if the reference is local
 */
EXTERN bool
x10_ref_is_local (x10_addr_t ref);


EXTERN x10_addr_t
x10_ref_get_addr (x10_addr_t ref);

EXTERN bool
x10_ref_is_equal(x10_addr_t ref1, x10_addr_t ref2);

#if defined(__cplusplus) 

namespace X10 { 
  
  typedef x10_addr_t Addr;
  
  typedef x10_err_t Err;
  
  typedef x10_finish_record_t FinishRecord;
  
  typedef x10_comm_handle_t CommHandle;
  
  typedef x10_clock_t Clock;
      
  typedef x10_condition_t Condition;  

  class RemoteRef;

  class Place;

  class Acts;
  
  Err Init();
  
  Err Finalize();
    
  class AsyncClosure
  {

  public:

    AsyncClosure();
    
    AsyncClosure(x10_async_closure_t handler);
    
    operator x10_async_closure_t() const;    
    
    AsyncClosure(const AsyncClosure& closure);

    x10_async_closure_t handler() const;
    
  private:
    
    x10_async_closure_t __handler;
  };

  class RemoteRef
  {         
  public:
    
    RemoteRef();
    
    RemoteRef(x10_remote_ref_t ref);
    
    operator x10_remote_ref_t() const;
      
    RemoteRef(const RemoteRef& ret);
    
    Addr GetAddr() const;
    
    Place GetLoc() const;
    
    void SetLoc (Place p);
    
    void SetAddr (Addr a);
    
  private:
    
    x10_remote_ref_t __ref;
    
  public :
      
    static RemoteRef Serialize(Addr ref);
    
    static Addr Deserialize(RemoteRef);
    
    static Place GetLoc(Addr ref);
    
    static bool IsLocal(Addr ref);
    
    static Addr GetAddr(Addr ref);
    
    static bool IsEqual(Addr ref1, Addr ref2);
  };
  
  class Place
  {      
  public:
    
    Place(x10_place_t id);
    
    operator x10_place_t() const;      
    
    operator x10_place_t() ;
    
    Place(const Place& p);
    
    const Place& operator=(const Place& p);
    
    const bool operator== (const Place& p) const;
    
  private:      
    
    x10_place_t __id;      
    
  };
  
  static x10_place_t Nplaces();
  
  static Place Here();      
  
  class Acts
    {      

    public:
      
      static Err Probe();

      static Err Wait();	    
      
      static CommHandle AsyncSpawn(const Place tgt, const AsyncClosure* closure, const size_t cl_size, const FinishRecord* frecord, const Clock* clocks, const int numClocks);
      
      static Err AsyncSpawnWait(const CommHandle handle);

      static x10_finish_record_t* GetCurFrecord();
      
      static Err FinishChild(const FinishRecord* frecord, void* ex_buf, int ex_buf_size);
      
      static Err FinishBegin(FinishRecord* frecord, void* multi_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);    

      static Err FinishBeginGlobal(FinishRecord* frecord, void* multi_ex_buf, int* ex_offsets, int max_ex_buf_size, int max_num_exceptions);      
      static Err  FinishEnd(const FinishRecord* frecord, int* num_exceptions);	 
    };  
}

#endif

#endif

// Local Variables:
// mode: C++
// End:
