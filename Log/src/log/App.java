package log;
 

import java.io.*;
import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;


 
public class App {
  
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
            
            long startTime = System.currentTimeMillis();
             
            LogExtracter le = new LogExtracter();
            
            //List<String> finalresult = le.getDataStored("/media/Respaldo/log/", "2014-01-31");
            List<String> finalresult = new ArrayList<String>();
             
            
            //something temporal
            BufferedReader reader2 = new BufferedReader(new FileReader("/media/Respaldo/log/data/2014-01-27.txt"));
            String linea;
            while (  (linea = reader2.readLine()) != null) {
               finalresult.add(linea);
            }
            //something temporal
             
            Frame f = new Frame();
            f.setGraph(finalresult);
            f.setVisible(true);//Plot the data
 
            // List<String> obs = l.getObservationTime("/media/Respaldo/log/", "2014-01-31");
 
            //for(int i=0;i<obs.size();i++){
            //    System.out.println(obs.get(i));
             //}
 
             long endTime   = System.currentTimeMillis();
             long totalTime = endTime - startTime;
             float tTime = (float) totalTime/60000;
             System.out.println("the program took "+tTime+" minutes to finish");

      }     
 }