/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.cc,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */
 
/** Implementation file for the messaging layer of X10Lib **/

#include <x10/x10lib.h>
#include <iostream>

using namespace std;

/* Initialization */
int
x10lib::Init(x10_async_handler_t *handlers, int n)
{
	/* Code for Initialization */
#ifdef DEBUG
	cout << "x10lib::Init()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Termination */
int
x10lib::Finalize()
{
	/* Code for Termination */
#ifdef DEBUG
	cout << "x10lib::Finalize()" << endl;
#endif /* DEBUG */
	return X10_OK;
}

/* Fetch n contiguous bytes from src address on a remote node to the dest address
 * on the local node
 */
int 
x10lib::Get(x10_gas_ref_t src, void *dest, int n)
{
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
extern "C" int x10_init(x10_async_handler_t *handlers, int n)
{
		return x10lib::Init(handlers, n);
}
	
/* Termination */
extern "C" int x10_finalize()
{
   return x10lib::Finalize();
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

