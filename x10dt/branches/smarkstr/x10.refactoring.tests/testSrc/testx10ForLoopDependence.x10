public class testx10ForLoopDependence {

    public testx10ForLoopDependence() { }

    void main(){
        final IntBox[.] z = new IntBox[[0:4]];
        z[0] = new IntBox(new java.lang.Integer(0));
        z[1] = new IntBox(new java.lang.Integer(1));
        java.lang.Integer zval = z[0].value;
        final IntBox y = new IntBox(zval);
        for(IntBox zsub : z) {
            y.value = zval;
            zsub.value=y.value;
            zval = new java.lang.Integer(zval.intValue()+1);
        }
    }
}