public class testx10NestedBlocks {

    public testx10NestedBlocks() { }

    void main(){
        final IntBox[.] z = new IntBox[[0:4]];
        z[0] = new IntBox(new java.lang.Integer(0));
        z[1] = new IntBox(new java.lang.Integer(1));
        java.lang.Integer zval = z[0].value;
        final IntBox y = new IntBox(zval);
        for(IntBox zsub : z) {
        	final java.lang.Integer newVal = zval;
        	{
        		zsub.value=newVal;
        	}
            zval = new java.lang.Integer(zval.intValue()+1);
        }
    }
}