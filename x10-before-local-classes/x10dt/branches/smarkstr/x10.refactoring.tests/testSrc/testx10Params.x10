public class testx10Params {
public testx10Params() { }

void main(){
int testparam = 0;
java.lang.Integer testparam2 = new java.lang.Integer(testparam);
final IntBox z = new IntBox(new java.lang.Integer(testparam), testparam2);
java.lang.Integer zval = z.value;
final IntBox y = new IntBox(zval);
async (here) {z.value = y.getValue2(z.value, y); };
while(true)
z.value=y.value;
}
}