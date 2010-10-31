package org.rsna.isn.transfercontent;

import org.apache.log4j.Logger;


/**
 * Hello world!
 *
 */
public class App
{
	private static final Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception
	{
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
