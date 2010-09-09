/**
 * 
 */
package x10.effects.constraints;

/**
 * An enum used to mark the safety status of a node.
 * SAFE: This node behaves the same as its serial elision and completes before it returns.
 * PAR_SAFE: THis node behaves the same as its serial elision but may continue executing even after it returns. (e.g. async)
 * UNSAFE: This node does not behave in the same way as it serial elision.
 * @author vj
 *
 */
public enum Safety {
	SAFE {
		@Override
		public Safety lub(Safety s) {
			return s;
		}
		@Override
		public String toString() {
			return "safe";
		}
	},
	PAR_SAFE {
		@Override
		public Safety lub(Safety s) {
			switch (s) {
			case SAFE : return PAR_SAFE;
			case PAR_SAFE : return PAR_SAFE;
			default : return UNSAFE;
			}
		}
		@Override
		public String toString() {
			return "parSafe";
		}
	}, 
	UNSAFE {
		@Override
		public Safety lub(Safety s) {
			return UNSAFE;
		}
		@Override
		public String toString() {
			return "unsafe";
		}
	};
	abstract public Safety lub(Safety s);
}
