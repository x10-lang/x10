interface I {
    static void f() { System.out.println("I.f() called"); }
    default void g() { System.out.println("I.g() called"); f(); }
    void h();
}
