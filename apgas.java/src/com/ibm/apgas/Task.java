package com.ibm.apgas;

import java.io.Serializable;

import x10.core.fun.VoidFun_0_0;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public abstract class Task implements VoidFun_0_0, Serializable {
    private static final long serialVersionUID = 2504253825456989543L;
   
    public RuntimeType<?> $getRTT() {
        return VoidFun_0_0.$RTT;
    }

    @Override
    public Type<?> $getParam(int i) {
        return null;
    }

    public void $apply() {
        body();
    }
    
    public abstract void body();
}
