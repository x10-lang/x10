package polyglot.ext.x10.types;

import polyglot.types.Flags;

public class X10Flags {

        public static final Flags VALUE        = Flags.createFlag("value", null);
        public static final Flags REFERENCE    = Flags.createFlag("reference", null);
        public static final Flags ATOMIC       = Flags.createFlag("atomic", null);
        public static final Flags PURE         = Flags.createFlag("pure", null);
        public static final Flags MUTABLE      = Flags.createFlag("mutable", null);
        /**
             * Return a copy of this <code>this</code> with the <code>value</code>
             * flag set.
         * @param flags TODO
             */
            public static Flags Value(Flags flags) {
        	return flags.set(VALUE);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>value</code>
             * flag clear.
         * @param flags TODO
             */
            public static Flags clearValue(Flags flags) {
        	return flags.clear(VALUE);
            }
        /**
             * Return true if <code>this</code> has the <code>value</code> flag set.
         * @param flags TODO
             */
            public static boolean isValue(Flags flags) {
        	return flags.contains(VALUE);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>reference</code>
             * flag set.
         * @param flags TODO
             */
            public static Flags Reference(Flags flags) {
        	return flags.set(REFERENCE);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>reference</code>
             * flag clear.
         * @param flags TODO
             */
            public static Flags clearReference(Flags flags) {
        	return flags.clear(REFERENCE);
            }
        /**
             * Return true if <code>this</code> has the <code>reference</code> flag set.
         * @param flags TODO
             */
            public static boolean isReference(Flags flags) {
        	return flags.contains(REFERENCE);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>atomic</code>
             * flag set.
         * @param flags TODO
             */
            public static Flags Atomic(Flags flags) {
        	return flags.set(ATOMIC);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>atomic</code>
             * flag clear.
         * @param flags TODO
             */
            public static Flags clearAtomic(Flags flags) {
        	return flags.clear(ATOMIC);
            }
        /**
             * Return true if <code>this</code> has the <code>atomic</code> flag set.
         * @param flags TODO
             */
            public static boolean isAtomic(Flags flags) {
        	return flags.contains(ATOMIC);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>pure</code>
             * flag set.
         * @param flags TODO
             */
            public static Flags Pure(Flags flags) {
        	return flags.set(PURE);
            }
        /**
             * Return a copy of this <code>this</code> with the <code>pure</code>
             * flag clear.
         * @param flags TODO
             */
            public static Flags clearPure(Flags flags) {
        	return flags.clear(PURE);
            }
        /**
             * Return true if <code>this</code> has the <code>pure</code> flag set.
         * @param flags TODO
             */
            public static boolean isPure(Flags flags) {
        	return flags.contains(PURE);
            }

}
