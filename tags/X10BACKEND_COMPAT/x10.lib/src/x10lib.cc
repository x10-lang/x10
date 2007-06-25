/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.cc,v 1.10 2007-06-21 14:24:26 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */
 
/** Implementation file for the messaging layer of X10Lib **/

#include <lapi.h>
#include <iostream>
#include <x10/x10lib.h>
#include <x10/alloc.h>

#ifdef STANDALONE
#include "standalone.h"
#endif

#define LRC(statement) \
do { \
	int rc = statement; \
	if (rc != LAPI_SUCCESS) { \
		return X10_ERR_LAPI; \
	} \
} while (0)

using namespace std;

namespace x10lib {
  lapi_handle_t hndl;
  lapi_info_t info;
  lapi_thread_func_t tf;
  lapi_am_t am;
}

extern error_t asyncRegister();
extern error_t asyncRegisterAgg();
extern error_t finishInit();
extern void finishTerminate();


/* Initialization */
int
x10lib::Init(func_t *handlers, int n)
{  
  memset((void *)&info, 0, sizeof(lapi_info_t));

#ifdef STANDALONE
  initStandAlone (&info);
#endif

  LRC(LAPI_Init(&hndl, &info));
#ifdef DEBUG
  cout << "x10lib::Init()" << endl;
#endif /* DEBUG */

 
  //Set the environment variables
  //(1) switch to POLLING mode
  LAPI_Senv (hndl, INTERRUPT_SET, 0);

  //Set some frequently used variables
  //(1) NUMPLACES
  LAPI_Qenv (hndl, NUM_TASKS, &x10lib::MAX_PLACES);
  
  //(2) ID
  LAPI_Qenv (hndl, TASK_ID, (int*) &x10lib::ID);

  //Intialize various Allocators
  
  //(1) Global Shared Memory Allocator
  x10lib::GlobalSMAlloc = new Allocator (1UL<<10);

  //(2) Local Shared Memory Allocator


  //Set the handlerTable to handlers 
  handlerTable = handlers;

  finishInit();
  asyncRegister();
  asyncRegisterAgg();

  LAPI_Gfence (hndl); 
  return X10_OK;
}

/* Termination */
int
x10lib::Finalize()
{
	LRC(LAPI_Term(hndl));
#ifdef DEBUG
	cout << "x10lib::Finalize()" << endl;
#endif /* DEBUG */
#ifdef STANDALONE
        delete [] info.add_info->add_udp_addrs;
        delete info.add_info;
#endif
        finishTerminate();

        //delete the Global SM Allocator
        delete GlobalSMAlloc;
	return X10_OK;
}

/* Get LAPI handle */
lapi_handle_t
x10lib::GetHandle()
{
	return x10lib::hndl;
}

