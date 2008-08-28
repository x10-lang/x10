package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Place")
public value Place(
        @Native("(#0).id()")
        id: nat
    ) {
        /**
         * The number of places in this run of the system.
         * This is set on initialization by x10.runtime.Runtime
         */
        @Native("java", "x10.runtime.Runtime.getMaxPlaces()")
        public const MAX_PLACES: int = getMaxPlaces();

        /** The first place. */
        @Native("java", "x10.runtime.Runtime.getFirstPlace()")
        public const FIRST_PLACE: Place = getFirstPlace();

        @Native("java", "x10.runtime.Runtime.getPlacesRail()")
        public const places: ValRail[Place] = getPlaces();

        /** Stubs to make the type-checker happy. */
        private static incomplete def getMaxPlaces(): int;
        private static incomplete def getFirstPlace(): Place;
        private static incomplete def getPlaces(): ValRail[Place];

        /** Private constructor. */
        private native def this();

        @Native("java", "x10.runtime.Runtime.place(#1)")
        public native static def place(i: nat): Place;

        @Native("java", "(#0).next()")
        public native def next(): Place;

        @Native("java", "(#0).prev()")
        public native def prev(): Place;

        @Native("java", "(#0).next(#1)")
        public native def next(k: int): Place;

        @Native("java", "(#0).prev(#1)")
        public native def prev(k: int): Place;
                
        @Native("java", "(#0).isFirst()")
        public native def isFirst(): Boolean;
                
        @Native("java", "(#0).isLast()")
        public native def isLast(): Boolean;
}
