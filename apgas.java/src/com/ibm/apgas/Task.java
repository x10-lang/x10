package com.ibm.apgas;

import java.io.Serializable;

public abstract class Task implements Serializable {
    private static final long serialVersionUID = 2504253825456989543L;
    
    public abstract void body();
}
