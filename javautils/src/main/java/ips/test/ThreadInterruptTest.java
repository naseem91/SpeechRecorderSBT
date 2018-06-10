//    IPS Java Utils

// 	  (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ips.test;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * @author klausj
 *
 */
public class ThreadInterruptTest implements Runnable, UncaughtExceptionHandler {

    private Thread t;
    private Object notify=new Object();
    public ThreadInterruptTest(){
        super();
        t=new Thread(this);
        t.setDefaultUncaughtExceptionHandler(this);
        t.start();
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        t.interrupt();
        
    }
    
    
    public static void main(String[] args){
        ThreadInterruptTest tit=new ThreadInterruptTest();
        
        
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                synchronized (notify) {
                   
                notify.wait(4000);
                }
            } catch (InterruptedException e) {
                System.out.println("Wait interrupted. Thread interrupted flag: "+Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
            }
          
            System.out.println("Thread interrupted flag before sleep: "+Thread.currentThread().isInterrupted());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted. Thread interrupted flag: "+Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
            }
            System.out.println("Thread interrupted flag after sleep: "+Thread.currentThread().isInterrupted());
            int i=0;
            for(;i<10000000;i++){
                double a=Math.log(i);
            }
            System.out.println(i+" Thread interrupted flag after calc: "+Thread.currentThread().isInterrupted());
        }
        
    }


    /* (non-Javadoc)
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    public void uncaughtException(Thread arg0, Throwable arg1) {
        System.out.println(arg0+" "+arg1);
    }
}
