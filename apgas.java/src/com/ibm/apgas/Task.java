package com.ibm.apgas;

import x10.core.fun.VoidFun_0_0;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class Task implements VoidFun_0_0 {
    private static final long serialVersionUID = 2504253825456989543L;

    private final Runnable body;

    public Task(Runnable r) {
        body = r;
    }
    
    public RuntimeType<?> getRTT() {
        return VoidFun_0_0._RTT;
    }

    @Override
    public Type<?> getParam(int i) {
        return null;
    }

    public void apply() {
        body.run();
    }

}
