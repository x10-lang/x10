#ifndef X10GAS_H_
#define X10GAS_H_

#include "x10-common.h"

namespace x10lib {

namespace xfer {
/**
 * Does the ref point to a location homed on the current node?
 */

extern bool gas_islocal(gas_ref_t* ref);

/**
 * Return the node id for the current node.
 */
extern place_t gas_here();

}

}

#endif /*X10GAS_H_*/
