class IntBox {

public java.lang.Integer value;

public IntBox(java.lang.Integer value) { this.value = value; }
public IntBox(java.lang.Integer value1, java.lang.Integer value2) { this.value = value2; }

public static java.lang.Integer getValue(IntBox x) { return x.value; }
public java.lang.Integer getValue2(java.lang.Integer x, IntBox y) { return y.value; }
}