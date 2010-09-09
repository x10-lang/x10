public class testx10Array {

public testx10Array() { }

void main(){
int testparam = 0;
final IntBox[] z = new IntBox[4];
z[0] = new IntBox(new java.lang.Integer(testparam));
z[1] = new IntBox(new java.lang.Integer(1));
java.lang.Integer zval = z[0].value;
final IntBox y = /*z;//*/ new IntBox(zval);
async (here) {z[1].value = new java.lang.Integer(1); };
//async (here) {z.value = y.getValue(y); };
while(true)
z[0].value=y.value;//IntBox.getValue(y);
}
}
