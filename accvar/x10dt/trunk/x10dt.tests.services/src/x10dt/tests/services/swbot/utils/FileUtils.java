package x10dt.tests.services.swbot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

public class FileUtils {

	private FileUtils() {
	}

	public static final void copy(File source, File destination)
			throws IOException {
		if (source.isDirectory()) {
			copyDirectory(source, destination);
		} else {
			copyFile(source, destination);
		}
	}

	public static final void copyDirectory(File source, File destination)
			throws IOException {
		if (!source.isDirectory()) {
			throw new IllegalArgumentException("Source (" + source.getPath()
					+ ") must be a directory.");
		}

		if (!source.exists()) {
			throw new IllegalArgumentException("Source directory ("
					+ source.getPath() + ") doesn't exist.");
		}

		if (destination.exists()) {
			throw new IllegalArgumentException("Destination ("
					+ destination.getPath() + ") exists.");
		}

		destination.mkdirs();
		File[] files = source.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				copyDirectory(file, new File(destination, file.getName()));
			} else {
				copyFile(file, new File(destination, file.getName()));
			}
		}
	}

	public static final void copyFile(File source, File destination)
			throws IOException {
		FileChannel sourceChannel = new FileInputStream(source).getChannel();
		FileChannel targetChannel = new FileOutputStream(destination)
				.getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
		sourceChannel.close();
		targetChannel.close();
	}

	public static int compareLinesOf(File actual, File expected)
			throws Exception {
		FileChannel fc1 = new FileInputStream(actual).getChannel();
		BufferedReader actualBr = new BufferedReader(Channels.newReader(fc1,
				"UTF-8"));

		FileChannel fc2 = new FileInputStream(expected).getChannel();
		BufferedReader expectedBr = new BufferedReader(Channels.newReader(fc2,
				"UTF-8"));

		try {
			String actualLine = "";
			int line = 1;

			actualBr.mark(500);
			if ((actualLine = actualBr.readLine()) == null) {
				return 0;
			}
			actualBr.reset();

			while ((actualLine = actualBr.readLine()) != null) {
				if (!actualLine.equals(expectedBr.readLine())) {
					return line;
				}

				line++;
			}

			return -1;
		} finally {
			fc1.close();
			actualBr.close();

			fc2.close();
			expectedBr.close();
		}
	}
}
