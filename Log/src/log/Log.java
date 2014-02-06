package log;
 
import java.awt.Color;
import java.io.*;
import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;
import org.jfree.ui.RefineryUtilities;

 
public class Log {
    
        public List<String> getDataStored(String path, String date) throws FileNotFoundException, IOException{
            
            String line;
            String pattern = "Successfully stored"; //pattern to look over the log
            String line2[]; //array that has each 'word' of a single line
            String timestamp="0"; //variable which has the timestamp
            long value = 0; //variable which stores the amount of data tranfered
            List<String> result = new ArrayList<String>();  //final array that has each timestamp value 
            List<String> result2 = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
            List<String> finalresult = new ArrayList<String>();
            
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            
            for (int i = 0; i < listOfFiles.length; i++) {  //for each (log) file in the folder
                                           
                if (listOfFiles[i].isFile()) {
                    
                    System.out.println("File "+(i+1)+"/"+listOfFiles.length); //the file that is being read
                    
                    BufferedReader reader = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));
                    
                    if(listOfFiles[i].getName().contains(date)){
                    
                    while (  (line = reader.readLine()) != null) { //reads every line of the file                                                                       
                        
                        if(line.contains(pattern)){ //if the line contains the pattern Successfully stored
                             line2 = line.split(" "); //split the line read
                             if(line2[1].substring(11, 21).equals(date)){
                             
                             if(line2[9].contains("FLOW")&&line2[1].length()<40){ //if the 9th 'word' contain FLOW
               
                                      if(timestamp.equals(line2[1].substring(11, 24))){ //the same timestamp as the before one
                
                                            value = value + Long.parseLong(line2[17]); //add the value
                                      }
                                      
                                      else{ //if it's not the same timestamp as the before one
                  
                                             if(!timestamp.equals("0")){ //if it's not the first line
                                                 if(!result.contains(timestamp)){ //this is because the timestamps are not read in order
                                                    result.add(timestamp);  //add the value to the array
                                                    result2.add(""+value);  //add the value to the array
                                                 }
                                                 
                                                 else if(result.contains(timestamp)){ //if the timestamp was already in the array
                                                    int index = result.indexOf(timestamp);//the position where it is
                                                    long aux = Long.parseLong(result2.get(index));//the value related to that timestamp
                                                    value = aux + value; //add the values
                                                    String s = ""+value; //to string
                                                    result2.set(index, s); //set the new value of "value"
                                                 }
                                                 
                                               timestamp = line2[1].substring(11, 24);  //start again with the new timestamp
                                               value = Long.parseLong(line2[17]);  //star again with the new value
                                             }
                                             
                                             else if(timestamp.equals("0")){  //if it's the first line of the file
                                                 timestamp = line2[1].substring(11, 24);  //start
                                                 value = Long.parseLong(line2[17]);  //start
                                             }
                  
                                          }
                              }
            
                          }
                        }
                      }
                     }//end WHILE which is reading a single file
                        
                 }
                
             }//end of for which indicates that there is no more files left
             
             if(!result.contains(timestamp)){
                 result.add(timestamp);  //add the value to the array
                 result2.add(""+value);  //add the value to the array
             }
         
             else if(result.contains(timestamp)){
                 int index = result.indexOf(timestamp);
                 long aux = Long.parseLong(result2.get(index));
                 value = aux + value;
                 String s = ""+value;
                 result2.set(index, s);
             }
             for (int i = 0; i < result.size(); i++) {
       
                finalresult.add(result.get(i)+"#"+(float) (Long.parseLong(result2.get(i)))/1000000);
  
             }

             Collections.sort(finalresult);
            
            return finalresult;
        }
            
         public List<String> getObservationTime(String path, String date) throws FileNotFoundException, IOException{
             
            String line; 
            String pattern1 = "Received ScriptInformationEvent for ExecBlock"; //pattern to look over the log
            String pattern2= "uid";
            String pattern3 = "/X00/X00";
            String line2[]; //array that has each 'word' of a single line    
            String MMEX, SBEX, END, last;
            List<String> iniTime = new ArrayList<String>();  //final array that has each timestamp value 
            List<String> finTime = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
            String pattern4 = "Created new SBEX";
            String pattern5 = "OBOPS_SCHEDULING_EVENT_LISTENER";
            String endpattern = "which ended with status";
            String line_anterior="nada";
            String line_anterior2="vacio";
            String start[], end[];
            List<String> obs = new ArrayList<String>();  //final array that has each timestamp value
             
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            
            for (int i = 0; i < listOfFiles.length; i++) {  //for each (log) file in the folder
                                           
                if (listOfFiles[i].isFile()) {
                    
                    System.out.println("File "+(i+1)+"/"+listOfFiles.length); //the file that is being read
                    
                    BufferedReader reader = new BufferedReader(new FileReader(path + listOfFiles[i].getName()));
                    
                    if(listOfFiles[i].getName().contains(date)){
                    
                    while (  (line = reader.readLine()) != null) { //reads every line of the file                                                                       
                        
                        if(line.contains(pattern1)){ // MMEX start
                          
                              if(line.contains(pattern2) && !line.contains(pattern3)){ //if the line contains the pattern
                          
                                  line2 = line.split(" "); //split the line read 
                                  MMEX = line2[1].substring(11,30)+" "+line2[14].replace("'", "").replace(",", "") +" "+line2[17].replaceAll(".*//*", "");
                                  iniTime.add(MMEX);                         
                          
                               }
                        
                        }
                        
                        else if(line.contains(pattern4)){  //SBEX start
                          
                          if(line_anterior.contains(pattern5) && line_anterior.contains(pattern2)){
                             
                              line2 = line_anterior.split(" ");
                              SBEX = line2[18].substring(0,19)+" "+line2[14].replace("'", "").replace(",", "")+" SBEX";    
                              iniTime.add(SBEX);
                              
                             // System.out.println(SBEX);
                          }
                          else if(line_anterior2.contains(pattern5) && line_anterior2.contains(pattern2)){
                            
                              line2 = line_anterior2.split(" ");
                              SBEX = line2[18].substring(0,19)+" "+line2[14].replace("'", "").replace(",", "")+" SBEX";
                              iniTime.add(SBEX);
                              
                              //System.out.println(SBEX);
                          }
                          
                          
                      }
                      
                      else if(line.contains(endpattern)){ //End
                         
                          line2 = line.split(" ");
                          END = line2[21].substring(0, 19)+" "+line2[14].replace("'", "").replace(",", "");
                          finTime.add(END);
                          //System.out.println(END);
                      }
                                       
                 line_anterior2=line_anterior;
                 line_anterior = line;  
                        
                        
                        
                        
                        
                        
                      
                     }//end WHILE which is reading a single file
                        
                 }
                }
             }//end of for which indicates that there is no more files left
             
          Collections.sort(iniTime);
          Collections.sort(finTime);   
          
          
         // join START and END Observations
   
         for (int i = 0; i < iniTime.size(); i++) {
               start = iniTime.get(i).split(" ");
               for (int j = 0; j < finTime.size(); j++) {
                    end = finTime.get(j).split(" ");
                    if(start[1].equals(end[1])){
                          last = start[0]+" "+end[0]+" "+start[1]+" "+start[2];
                          obs.add(last);
                    }
              } 
        } 
 
 
        
         // join START and END Observations
         return obs;
         }
        
 
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
            
            long startTime = System.currentTimeMillis();
             

             
             
 Log l = new Log();
 
 //List<String> finalresult = l.getDataStored("/media/Respaldo/log/", "2014-01-31");
 
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