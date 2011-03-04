public class testx10ForLoop {

public testx10ForLoop() { }

void main(){
int testparam = 0;
final IntBox[.] z = new IntBox[[0:4]]; 
z[0] = new IntBox(new java.lang.Integer(testparam));
z[1] = new IntBox(new java.lang.Integer(1));
java.lang.Integer zval = z[0].value;
final IntBox y = /*z;//*/ new IntBox(zval);
async (here) {z[0].value = y.getValue2(y.value, z[1]); };
for(point p : z.region) {
z[p].value=y.value;//IntBox.getValue(y);
}
}
}
