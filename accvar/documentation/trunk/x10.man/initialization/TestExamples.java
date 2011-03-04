public class TestExamples {	
  public static void main(String[] args) {
    B b = new B(); // prints: a=42,b=0
    System.out.println(b); // prints: a=42,b=2
  }
}

class A {
  static HashSet INSTANCES = new HashSet();
  final int a;
  A() {
    a = initA(); // dynamic dispatch!
    System.out.println(toString()); //again!
    INSTANCES.add(this); // leakage!
  }
  int initA() {
    return 1;
  }
  public String toString() {
    return "a="+a;
  }
}
class B extends A {
  int b = 2;
  B() { super(); }
  int initA() {
    return b+42;
  }
  public String toString() {
    return super.toString()+",b="+b;
  }
    
  void threadExample() {     
    final String name = "AB".substring(1);
    new Thread() {
      public void run() {
        System.out.println(name);
      }
    }.run();
  }
}