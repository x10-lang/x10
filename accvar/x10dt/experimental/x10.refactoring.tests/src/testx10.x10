public class testx10 {

public testx10() { }

void main(){
int testparam = 0;
final IntBox z = new IntBox(new java.lang.Integer(testparam));
java.lang.Integer zval = z.value;
final IntBox y = /*z;//*/ new IntBox(zval);
async (here) {z.value = new java.lang.Integer(1); };
//async (here) {z.value = y.getValue(y); };
while(true) {
z.value=IntBox.getValue(y);//IntBox.getValue(y);
testparam += 1;
y.value=new java.lang.Integer(testparam);
}
}
}
