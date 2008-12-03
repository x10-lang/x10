/*
 * (c) Copyright IBM Corporation 2007
 * $Id: recv_cc.cc,v 1.1 2007-06-27 14:48:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Example Program using the x10lib::NbPut() api call.
 ** For a set of n places 0, 1, ..., n-1, where n is an
 ** even number, all places are divided into (src,tgt) buddy
 ** pairs (0,1), (2,3), etc.
 ** For each pair, an array of ints is transferred
 ** from src to tgt with a x10lib::NbPut() call.
 ** The various data buffers are printed along the way to
 ** show progress.
 ** [C++ Lang Version]
 **/

#include <x10/x10lib.h>
#include <iostream>

using namespace std;

/* macro for checking X10Lib's return code */
#define XRC(statement) \
do { \
	x10_err_t xrc; \
	char err_msg_buf[X10_MAX_ERR_STRING]; \
	xrc = statement; \
	if (xrc != X10_OK) { \
		(void)x10lib::ErrMsg(xrc, err_msg_buf, X10_MAX_ERR_STRING); \
		cerr << "[" << argv[0] << "]: " << err_msg_buf << endl; \
		x10lib::Cleanup(); \
		exit(1); \
	} \
} while (0)

/* constant for array lengths */
#define ARRAYLEN 10

/* store initial value on src and final value on tgt */
int data_buffer[ARRAYLEN];

/* store list of remote buffer addrs */
x10_gas_ref_t *data_buffer_list;

/* updates on tgt at msg completion */
x10_switch_t tgt_switch;

int
main(int argc, char *argv[])
{
	int my_place; /* Our place ID */
	int num_places; /* Total number of places */
	int i, j;
	int buddy;

	/* Initialize X10Lib */
	XRC(x10lib::Init(NULL, 0));

	/* Get our place ID */
	XRC(x10lib::Getenv(PLACE_ID, &my_place));

	/* Get the total number of places in the job */
	XRC(x10lib::Getenv(NUM_PLACES, &num_places));

	/* This examples only supports even number of places */
	if ((num_places < 2) || ((num_places % 2) != 0)) {
		cerr << "[" << argv[0] << "]: "
			<< "this example requires an even number of places,"
			<< " but has been invoked with" << num_places << endl;
		XRC(x10lib::Finalize());
		exit(1);
	}

	/* allocate memory for remote addrs */
	data_buffer_list = (x10_gas_ref_t *)new x10_gas_ref_t [num_places];
	tgt_switch = x10lib::AllocSwitch();

	/* for each place, get the remote addr and store it */
	for (i = 0; i < num_places; i++) {
		data_buffer_list[i] = x10lib::MakeGASRef(i,
									(void *)data_buffer);
	}

	/**
	 ** up to this point, all instructions have executed on all
	 ** places. we now begin differentiating places.
	 **/
	if (my_place % 2 == 0) { /* sender */
		buddy = my_place + 1;

		/* initialize data buffer */
		for (i = 0; i < ARRAYLEN; i++) {
			data_buffer[i] = i * i;
		}

		cout << "place " << my_place << " ==>" << endl;
		for (i = 0; i < ARRAYLEN; i++) {
			cout << "\tsrc data_buffer[" << i
				<< "]: " << data_buffer[i] << endl;
		}

		/* sync before starting data transfer */
		x10lib::SyncGlobal();

		/* execute the data transfer to our buddy place.
		 * send ARRAYLEN ints, starting with data_buffer[0].
		 * args: buddy -- the target place id
		 *       ARRAYLEN * sizeof(int) -- the length of data
		 *                                 to transfer
		 *       data_buffer_list[buddy] -- remote addr for
		 *                                  writing data
		 *       &(data_buffer[0]) -- the starting address
		 *                            of data to transfer
		 *       tgt_switch -- 		tgt switch
		 *                                 address.  will update
		 *                                 when message completes
		 *                                 on the target.
		 */
		XRC(x10lib::NbPut(&(data_buffer[0]),
				data_buffer_list[buddy], ARRAYLEN * sizeof(int),
				tgt_switch));

		/* wait on completion of the data transfer */
		tgt_switch->next();

		/* to sync with buddy */
		x10lib::SyncGlobal();

	} else { /* receiver */
		/* match src's sync */
		x10lib::SyncGlobal();

		/* wait for data to arrive from src */
		x10lib::SyncGlobal(); /* fix for the moment */

		/* display received data */
		cout << "place " << my_place << " ==>" << endl;
		for (i = 0; i < ARRAYLEN; i++) {
			cout << "\tdest data_buffer[" << i
				<< "]: " << data_buffer[i] << endl;
		}
	}

	/* all places will execute this before term */
	x10lib::SyncGlobal();

	/* cleanup */
	delete [] data_buffer_list;
	x10lib::FreeSwitch(tgt_switch);

	/* terminate the X10Lib context */
	XRC(x10lib::Finalize());
	return 0;
}
