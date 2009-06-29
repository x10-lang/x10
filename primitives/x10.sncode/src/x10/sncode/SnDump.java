package x10.sncode;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SnDump {
	public static void main(String[] args) {
		try {
			byte[] buf = new byte[4096];

			for (String a : args) {
				FileInputStream in = new FileInputStream(a);

				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int n = 0;

				do {
					n = in.read(buf);
					if (n >= 0)
						out.write(buf, 0, n);
				} while (n >= 0);

				byte[] bytecode = out.toByteArray();

				ByteBuffer b = new ByteBuffer(bytecode);

				SnFile f = new SnFile();
				f.readFrom(b);
				
				System.out.println(a + ":");
				f.dump(System.out);
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		catch (InvalidClassFileException e) {
			System.err.println(e.getMessage());
		}
	}
}
