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
package net.sourceforge.plantuml.preproc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.plantuml.AFile;
import net.sourceforge.plantuml.AFileRegular;
import net.sourceforge.plantuml.AParentFolder;
import net.sourceforge.plantuml.Log;

public class FileWithSuffix {

	private final AFile file;
	private final String suffix;
	private final String entry;
	private final String description;

	@Override
	public String toString() {
		return file.toString();
	}
	public Reader getReader(String charset) throws IOException {
		if (entry == null) {
			if (charset == null) {
				Log.info("Using default charset");
				return new InputStreamReader(file.open());
			}
			Log.info("Using charset " + charset);
			return new InputStreamReader(file.open(), charset);
		}
		final InputStream is = getDataFromZip(file.open(), entry);
		if (is == null) {
			return null;
		}
		if (charset == null) {
			Log.info("Using default charset");
			return new InputStreamReader(is);
		}
		Log.info("Using charset " + charset);
		return new InputStreamReader(is, charset);
	}

	private InputStream getDataFromZip(InputStream is, String name) throws IOException {
		final ZipInputStream zis = new ZipInputStream(is);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			final String fileName = ze.getName();
			if (ze.isDirectory()) {
			} else if (fileName.equals(name)) {
				return zis;
			}
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		return null;
	}

	public boolean fileOk() {
		return file != null && file.isOk();
	}

	FileWithSuffix(File file, String suffix) {
		this.file = new AFileRegular(file);
		this.suffix = suffix;
		this.entry = null;
		// this.description = file.getAbsolutePath();
		this.description = getFileName(file);
	}

	public static String getFileName(File file) {
		return file.getName();
	}

	public static String getAbsolutePath(File file) {
		return file.getAbsolutePath();
	}

	public FileWithSuffix(ImportedFiles importedFiles, String fileName, String suffix) throws IOException {
		final int idx = fileName.indexOf('~');
		this.suffix = suffix;
		if (idx == -1) {
			this.file = importedFiles.getAFile(fileName);
			this.entry = null;
		} else {
			this.file = importedFiles.getAFile(fileName.substring(0, idx));
			this.entry = fileName.substring(idx + 1);
		}

		if (file == null) {
			this.description = fileName;
		} else if (entry == null) {
			// this.description = file.getAbsolutePath();
			this.description = fileName;
		} else {
			// this.description = file.getAbsolutePath() + "~" + entry;
			this.description = fileName;
		}

	}

	@Override
	public int hashCode() {
		return (file == null ? 0 : file.hashCode()) + (suffix == null ? 0 : suffix.hashCode() * 43)
				+ (entry == null ? 0 : entry.hashCode());
	}

	@Override
	public boolean equals(Object arg) {
		final FileWithSuffix other = (FileWithSuffix) arg;
		return this.file.equals(other.file) && equals(suffix, other.suffix) && same(entry, other.entry);
	}

	private static boolean same(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 != null && s2 != null) {
			return s1.equals(s2);
		}
		return false;
	}

	private static boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 != null && s2 != null) {
			return s1.equals(s2);
		}
		assert (s1 == null && s2 != null) || (s1 != null && s2 == null);
		return false;
	}

	public static Set<File> convert(Set<FileWithSuffix> all) {
		final Set<File> result = new HashSet<File>();
		for (FileWithSuffix f : all) {
			result.add(f.file.getUnderlyingFile());
		}
		return result;
	}

	public AParentFolder getParentFile() {
		Log.info("Getting parent of " + file);
		Log.info("-->The parent is " + file.getParentFile());
		return file.getParentFile();
	}

	public String getDescription() {
		return description;
	}

	public final String getSuffix() {
		return suffix;
	}
	
	public String toStringDebug() {
		return file.getAbsolutePath();
	}

}