/* Get environment */
int
x10lib::Getenv(x10_query_t query, int *ret_val)
{
	LRC(LAPI_Qenv(hndl, query, ret_val));
#ifdef DEBUG
	cout << "x10lib::Getenv()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Set environment */
int
x10lib::Setenv(x10_query_t query, int set_val)
{
	LRC(LAPI_Senv(hndl, query, set_val));
#ifdef DEBUG
	cout << "x10lib::Setenv()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Establish shared lock */
int
x10lib::Lock(void)
{
	tf.Util_type = LAPI_GET_THREAD_FUNC;
	LRC(LAPI_Util(hndl, (lapi_util_t *)&tf));
	tf.mutex_lock(hndl);
#ifdef DEBUG
	cout << "x10lib::Lock()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Remove shared lock */
int
x10lib::Unlock(void)
{
	tf.mutex_unlock(hndl);
#ifdef DEBUG
	cout << "x10lib::Unlock()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Enforce global ordering */
int
x10lib::Gfence(void)
{
	LRC(LAPI_Gfence(hndl));
#ifdef DEBUG
	cout << "x10lib::Gfence()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Enforce local ordering */
int
x10lib::Fence(void)
{
	LRC(LAPI_Fence(hndl));
#ifdef DEBUG
	cout << "x10lib::Fence()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Register function address */
int
x10lib::Register(void *addr, int addr_hndl)
{
	LRC(LAPI_Addr_set(hndl, addr, addr_hndl));
#ifdef DEBUG
	cout << "x10lib::Register()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Send an active message */
int
x10lib::Xfer(void *src, x10_gas_ref_t dest, int n, void *hdr_hdl)
{
	unsigned int tgt;

	tgt = GetTaskId(dest);
	LRC(LAPI_Amsend(hndl, tgt, hdr_hdl, NULL, 0, src, n, NULL, NULL, NULL));
#ifdef DEBUG
	cout << "x10lib::Xfer()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Create remote address table */
int
x10lib::AddrInit(void *my_addr, void *addr_tab[])
{
	LRC(LAPI_Address_init(hndl, my_addr, addr_tab));
#ifdef DEBUG
	cout << "x10lib::AddrInit()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Fetch n contiguous bytes from src address on a remote node to the dest address
 * on the local node
 */
int 
x10lib::Get(x10_gas_ref_t src, void *dest, int n)
{
	unsigned int tgt;
	void *tgt_addr;

	tgt = GetTaskId(src);
	tgt_addr = (void *)GetRemoteAddr(src);
	LRC(LAPI_Get(hndl, tgt, n, tgt_addr, dest, NULL, NULL));
	/* Code for Get */
#ifdef DEBUG
	cout << "x10lib::Get()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Non-blocking remote get operation for contiguous bytes */
int
x10lib::NbGet(x10_gas_ref_t src, void *dest, int n, x10_switch_t hndl)
{
	/* Code for NbGet */
#ifdef DEBUG
	cout << "x10lib::NbGet()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

/* Remote get operation(s) for int/long/double/float */
int
x10lib::GetValueInt(x10_gas_ref_t src)
{
	/* Code for GetValueInt */
#ifdef DEBUG
	cout << "x10lib::GetValueInt()" << endl;
#endif /* DEBUG */
	return (int)X10_OK;
}

long
x10lib::GetValueLong(x10_gas_ref_t src)
{
	/* Code for GetValueLong */
#ifdef DEBUG
	cout << "x10lib::GetValueLong()" << endl;
#endif /* DEBUG */
	return (long)X10_OK;
}

double
x10lib::GetValueDouble(x10_gas_ref_t src)
{
	/* Code for GetValueDouble */
#ifdef DEBUG
	cout << "x10lib::GetValueDouble()" << endl;
#endif /* DEBUG */
	return (double)X10_OK;
}

float
x10lib::GetValueFloat(x10_gas_ref_t src)
{
	/* Code for GetValueFloat */
#ifdef DEBUG
	cout << "x10lib::GetVAlueFloat()" << endl;
#endif /* DEBUG */
	return (float)X10_OK;
}

/* Strided get operations */
int
x10lib::GetS(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
		   int count[], int levels)
{
	/* Code for GetS */
#ifdef DEBUG
	cout << "x10lib::GetS()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

int
x10lib::NbGetS(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
		   int count[], int levels, x10_switch_t hndl)
{
	/* Code for NbGetS */
#ifdef DEBUG
	cout << "x10lib::NbGetS()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

/* Generalized I/O vector get operations */
int
x10lib::GetV(x10_giov_ref_t *dsrc, int len)
{
	/* Code for GetV */
#ifdef DEBUG
	cout << "x10lib::GetV()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

int
x10lib::NbGetV(x10_giov_ref_t *dsrc, int len, x10_switch_t hndl)
{
	/* Code for NbGetV */
#ifdef DEBUG
	cout << "x10lib::NbGetV()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

/* Write n contiguous bytes from src address on the local node to the dest address
 * on a remote node
 */
int
x10lib::Put(void *src, x10_gas_ref_t dest, int n)
{
	unsigned int tgt;
	void *tgt_addr;

	tgt = GetTaskId(dest);
	tgt_addr = (void *)GetRemoteAddr(dest);
	LRC(LAPI_Put(hndl, tgt, n, tgt_addr, src, NULL, NULL, NULL));
	/* Code for Put */
#ifdef DEBUG
	cout << "x10lib::Put()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Non-blocking remote put operation for contiguous bytes */
int
x10lib::NbPut(void *src, x10_gas_ref_t dest, int n, x10_switch_t hndl)
{
	/* Code for NbPut */
#ifdef DEBUG
	cout << "x10lib::NbPut()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

/* Remote put operation(s) for int/long/double/float */
int
x10lib::PutInt(int val, x10_gas_ref_t dest)
{
	/* Code for PutInt */
#ifdef DEBUG
	cout << "x10lib::PutInt()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

int
x10lib::PutLong(long val, x10_gas_ref_t dest)
{
	/* Code for PutLong */
#ifdef DEBUG
	cout << "x10lib::PutLong()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

int
x10lib::PutDouble(double val, x10_gas_ref_t dest)
{
	/* Code for PutDouble */
#ifdef DEBUG
	cout << "x10lib::PutDouble()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

int
x10lib::PutFloat(float val, x10_gas_ref_t dest)
{
	/* Code for PutFloat */
#ifdef DEBUG
	cout << "x10lib::PutFloat()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Non-blocking remote put operation(s) for int/long/double/float */
int
x10lib::NbPutInt(int val, x10_gas_ref_t dest, x10_switch_t hndl)
{
	/* Code for NbPutInt */
#ifdef DEBUG
	cout << "x10lib::NbPutInt()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

int
x10lib::NbPutLong(long val, x10_gas_ref_t dest, x10_switch_t hndl)
{
	/* Code for NbPutLong */
#ifdef DEBUG
	cout << "x10lib::NbPutLong()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

int
x10lib::NbPutDouble(double val, x10_gas_ref_t dest, x10_switch_t hndl)
{
	/* Code for NbPutDouble */
#ifdef DEBUG
	cout << "x10lib::NbPutDouble()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}

int
x10lib::NbPutFloat(float val, x10_gas_ref_t dest, x10_switch_t hndl)
{
	/* Code for NbPutFloat */
#ifdef DEBUG
	cout << "x10lib::NbPutFloat()" << endl;
#endif /* DEBUG */
	hndl = 0;
	return X10_OK;
}


/** Wrappers for "C" language interface **/

/* Initialization */
extern "C" int x10_init(func_t *handlers, int n)
{
		return x10lib::Init(handlers, n);
}
	
/* Termination */
extern "C" int x10_finalize()
{
   return x10lib::Finalize();
}

/* Get LAPI handle */
extern "C" lapi_handle_t x10_get_handle()
{
	return x10lib::GetHandle();
}

/* Get environment */
extern "C" int x10_getenv(x10_query_t query, int *ret_val)
{
	return x10lib::Getenv(query, ret_val);
}

/* Set environment */
extern "C" int x10_setenv(x10_query_t query, int set_val)
{
	return x10lib::Setenv(query, set_val);
}

/* Establish shared lock */
extern "C" int x10_lock(void)
{
	return x10lib::Lock();
}

/* Remove shared lock */
extern "C" int x10_unlock(void)
{
	return x10lib::Unlock();
}

/* Enforce global ordering */
extern "C" int x10_gfence(void)
{
	return x10lib::Gfence();
}

/* Enforce local ordering */
extern "C" int x10_fence(void)
{
	return x10lib::Fence();
}

/* Register function address */
extern "C" int x10_register(void *addr, int addr_hndl)
{
	return x10lib::Register(addr, addr_hndl);
}

/* Send an active message */
extern "C" int x10_xfer(void *src, x10_gas_ref_t dest, int n, void *hdr_hdl)
{
	return x10lib::Xfer(src, dest, n, hdr_hdl);
}

/* Create remote address table */
extern "C" int x10_addr_init(void *my_addr, void *addr_tab[])
{
	return x10lib::AddrInit(my_addr, addr_tab);
}

/* Fetch n contiguous bytes from src address on a remote node to the dest address
 * on the local node
 */
extern "C" int x10_get(x10_gas_ref_t src, void *dest, int n)
{
   return x10lib::Get(src, dest, n);
}

/* Non-blocking remote get operation for contiguous bytes */
extern "C" int x10_get_nb(x10_gas_ref_t src, void *dest, int n, x10_switch_t hndl)
{
   return x10lib::NbGet(src, dest, n, hndl);
}

/* Remote get operation(s) for int/long/double/float */
extern "C" int x10_get_value_int(x10_gas_ref_t src)
{
   return x10lib::GetValueInt(src);
}

extern "C" long x10_get_value_long(x10_gas_ref_t src)
{
   return x10lib::GetValueLong(src);
}

extern "C" double x10_get_value_double(x10_gas_ref_t src)
{
   return x10lib::GetValueDouble(src);
}

extern "C" float x10_get_value_float(x10_gas_ref_t src)
{
   return x10lib::GetValueFloat(src);
}


/* Strided get operations */
extern "C" int x10_get_s(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
		   int count[], int levels)
{
   return x10lib::GetS(src, src_stride, dest, dest_stride, count, levels);
}

extern "C" int x10_get_s_nb(x10_gas_ref_t src, int src_stride[], void *dest, int dest_stride[],
		   int count[], int levels, x10_switch_t hndl)
{
   return x10lib::NbGetS(src, src_stride, dest, dest_stride, count, levels, hndl);
}

/* Generalized I/O vector get operations */
extern "C" int x10_get_v(x10_giov_ref_t *dsrc, int len)
{
   return x10lib::GetV(dsrc, len);
}

extern "C" int x10_get_v_nb(x10_giov_ref_t *dsrc, int len, x10_switch_t hndl)
{
   return x10lib::NbGetV(dsrc, len, hndl);
}

/* Write n contiguous bytes from src address on the local node to the dest address
* on a remote node
*/
extern "C" int x10_put(void *src, x10_gas_ref_t dest, int n)
{
   return x10lib::Put(src, dest, n);
}

/* Non-blocking remote put operation for contiguous bytes */
extern "C" int x10_put_nb(void *src, x10_gas_ref_t dest, int n, x10_switch_t hndl)
{
   return x10lib::NbPut(src, dest, n, hndl);
}

/* Remote put operation(s) for int/long/double/float */
extern "C" int x10_put_int(int val, x10_gas_ref_t dest)
{
   return x10lib::PutInt(val, dest);
}

extern "C" int x10_put_long(long val, x10_gas_ref_t dest)
{
   return x10lib::PutLong(val, dest);
}

extern "C" int x10_put_double(double val, x10_gas_ref_t dest)
{
   return x10lib::PutDouble(val, dest);
}

extern "C" int x10_put_float(float val, x10_gas_ref_t dest)
{
   return x10lib::PutFloat(val, dest);
}

/* Non-blocking remote put operation(s) for int/long/double/float */
extern "C" int x10_put_int_nb(int val, x10_gas_ref_t dest, x10_switch_t hndl)
{
   return x10lib::NbPutInt(val, dest, hndl);
}

extern "C" int x10_put_long_nb(long val, x10_gas_ref_t dest, x10_switch_t hndl)
{
   return x10lib::NbPutLong(val, dest, hndl);
}

extern "C" int x10_put_double_nb(double val, x10_gas_ref_t dest, x10_switch_t hndl)
{
   return x10lib::NbPutDouble(val, dest, hndl);
}

extern "C" int x10_put_float_nb(float val, x10_gas_ref_t dest, x10_switch_t hndl)
{
   return x10lib::NbPutFloat(val, dest, hndl);
}
