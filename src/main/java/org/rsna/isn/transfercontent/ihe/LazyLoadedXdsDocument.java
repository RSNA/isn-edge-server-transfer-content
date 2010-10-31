/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.ihe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;

/**
 * The XDSDocumentFromFile class loads the file into memory first. This class
 * will just provide a FileInputStream upon invocation of getStream().
 * @author wtellis
 */
public class LazyLoadedXdsDocument extends XDSDocument
{
	private final File file;

	public LazyLoadedXdsDocument(DocumentDescriptor descriptor, File file)
	{
		super(descriptor);

		this.file = file;
	}

	@Override
	public InputStream getStream()
	{
		try
		{
			return new FileInputStream(file);
		}
		catch (FileNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
