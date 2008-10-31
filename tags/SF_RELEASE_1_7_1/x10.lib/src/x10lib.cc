/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10lib.cc,v 1.31 2008-02-15 09:49:27 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */
 
/** Implementation file for X10Lib's initialization. **/

#include <x10/x10lib.h>
#include <stdlib.h>
#include <memory.h>

#include "__x10lib.h__"

namespace x10lib {

/* global data */
/* will subsequently become X10Lib's context */

int __x10_inited;
lapi_handle_t __x10_hndl;
lapi_thread_func_t __x10_tf;
lapi_cntr_t __x10_wait_cntr;

int __x10_num_places;
int __x10_my_place;
int __x10_addr_hndl;
int __x10_addrtbl_sz;

/* aggregation limit */
int __x10_max_agg_size;


/* Initialization */
x10_err_t Init(x10_async_handler_t *hndlrs, int n)
{
	char *envp;
	lapi_info_t info;
	
	/* already inited; give up -- not re-entrant for the moment  */
	if (__x10_inited == 1)
		return X10_ERR_INIT;


	/* query and set the environment variables */
	/* POE/LAPI environ vars that hold default values
	 * if not set explicitly --
	 * MP_EUILIB=ip
	 * MP_PROCS=1
	 */
	if (!getenv("MP_MSG_API"))
		(void)putenv("MP_MSG_API=lapi");
	envp = getenv("X10_USE_SHM");
	if (envp && (strcmp(envp, "yes") == 0))
		(void)putenv("LAPI_USE_SHM=yes");

	envp = getenv("X10_MAX_AGG_SIZE");
	if (envp)
		__x10_max_agg_size = atoi(envp);
	else
		__x10_max_agg_size = 1024;

	X10_DEBUG(1, "AGG_LIMIT " << __x10_max_agg_size);

	/* LAPI initialization */
	(void)memset((void *)&info, 0, sizeof(lapi_info_t));

#ifdef STANDALONE
	InitStandAlone(&info);
#endif

	LRC(LAPI_Init(&__x10_hndl, &info));

	/* set mode of operation (polling or interrupt)
	 * default: polling mode
	 */
	envp = getenv("X10_INTERRUPT_SET");
	if (envp && (strcmp(envp, "yes") == 0))
		(void)LAPI_Senv(__x10_hndl, INTERRUPT_SET, 1);
	else
		(void)LAPI_Senv(__x10_hndl, INTERRUPT_SET, 0);
	
	/* global data initialization */
	(void)LAPI_Qenv(__x10_hndl, TASK_ID, &__x10_my_place);
	(void)LAPI_Qenv(__x10_hndl, NUM_TASKS, &__x10_num_places);
	(void)LAPI_Qenv(__x10_hndl, LOC_ADDRTBL_SZ, &__x10_addrtbl_sz);
	__x10_addr_hndl = 0;
	(void)LAPI_Setcntr(__x10_hndl, &__x10_wait_cntr, 0);
	__x10_tf.Util_type = LAPI_GET_THREAD_FUNC;
	(void)LAPI_Util(__x10_hndl, (lapi_util_t *)&__x10_tf);

	AsyncInit();
	AsyncAggInit();
	AsyncAggSingleInit();
	FinishInit();
	ReduceInit();
	ArrayInit();
	ArrayCopyInit();
        QueueInit();

	LAPI_Gfence(__x10_hndl);

	/* set X10Lib's initialization variable */
	__x10_inited = 1;

#ifdef STANDALONE
       delete [] info.add_info->add_udp_addrs;
       delete info.add_info;
#endif
	return X10_OK;
}

/* Termination */
x10_err_t Finalize(void)
{
	LAPI_Gfence(__x10_hndl);

	/* termination should be preceded by initialization */
	if (!__x10_inited)
		return X10_ERR_INIT;

	/* terminate all the module functionality */
	ReduceFinalize();
	FinishFinalize();
	AsyncAggFinalize();
	AsyncAggSingleFinalize();
  
	/* terminate LAPI context */
	(void)LAPI_Term(__x10_hndl);
  
	/* reset X10Lib's init var */
	__x10_inited = 0;
	return X10_OK;
}

/* Cleanup */
void Cleanup(void)
{
	/* not initialized; nothing to cleanup */
	if (!__x10_inited) return;

	/* terminate LAPI context, if handle is valid */
	if (__x10_hndl)
		(void)LAPI_Term(__x10_hndl);

	/* free up any allocated memory???
	 * instead of this we should have fine grain deallocation
	 * mechanism
	 */
	ReduceFinalize();
	FinishFinalize();
	AsyncAggFinalize();

	/* reset X10Lib's init var */
	__x10_inited = 0;
}

} /* closing brace for namespace x10lib */


/* Initialization */
extern "C"
x10_err_t x10_init(x10_async_handler_t *hndlrs, int n)
{
	return x10lib::Init(hndlrs, n);
}

/* Termination */
extern "C"
x10_err_t x10_finalize(void)
{
	return x10lib::Finalize();

}

/* Cleanup */
extern "C"
void x10_cleanup(void)
{
	return x10lib::Cleanup();
}
