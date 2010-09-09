package x10.util;

/** future(P) {e} would be translated to:
    new Runnable() {
      public Latch run() {
         final Latch l = new Latch();
         async ( P ) {
            Object x;
            try {
                finish x = e;
                final Object z = x;
                async ( l ) {
                   l.setValue( z ); 
                }
            } catch (final Exception z) {
               async ( l ) {
                 l.setValue( z );
               }
            }
         }
         return l;
      }
    }.run()

   future T (P) { S }  would be translated to:
    new Runnable() {
      
      public Latch run() {
         final Latch l = new Latch();
         async ( P ) {
            T x;
            try {
                finish x = new Runnable() {
                            public T run() {
                              S
                            }
                           }.run();
                final Object z = x;
                async ( l ) {
                   l.setValue( z ); 
                }
            } catch (final Exception z) {
               async ( l ) {
                 l.setValue( z );
               }
            }
         }
         return l;
      }
    }.run()

   onFinish { S } would be translated to:
    new Runnable() {
      public Latch run() {
         final Latch l = new Latch();
         try {
           finish S;
           l.setValue( true ); 
         } catch (final Exception z) {
            l.setValue( z );
         }
         return l;
      }
    }.run()
     
 */
public class Latch implements future {
    boolean latched = false;
    nullable boxed result = null;
    nullable exception z = null;

    public atomic boolean setValue( nullable Object val ) {
	return setValue( val, null);
    }
    public atomic boolean setValue( nullable exception z ) {
	return setValue( null, z);
    }
    public atomic boolean setValue( nullable Object val, 
                                    nullable exception z ) {
	if ( latched ) return false;
        // this assignment happens only once for a latch.
	this.result = val;
        this.z = z;
        this.latched = true;
	return true;
    }
    public atomic boolean latched() {
	return latched;
    }
    public Object force() {
	when ( latched ) {
            if (z != null) throw z;
	    return result;
	}
    }
}
