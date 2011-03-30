class G {
   public static void main(String[] args) {
      new G().tryIt();
   }
   public void nat() {
     System.err.println("here2");
   }
   void tryIt() {
      nat();
   }
} 
