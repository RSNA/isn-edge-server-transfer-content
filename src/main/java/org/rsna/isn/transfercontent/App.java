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
package org.rsna.isn.transfercontent;

import org.apache.log4j.Logger;
import org.rsna.isn.util.Environment;

/**
 * Main class of tranfser content app.
 * 
 * @author Wyatt Tellis
 * @version 1.2.0
 */
public class App
{
	private static final Logger logger = Logger.getLogger(App.class);

	/**
	 * Starts the transfer content application.
	 * @param args Command line arguments.  These are ignored.
	 * @throws Exception If there was an exception while attempting to start
	 * the application.
	 */
	public static void main(String[] args) throws Exception
	{
		Environment.init("transfer");


		Monitor monitor = new Monitor();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(monitor));


		logger.info("Attempting to start job monitor");
		monitor.start();
	}

	private static class ShutdownHook extends Thread
	{
		private final Monitor monitor;

		public ShutdownHook(Monitor monitor)
		{
			this.monitor = monitor;
		}

		@Override
		public void run()
		{
			try
			{
				logger.info("Attempting to stop job monitor.");

				monitor.stopRunning();
			}
			catch (InterruptedException ex)
			{
				logger.fatal("Uncaught exception while stopping job monitor", ex);
			}
		}

	}
}
