/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author wtellis
 */
public class WebServiceDump
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		ServerSocket server = new ServerSocket(80);

		System.out.println("Started listening...");

		Socket s = server.accept();
		InputStream input = s.getInputStream();

		FileOutputStream output = new FileOutputStream("D:\\dump.txt");

		long bytes = IOUtils.copy(input, output);

		input.close();
		output.close();



		System.out.println("Read " + bytes + " bytes");
	}

}
