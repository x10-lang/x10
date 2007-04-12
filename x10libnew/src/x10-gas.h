#ifndef X10GAS_H_
#define X10GAS_H_

typedef uint32 X10_place_t;
typedef uint64 X10_gas_ref_t;

/**
 * Does the ref point to a location homed on the current node?
 */
extern boolean X10_gas_islocal(X10_gas_ref_t* ref);
/**
 * Return the node id for the current node.
 */
extern X10_place_t X10_gas_here()

#endif /*X10GAS_H_*/
