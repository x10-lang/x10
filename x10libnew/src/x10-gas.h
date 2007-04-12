#ifndef X10GAS_H_
#define X10GAS_H_

typedef uint32_t X10_place_t;
typedef uint64 x10_gas_ref_t;

/**
 * Does the ref point to a location homed on the current node?
 */
extern boolean X10_gas_islocal(x10_gas_ref_t* ref);
/**
 * Return the node id for the current node.
 */
extern x10_place_t X10_gas_here()

#endif /*X10GAS_H_*/
