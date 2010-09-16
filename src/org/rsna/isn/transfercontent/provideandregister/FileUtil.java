/*---------------------------------------------------------------
*  Copyright 2005 by the Radiological Society of North America
*
*  This source software is released under the terms of the
*  RSNA Public License (http://mirc.rsna.org/rsnapubliclicense)
*----------------------------------------------------------------*/

package org.rsna.isn.transfercontent.provideandregister;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.zip.*;

/**
 * Encapsulates static methods for working with files and directories.
 */
public class FileUtil {

	public static Charset latin1 = Charset.forName("ISO-8859-1");
	public static Charset utf8 = Charset.forName("UTF-8");

	/**
	 * Reads a file completely and returns a byte array containing the data.
	 * @param file the file to read.
	 * @return the bytes of the file, or an empty array if an error occurred.
	 */
	public static byte[] getFileBytes(File file) {
		if (!file.exists()) return new byte[0];
		int length = (int)file.length();
		byte[] bytes = new byte[length];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(bytes,0,bytes.length);
			fis.close();
			return bytes;
		}
		catch (Exception e) {
			if (fis != null) {
				try { fis.close(); }
				catch (Exception ignore) { }
			}
			return new byte[0];
		}
	}

	/**
	 * Reads a text file completely, using the UTF-8 encoding
	 * @param file the file to read.
	 * @return the text of the file, or an empty string if an error occurred.
	 */
	public static String getFileText(File file) {
		return getFileText(file,utf8);
	}

	/**
	 * Reads a text file completely, using the specified encoding, or
	 * UTF-8 if the specified encoding is not supported.
	 * @param file the file to read.
	 * @param encoding the name of the charset to use.
	 * @return the text of the file, or an empty string if an error occurred.
	 */
	public static String getFileText(File file, String encoding) {
		Charset charset;
		try { charset = Charset.forName(encoding); }
		catch (Exception ex) { charset = utf8; }
		return getFileText(file,charset);
	}

	/**
	 * Reads a text file completely, using the specified encoding.
	 * @param file the file to read.
	 * @param charset the character set to use for the encoding of the file.
	 * @return the text of the file, or an empty string if an error occurred.
	 */
	public static String getFileText(File file, Charset charset) {
		BufferedReader br = null;
		try {
			if (!file.exists()) return "";
			br = new BufferedReader(
					new InputStreamReader(
						new FileInputStream(file),charset));
			StringWriter sw = new StringWriter();
			int n;
			char[] cbuf = new char[1024];
			while ((n=br.read(cbuf,0,cbuf.length)) != -1) sw.write(cbuf,0,n);
			br.close();
			return sw.toString();
		}
		catch (Exception e) {
			if (br != null) {
				try { br.close(); }
				catch (Exception ignore) { }
			}
			return "";
		}
	}

	/**
	 * Writes a string to a text file, trying to determine the desired encoding
	 * from the text itself and using the UTF-8 encoding as a default.
	 * @param file the file to write.
	 * @param text the string to write into the file.
	 * @return true if the operation succeeded completely; false otherwise.
	 */
	public static boolean setFileText(File file, String text) {
		return setFileText(file,utf8,text);
	}

	/**
	 * Writes a string to a text file, using the specified encoding, or
	 * UTF-8 if the specified encoding is not supported.
	 * @param file the file to write.
	 * @param encoding the name of the charset to use.
	 * @param text the string to write into the file.
	 * @return true if the operation succeeded completely; false otherwise.
	 */
	public static boolean setFileText(File file, String encoding, String text) {
		Charset charset;
		try { charset = Charset.forName(encoding); }
		catch (Exception ex) { charset = utf8; }
		return setFileText(file,charset,text);
	}

	/**
	 * Writes a string to a text file, using the specified encoding.
	 * @param file the file to write.
	 * @param charset the character set to use for the encoding of the file.
	 * @param text the string to write into the file.
	 * @return true if the operation succeeded completely; false otherwise.
	 */
	public static boolean setFileText(File file, Charset charset, String text) {
		BufferedWriter bw = null;
		boolean result = true;
		try {
			bw = new BufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(file),charset));
			bw.write(text,0,text.length());
		}
		catch (Exception e) { result = false; }
		finally {
			try { bw.flush(); bw.close(); }
			catch (Exception ignore) { }
		}
		return result;
	}

	/**
	 * Copies a file.
	 * @param inFile the file to copy.
	 * @param outFile the copy.
	 * @return true if the operation succeeded completely; false otherwise.
	 */
	public static boolean copyFile(File inFile, File outFile) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		boolean result = true;
		try {
			in = new BufferedInputStream(new FileInputStream(inFile));
			out = new BufferedOutputStream(new FileOutputStream(outFile));
			byte[] buffer = new byte[4096];
			int n;
			while ( (n = in.read(buffer,0,4096)) != -1) out.write(buffer,0,n);
		}
		catch (Exception e) { result = false; }
		finally {
			try { out.flush(); out.close(); in.close(); }
			catch (Exception ignore) { }
		}
		return result;
	}

	/**
	 * Deletes a file. If the file is a directory, deletes the contents
	 * of the directory and all its child directories, then deletes the
	 * directory itself.
	 * @param file the file to delete.
	 * @return true if the operation succeeded completely; false otherwise.
	 */
	public static boolean deleteAll(File file) {
		boolean b = true;
		if (file.exists()) {
			if (file.isDirectory()) {
				try {
					File[] files = file.listFiles();
					for (int i=0; i<files.length; i++) b &= deleteAll(files[i]);
				}
				catch (Exception e) { return false; }
			}
			b &= file.delete();
		}
		return b;
	}

	//Zip a directory and its subdirectories.
	public static synchronized boolean zipDirectory(File dir, File zipFile) {
		try {
			//Get the parent and find out how long it is
			File parent = dir.getParentFile();
			int rootLength = parent.getAbsolutePath().length() + 1;

			//Get the various streams and buffers
			FileOutputStream fout = new FileOutputStream(zipFile);
			ZipOutputStream zout = new ZipOutputStream(fout);
			zipDirectory(zout,dir,rootLength);
			zout.close();
			return true;
		}
		catch (Exception ex) {
			//No joy - log the exception message
			System.out.println("Error creating zip file:\n" + ex.getMessage());
			return false;
		}
	}

	//Zip a directory and its subdirectories
	//into a ZipOutputStream, setting the root of the zip package to be the parent
	//directory of the originally requested directory.
	private static synchronized void zipDirectory(ZipOutputStream zout, File dir, int rootLength)
												throws Exception {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i=0; i<files.length; i++) {
				if (files[i].isDirectory()) zipDirectory(zout,files[i],rootLength);
				else zipFile(zout,files[i],rootLength);
			}
		}
	}

	//Zip a file into a ZipOutputStream, setting the
	//root of the zip package to be the parent directory
	//of the originally requested directory.
	private static synchronized void zipFile(ZipOutputStream zout, File file, int rootLength)
												throws Exception {
		FileInputStream fin;
		ZipEntry ze;
		byte[] buffer = new byte[10000];
		int bytesread;
		String entryname = file.getAbsolutePath().substring(rootLength);
		ze = new ZipEntry(entryname);
		if (file.exists()) {
			fin = new FileInputStream(file);
			zout.putNextEntry(ze);
			while ((bytesread = fin.read(buffer)) > 0) zout.write(buffer,0,bytesread);
			zout.closeEntry();
			fin.close();
		}
	}

}