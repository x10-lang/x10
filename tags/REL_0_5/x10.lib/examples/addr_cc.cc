/*
 * (c) Copyright IBM Corporation 2007
 * $Id: addr_cc.cc,v 1.1 2007-06-27 14:48:33 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Example Program to illustrate the use of various X10Lib's
 ** Addressing routines, namely x10lib::RegHandlerAddr(),
 ** x10lib::GetHandlerAddr(), and x10lib::MakeGASRef().
 ** For a set of n places 0, 1, ...., n-1, where n is an even
 ** number, each of the above routines is called, and the
 ** resulting data is printed.
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

/* local pointer whose addresses we will exchange */
void *local_addr0;
void *local_addr1;

/* list of addresses gathered from all places */
void **addr_list;

int
main(int argc, char *argv[])
{
	int my_place; /* Our place ID */
	int num_places; /* Total number of places */
	int i;
	int ah; /* address handle */
	void *haddr; /* handler address */

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

	/* allocate memory for the data addrs */
	addr_list = (void **)new char [sizeof(void *) * num_places];

	/* for each place, get the remote addr and store it */
	for (i = 0; i < num_places; i++) {
		x10_gas_ref_t ref;
		int place;
		void *addr;

		ref = x10lib::MakeGASRef(i, (void *)
				((i % 2 == 0) ? (&local_addr0) : (&local_addr1)));
		place = x10lib::GAS2Place(ref);
		addr = x10lib::GAS2Addr(ref);
		addr_list[place] = addr;
	}

	cout << "place " << my_place << " ==>" << endl;

	/* show local addrss */
	cout << "\taddress of local_addr: "
		<< ((my_place % 2 == 0) ? (&local_addr0) : (&local_addr1))
		<< endl;

	/* show list of remote addresses */
	for (i = 0; i < num_places; i++) {
		cout << "\taddr_list[" << i << "]: "
			<< addr_list[i] << endl;
	}

	/* register handler address and retrieve it */
	ah = x10lib::RegHandlerAddr(
			((my_place % 2 == 0) ? (&local_addr0) : (&local_addr1)));
	cout << "[place " << my_place << "] storing address "
		<< ((my_place % 2 == 0) ? (&local_addr0) : (&local_addr1))
		<< " at position " << ah << "." << endl;

	XRC(x10lib::GetHandlerAddr(&haddr, ah));
	cout << "[place " << my_place << "] retrieved address "
		<< haddr << " from position " << ah << "." << endl;

	/* sync */
	x10lib::SyncGlobal();

	/* free allocated memory */
	delete [] addr_list;

	/* terminate the X10Lib context */
	XRC(x10lib::Finalize());
	return 0;
}
