/*
 * (c) Copyright IBM Corporation 2007
 * $Id: recv_c.c,v 1.3 2007-06-27 14:48:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Example Program using the x10_put api call.
 ** For a set of n places 0, 1, ..., n-1, where n is an
 ** even number, all places are divided into (src,tgt) buddy
 ** pairs (0,1), (2,3), etc.
 ** For each pair, an array of ints is transferred
 ** from src to tgt with a x10_put call.
 ** The various data buffers are printed along the way to
 ** show progress.
 **/

#include <x10/x10lib.h>
#include <stdio.h>
#include <stdlib.h>

/* macro for checking X10Lib's return code */
#define XRC(statement) \
do { \
	x10_err_t xrc; \
	char err_msg_buf[X10_MAX_ERR_STRING]; \
	xrc = statement; \
	if (xrc != X10_OK) { \
		(void)x10_err_msg(xrc, err_msg_buf, X10_MAX_ERR_STRING); \
		fprintf(stderr, "[%s]: %s\n", argv[0], err_msg_buf); \
		x10_cleanup(); \
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
	XRC(x10_init(NULL, 0));

	/* Get our place ID */
	XRC(x10_getenv(PLACE_ID, &my_place));

	/* Get the total number of places in the job */
	XRC(x10_getenv(NUM_PLACES, &num_places));

	/* This examples only supports even number of places */
	if ((num_places < 2) || ((num_places % 2) != 0)) {
		fprintf(stderr, "[%s]: this example requires an even"
			" number of places, but has been invoked with %d\n",
			argv[0], num_places);
		XRC(x10_finalize());
		exit(1);
	}

	/* allocate memory for remote addrs */
	data_buffer_list = (x10_gas_ref_t *)malloc(num_places *
							sizeof(x10_gas_ref_t));
	tgt_switch = x10_alloc_switch();

	/* for each place, get the remote addr and store it */
	for (i = 0; i < num_places; i++) {
		data_buffer_list[i] = x10_make_gas_ref(i,
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

		printf("place %d ==>\n", my_place);
		for (i = 0; i < ARRAYLEN; i++) {
			printf("\tsrc data_buffer[%d]: %d\n",
					i, data_buffer[i]);
		}

		/* sync before starting data transfer */
		x10_sync_global();

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
		XRC(x10_put_nb(&(data_buffer[0]),
				data_buffer_list[buddy], ARRAYLEN * sizeof(int),
				tgt_switch));

		/* wait on completion of the data transfer */
		x10_next_on_switch(tgt_switch);

		/* to sync with buddy */
		x10_sync_global();

	} else { /* receiver */
		/* match src's sync */
		x10_sync_global();

		/* wait for data to arrive from src */
		x10_sync_global(); /* fix for the moment */

		/* display received data */
		printf("place %d ==>\n", my_place);
		for (i = 0; i < ARRAYLEN; i++) {
			printf("\tdest data_buffer[%d]: %d\n",
					i, data_buffer[i]);
		}
	}

	/* all places will execute this before term */
	x10_sync_global();

	/* cleanup */
	free((void *)data_buffer_list);
	x10_free_switch(tgt_switch);

	/* terminate the X10Lib context */
	XRC(x10_finalize());
	return 0;
}
