import x10.lang.clock;

public class zz
   {
       public static void main(String args[]) {
           async (place.FIRST_PLACE) {
               System.out.println("xx " + place.MAX_PLACES);
               int i = 0;
               place p = place.FIRST_PLACE;
               do {
                   final int j = i;
                   final clock c = clock.factory.clock();
                   async(p) clocked (c) {
                       final int q = j*10 + 3;
                       System.out.println("zz " + j); System.out.flush();
                       if (j != 0) {
                           async (place.FIRST_PLACE) {
                               System.out.println("aa " +  q); System.out.flush();
                               try {
                                   Thread.sleep(10);
                               } catch (java.lang.Exception e) {
                               }
                               System.out.println("bb " +  q); System.out.flush();
                           }
                       }
                       c.resume();
                       next;
                   }
                   c.drop();
                   next;
                   p = p.next();
		   //System.out.println("i:"+i);
                   ++i;
               } while (i < place.MAX_PLACES);
           }
       }
}
