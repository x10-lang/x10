class IntStream {
public final clock clk = clock.factory.clock();

private int[] buf = new int[2];
private int i = 0;
private int j = 1;

public IntStream(final int v) {
buf[0] = v;
}

public void put(final int v) {
//if (!clk.registered()) throw new ClockUseException();
clk.doNext();
i = 1 - i;
buf[i] = v;
clk.resume();
}

public int get() {
//if (!clk.registered()) throw new ClockUseException();
clk.doNext();
j = 1 -j;
final int v = buf[j];
clk.resume();
return v;
}
}


public class ClockPascalTriangle {
static IntStream row(final int n) {
final IntStream row = new IntStream(1);
async clocked(row.clk) {
if (n > 0) {
final IntStream previous = row(n - 1);
int v;
int w = previous.get();
while (w != 0) {
v = w;
w = previous.get();
row.put(v + w);
}
}
row.put(0);
}
return row;
}

public static void main(String[] args){
final IntStream row = row(10);
int w = row.get();
while (w != 0) {
System.out.println(w);
w = row.get();
}
}
}