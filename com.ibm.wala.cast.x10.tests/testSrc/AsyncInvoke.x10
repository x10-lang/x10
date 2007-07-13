public class AsyncInvoke {
  void main(){
    final IntBox z = new IntBox(0);
    final IntBox y = z;
    async (here) {z.value = y.getValue(y) };
    while(true)
      z.value=y.getValue(y);
  }
  class IntBox {
    public java.lang.Integer value;
    public IntBox(int value) { this.value = new java.lang.Integer(value); }
    public java.lang.Integer getValue(IntBox x) { return x.value; }
  }
}
