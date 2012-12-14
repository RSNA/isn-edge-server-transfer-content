/* Copyright (c) <2010>, <Radiological Society of North America>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the <RSNA> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.rsna.isn.transfercontent.ihe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;

/**
 * This class loads the contents of an XDS document on demand instead of when an
 * instance is instantiated.
 *
 * @author Wyatt Tellis
 * @version 2.1.0
 *
 */
public class LazyLoadedXdsDocument extends XDSDocument
{
	private static final Logger logger = Logger.getLogger(LazyLoadedXdsDocument.class);

	public LazyLoadedXdsDocument(DocumentDescriptor descriptor, File file)
	{
		super(descriptor);

		this.file = file;
	}

	private final File file;

	/**
	 * Get the value of file
	 *
	 * @return the value of file
	 */
	public File getFile()
	{
		return file;
	}

	@Override
	public InputStream getStream()
	{
		return new AutoCloseInputStream(new LazyOpenFileInputStream(file));
	}

	private class LazyOpenFileInputStream extends InputStream
	{
		private final File file;

		private BufferedInputStream in = null;

		public LazyOpenFileInputStream(File file)
		{
			this.file = file;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException
		{
			init();

			return in.read(b, off, len);
		}

		@Override
		public int read(byte[] b) throws IOException
		{
			init();

			return in.read(b);
		}

		@Override
		public int read() throws IOException
		{
			init();

			return in.read();
		}

		@Override
		public int available() throws IOException
		{
			init();

			return in.available();
		}

		@Override
		public boolean markSupported()
		{
			try
			{
				init();

				return in.markSupported();
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}

		@Override
		public synchronized void mark(int readlimit)
		{
			try
			{
				init();

				in.mark(readlimit);
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}

		@Override
		public synchronized void reset() throws IOException
		{
			init();

			in.reset();
		}

		@Override
		public long skip(long n) throws IOException
		{
			init();

			return in.skip(n);
		}

		@Override
		public void close() throws IOException
		{
			if (in != null)
			{
				in.close();
			
				in = null;
				
				logger.info("Completed transmission of file " + file);
			}
		}

		private void init() throws IOException
		{
			if (in == null)
			{
				FileInputStream fis = new FileInputStream(file);
				in = new BufferedInputStream(fis);

				logger.info("Started transmission of file " + file);
			}
		}

	}
}
