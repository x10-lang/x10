package x10.sncode;

public class ByteBuffer {
	private byte[] buf;
	private int offset;
	private int buflen;

	public ByteBuffer(byte[] buf) {
		this.buf = buf;
		this.offset = 0;
		this.buflen = buf.length;
	}

	public ByteBuffer() {
		this(64);
	}

	public ByteBuffer(int size) {
		this.buf = new byte[size];
		this.offset = 0;
		this.buflen = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buflen; i++) {
			byte b = buf[i];
			if (b == '\\')
				sb.append("\\\\");
			else if (Character.isWhitespace((char) b) || (b >= 33 && b <= 126))
				sb.append((char) b);
			else {
				sb.append("\\x");
				String x = Integer.toHexString(b & 0xff);
				for (int j = x.length(); j < 2; j++)
					sb.append("0");
				sb.append(x);
			}
		}
		return sb.toString();
	}

	public int offset() {
		return offset;
	}

	void skip(int n) {
		this.offset += n;
	}

	public void seek(int n) {
		this.offset = n;
	}

	void reserve(int size) {
		if (buf == null) {
			buf = new byte[size];
		} else if (size > buf.length) {
			byte[] newBuf = new byte[Math.max(buf.length * 2, size)];
			System.arraycopy(buf, 0, newBuf, 0, buf.length);
			buf = newBuf;
		}
	}

	/**
	 * Set the 4 bytes at offset 'offset' in 'buf' to the signed 32-bit value in
	 * v.
	 */
	public void addInt(int v) {
		setInt(offset, v);
		offset += 4;
	}

	/**
	 * Set the 4 bytes at offset 'offset' in 'buf' to the signed 32-bit value in
	 * v.
	 */
	void setInt(int offset, int v) {
		reserve(offset + 4);
		buf[offset++] = (byte) (v >> 24);
		buf[offset++] = (byte) (v >> 16);
		buf[offset++] = (byte) (v >> 8);
		buf[offset++] = (byte) v;
		buflen = Math.max(offset, buflen);
	}

	void setLong(int offset, long v) {
		reserve(offset + 8);
		setInt(offset, (int) (v >> 32));
		setInt(offset + 4, (int) v);
	}

	/**
	 * Set the 8 bytes at offset 'offset' in 'buf' to the signed 64-bit value in
	 * v.
	 */
	public void addLong(long v) {
		setLong(offset, v);
		offset += 8;
	}

	/**
	 * Set the 4 bytes at offset 'offset' in 'buf' to the float value in v.
	 */
	public void addFloat(float v) {
		addInt(Float.floatToIntBits(v));
	}

	/**
	 * Set the 4 bytes at offset 'offset' in 'buf' to the float value in v.
	 */
	public void setFloat(int offset, float v) {
		setInt(offset, Float.floatToIntBits(v));
	}

	/**
	 * Set the 8 bytes at offset 'offset' in 'buf' to the double value in v.
	 */
	public void addDouble(double v) throws IllegalArgumentException {
		addLong(Double.doubleToRawLongBits(v));
	}

	/**
	 * Set the 8 bytes at offset 'offset' in 'buf' to the double value in v.
	 */
	public void setDouble(int offset, double v) throws IllegalArgumentException {
		setLong(offset, Double.doubleToRawLongBits(v));
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void setUShort(int offset, int v) throws IllegalArgumentException {
		reserve(offset + 2);
		buf[offset++] = (byte) (v >> 8);
		buf[offset++] = (byte) v;
		buflen = Math.max(offset, buflen);
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void addUShort(int v) throws IllegalArgumentException {
		setUShort(offset, v);
		offset += 2;
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void setUByte(int offset, int v) throws IllegalArgumentException {
		reserve(offset + 1);
		buf[offset++] = (byte) v;
		buflen = Math.max(offset, buflen);
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void addUByte(int v) {
		setUByte(offset, v);
		offset++;
	}

	public byte[] getBytes() {
		if (buf.length == buflen) {
			return buf;
		} else {
			byte[] b = new byte[buflen];
			System.arraycopy(buf, 0, b, 0, buflen);
			return b;
		}
	}

	public byte[] getBytes(int i, int len) throws InvalidClassFileException {
		if (i < 0 || i + len > buflen)
			throw new InvalidClassFileException(i, "cannot get raw bytes");
		byte[] b = new byte[len];
		System.arraycopy(buf, i, b, 0, len);
		return b;
	}

	public void addBytes(byte[] bytes) {
		reserve(offset + bytes.length);
		System.arraycopy(bytes, 0, buf, offset, bytes.length);
		offset += bytes.length;
		buflen = Math.max(offset, buflen);
	}

	public void setBytes(int offset, byte[] bytes) {
		reserve(offset + bytes.length);
		System.arraycopy(bytes, 0, buf, offset, bytes.length);
		buflen = Math.max(offset, buflen);
	}

	public void addBytes(byte[] bytes, int boff, int blen) {
		reserve(offset + blen);
		System.arraycopy(bytes, boff, buf, offset, blen);
		offset += blen;
		buflen = Math.max(offset, buflen);
	}

	public void setBytes(int offset, byte[] bytes, int boff, int blen) {
		reserve(offset + blen);
		System.arraycopy(bytes, boff, buf, offset, blen);
		buflen = Math.max(offset, buflen);
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void setByte(int offset, int v) throws IllegalArgumentException {
		setUByte(offset, v);
	}

	/**
	 * Set the 2 bytes at offset 'offset' in 'buf' to the unsigned 16-bit value
	 * in v.
	 * 
	 * @throws IllegalArgumentException
	 *             if buf is null
	 */
	public void addByte(int v) {
		addUByte(v);
	}

	public void checkLength(int size) throws InvalidClassFileException {
		if (size > buflen || size > buf.length)
			throw new InvalidClassFileException(size, "Out of range.");
	}

	public long getLong() throws InvalidClassFileException {
		long result = getLong(offset);
		offset += 8;
		return result;
	}

	public int getInt() throws InvalidClassFileException {
		int result = getInt(offset);
		offset += 4;
		return result;
	}

	public float getFloat() throws InvalidClassFileException {
		int result = getInt();
		return Float.intBitsToFloat(result);
	}

	public float getFloat(int offset) throws InvalidClassFileException {
		int result = getInt(offset);
		return Float.intBitsToFloat(result);
	}

	public double getDouble() throws InvalidClassFileException {
		long result = getLong();
		return Double.longBitsToDouble(result);
	}

	public double getDouble(int offset) throws InvalidClassFileException {
		long result = getLong(offset);
		return Double.longBitsToDouble(result);
	}

	/**
	 * @return the signed 32-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public int getInt(int offset) throws InvalidClassFileException {
		checkLength(offset + 4);
		return (buf[offset] << 24) + ((buf[offset + 1] & 0xFF) << 16)
				+ ((buf[offset + 2] & 0xFF) << 8) + (buf[offset + 3] & 0xFF);
	}

	/**
	 * @return the signed 64-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public long getLong(int offset) throws InvalidClassFileException {
		checkLength(offset + 8);
		int r1 = getInt(offset);
		int r2 = getInt(offset + 4);
		return ((long) r1 << 32) | (r2 & 0xFFFFFFFFL);
	}

	public String getUtf8() throws InvalidClassFileException {
		int count = getInt(offset);
		String s = getUtf8(offset + 4, count);
		offset += 4;
		offset += count;
		return s;
	}

	public String getUtf8(int offset) throws InvalidClassFileException {
		int count = getInt(offset);
		return getUtf8(offset + 4, count);
	}

	private InvalidClassFileException invalidUtf8(int offset) {
		return new InvalidClassFileException(offset,
				"Invalid Java Utf8 string.");
	}

	/**
	 * @return the value of the Utf8 string at constant pool item i
	 */
	private String getUtf8(int offset, int count)
			throws InvalidClassFileException {
		int end = count + offset;
		StringBuilder buf = new StringBuilder(count);
		offset += 4;
		while (offset < end) {
			byte x = getByte(offset);
			if ((x & 0x80) == 0) {
				if (x == 0) {
					throw invalidUtf8(offset);
				}
				buf.append((char) x);
				offset++;
			} else if ((x & 0xE0) == 0xC0) {
				if (offset + 1 >= end) {
					throw invalidUtf8(offset);
				}
				byte y = getByte(offset + 1);
				if ((y & 0xC0) != 0x80) {
					throw invalidUtf8(offset);
				}
				buf.append((char) (((x & 0x1F) << 6) + (y & 0x3F)));
				offset += 2;
			} else if ((x & 0xF0) == 0xE0) {
				if (offset + 2 >= end) {
					throw invalidUtf8(offset);
				}
				byte y = getByte(offset + 1);
				byte z = getByte(offset + 2);
				if ((y & 0xC0) != 0x80 || (z & 0xC0) != 0x80) {
					throw invalidUtf8(offset);
				}
				buf
						.append((char) (((x & 0x0F) << 12) + ((y & 0x3F) << 6) + (z & 0x3F)));
				offset += 3;
			} else {
				throw invalidUtf8(offset);
			}
		}
		return buf.toString();
	}

	public String getUtf8ToDelimiter(int offset, String delims)
			throws InvalidClassFileException {
		int i = offset();
		String s = getUtf8ToDelimiter(delims);
		seek(i);
		return s;
	}

	public String getUtf8ToDelimiter(String delims)
			throws InvalidClassFileException {
		StringBuilder buf = new StringBuilder();
		while (offset < buflen && offset < this.buf.length) {
			byte x = getByte(offset);
			if ((x & 0x80) == 0) {
				if (x == 0) {
					throw invalidUtf8(offset);
				}
				buf.append((char) x);
				offset++;
			} else if ((x & 0xE0) == 0xC0) {
				byte y = getByte(offset + 1);
				if ((y & 0xC0) != 0x80) {
					throw invalidUtf8(offset);
				}
				buf.append((char) (((x & 0x1F) << 6) + (y & 0x3F)));
				offset += 2;
			} else if ((x & 0xF0) == 0xE0) {
				byte y = getByte(offset + 1);
				byte z = getByte(offset + 2);
				if ((y & 0xC0) != 0x80 || (z & 0xC0) != 0x80) {
					throw invalidUtf8(offset);
				}
				buf
						.append((char) (((x & 0x0F) << 12) + ((y & 0x3F) << 6) + (z & 0x3F)));
				offset += 3;
			} else {
				throw invalidUtf8(offset);
			}

			char c = buf.charAt(buf.length() - 1);

			if (delims.indexOf(c) >= 0)
				break;
		}

		return buf.toString();
	}

	public int getUShort() throws InvalidClassFileException {
		int result = getUShort(offset);
		offset += 2;
		return result;
	}

	/**
	 * @return the unsigned 16-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public int getUShort(int offset) throws InvalidClassFileException {
		checkLength(offset + 2);
		return ((buf[offset] & 0xFF) << 8) + (buf[offset + 1] & 0xFF);
	}

	/**
	 * @return the signed 16-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public short getShort(int i) throws InvalidClassFileException {
		checkLength(i + 2);
		return (short) ((buf[i] << 8) + (buf[i + 1] & 0xFF));
	}

	/**
	 * @return the signed 8-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public byte getByte(int i) throws InvalidClassFileException {
		checkLength(i + 1);
		return buf[i];
	}

	/**
	 * @return the signed 8-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public byte getByte() throws InvalidClassFileException {
		byte result = getByte(offset);
		offset++;
		return result;
	}

	/**
	 * @return the unsigned 8-bit value at offset i in the class data
	 * @throws InvalidClassFileException
	 */
	public int getUByte(int i) throws InvalidClassFileException {
		checkLength(i + 1);
		return buf[i] & 0xFF;
	}

	public void setCPIndex(int offset, int i) {
		setInt(offset, i);
	}

	public void addCPIndex(int i) {
		addInt(i);
	}

	public int getCPIndex(int i) throws InvalidClassFileException {
		return getInt(i);
	}

	public int getCPIndex() throws InvalidClassFileException {
		return getInt();
	}

	public void setLength(int offset, int i) {
		setInt(offset, i);
	}

	public void addLength(int i) {
		addInt(i);
	}

	public int getLength(int i) throws InvalidClassFileException {
		return getInt(i);
	}

	public int getLength() throws InvalidClassFileException {
		return getInt();
	}

	public void setCount(int offset, int i) {
		setInt(offset, i);
	}

	public void addCount(int i) {
		addInt(i);
	}

	public int getCount(int i) throws InvalidClassFileException {
		return getInt(i);
	}

	public int getCount() throws InvalidClassFileException {
		return getInt();
	}

	public void getAttributeOffsets(int[] offsets)
			throws InvalidClassFileException {
		int count = offsets.length - 1;

		for (int i = 0; i < count; i++) {
			offsets[i] = offset();
			int nameIndex = getCPIndex(); // unused
			int payloadLen = getLength();
			if (payloadLen < 0) {
				throw new InvalidClassFileException(offset(),
						"negative attribute length: " + payloadLen);
			}
			skip(payloadLen);
		}

		offsets[count] = offset();
	}
}
