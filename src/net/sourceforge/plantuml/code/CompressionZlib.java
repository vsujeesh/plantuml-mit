/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under The MIT License (Massachusetts Institute of Technology License)
 * 
 * See http://opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.code;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionZlib implements Compression {

	private static boolean USE_ZOPFLI = false;
	private static final int COMPRESSION_LEVEL = 9;

	public byte[] compress(byte[] in) {
		if (USE_ZOPFLI) {
			return new CompressionZopfliZlib().compress(in);
		}
		if (in.length == 0) {
			return null;
		}
		int len = in.length * 2;
		if (len < 100) {
			len = 100;
		}
		byte[] result = null;
		while (result == null) {
			result = tryCompress(in, len);
			len *= 2;
		}
		return result;
	}

	private byte[] tryCompress(byte[] in, final int len) {
		// Compress the bytes
		final Deflater compresser = new Deflater(COMPRESSION_LEVEL, true);
		compresser.setInput(in);
		compresser.finish();

		final byte[] output = new byte[len];
		final int compressedDataLength = compresser.deflate(output);
		if (compresser.finished() == false) {
			return null;
		}
		final byte[] result = copyArray(output, compressedDataLength);
		return result;
	}

	public byte[] decompress(byte[] in) throws IOException {

		final byte in2[] = new byte[in.length + 256];
		for (int i = 0; i < in.length; i++) {
			in2[i] = in[i];
		}

		int len = in.length * 5;
		byte[] result = null;
		while (result == null) {
			result = tryDecompress(in2, len);
			len *= 2;
		}
		return result;
	}

	private byte[] tryDecompress(byte[] in, final int len) throws IOException {
		if (len > 200000) {
			throw new IOException("OutOfMemory");
		}
		// Decompress the bytes
		final byte[] tmp = new byte[len];
		final Inflater decompresser = new Inflater(true);
		decompresser.setInput(in);
		try {
			final int resultLength = decompresser.inflate(tmp);
			if (decompresser.finished() == false) {
				return null;
			}
			decompresser.end();

			final byte[] result = copyArray(tmp, resultLength);
			return result;
		} catch (DataFormatException e) {
			// e.printStackTrace();
			throw new IOException(e.toString());
		}
	}

	private byte[] copyArray(final byte[] data, final int len) {
		final byte[] result = new byte[len];
		for (int i = 0; i < result.length; i++) {
			result[i] = data[i];
		}
		return result;
	}

}
