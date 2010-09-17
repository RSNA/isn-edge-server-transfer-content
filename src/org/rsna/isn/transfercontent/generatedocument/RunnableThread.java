/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.generatedocument;

import java.lang.Thread.UncaughtExceptionHandler;

class RunnableThread implements Runnable {
    long threadID;
    private Thread runner;
    private UncaughtExceptionHandler eh;

    public RunnableThread() {
    }

    public RunnableThread(String threadName) throws InterruptedException {
        //System.out.println(runner.getName()  +  "\n");
        runner = new Thread(this, threadName); // (1) Create a new thread.
        runner.setName(threadName);
        runner.setDaemon(true);
        threadID = runner.getId();
        start();

        int active = Thread.activeCount();
        System.out.println("currently active threads: " + active);
        Thread all[] = new Thread[active];
        Thread.enumerate(all);
        for (int i = 0; i < active; i++) {
            System.out.println(i + ": " + all[i]);
        }


        long patience = 1000 * 60 * 60;
        long startTime = System.currentTimeMillis();


        while (runner.isAlive()) {
            System.out.println(" Thread Still waiting...");
            //Wait maximum of 1 second for MessageLoop thread to
            //finish.
           runner.join(1000);
           if (((System.currentTimeMillis() - startTime) > patience) && runner.isAlive()) {
               System.out.println("Thread Tired of waiting!");
                runner.interrupt();
                //Shouldn't be long now -- wait indefinitely
                runner.join();
            }

        }

    }

    public void run() {
        //Display info about this particular thread
        System.out.println(Thread.currentThread() + " " + threadID + "\n");
        System.out.print("Thread " + runner.getName() + ": Entered\n");
        System.out.print("Thread " + runner.getName() + ": Working\n");
        safeSleep(150, "Thread " + runner.getName() + " work");
        System.out.print("Thread " + runner.getName() + ": Done with work\n");

   }

    public final void start() {
        runner.start();
    }

    public void sleep(long numMillisecondsToSleep) throws InterruptedException {
        Thread.sleep(numMillisecondsToSleep);
    //   throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void safeSleep(long milliseconds, String s) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.print(e.getStackTrace());
        }
    }
}