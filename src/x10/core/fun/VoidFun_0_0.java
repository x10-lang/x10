package x10.core.fun;

import x10.types.RuntimeType;

public interface VoidFun_0_0 {
    void apply();

    public static class RTT extends RuntimeType<VoidFun_0_0>{
        public static final RTT it = new RTT();

        public RTT() {
            super(VoidFun_0_0.class);
        }

        @Override
        public boolean instanceof$(Object o) {
            return o instanceof VoidFun_0_0;
        }
    }
}
