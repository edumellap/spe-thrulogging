package log;
 
import java.io.*;
import java.io.BufferedReader;
import java.util.*;


 
public class Log {
 
	public static void main(String[] args) throws FileNotFoundException, IOException {
            
            long startTime = System.currentTimeMillis();
 
            File folder = new File("/media/Respaldo/log/");
            File[] listOfFiles = folder.listFiles();
            
             String line; //each line of the log file
             String line_anterior="nada";
             String line_anterior2="vacio";
             String timestamp="0"; //variable which has the timestamp
             String line2[]; //array that has each 'word' of a single line
             String start[], end[];
             List<String> result = new ArrayList<String>();  //final array that has each timestamp value 
             List<String> result2 = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
             List<String> iniTime = new ArrayList<String>();  //final array that has each timestamp value 
             List<String> finTime = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
             List<String> obs = new ArrayList<String>();  //final array that has each timestamp value
             String pattern = "Successfully stored"; //pattern to look over the log
             String pattern1 = "Received ScriptInformationEvent for ExecBlock"; //pattern to look over the log
             String pattern2= "uid";
             String pattern3 = "/X00/X00";
             String pattern4 = "Created new SBEX";
             String pattern5 = "OBOPS_SCHEDULING_EVENT_LISTENER";
             String endpattern = "which ended with status";
             String MMEX, SBEX, END, last;
             long value = 0; //variable which stores the amount of data tranfered
            
            for (int i = 0; i < listOfFiles.length; i++) {  //for each (log) file in the folder
               // for (int i = 0; i < 1; i++) {
              if (listOfFiles[i].isFile()) {
               System.out.println("File " + listOfFiles[i].getName());
               
               
         //   /home/striker/NetBeansProjects/Prueba/log/
                  //  /media/Respaldo/log/

              // BufferedReader reader = new BufferedReader(new FileReader("log/log2014-01-05T23:42:28.250--2014-01-05T23:54:55.979--AOS.xml"));
               BufferedReader reader = new BufferedReader(new FileReader("/media/Respaldo/log/" + listOfFiles[i].getName()));

      

                 while (  (line = reader.readLine()) != null) { //reads every line of the file
                    
                      if(line.contains(pattern)){ //if the line contains the pattern
                          
         
                             line2 = line.split(" "); //split the line read 
           
                                  if(line2[9].contains("FLOW")&&line2[1].length()<40){ //if the 9th 'word' contain FLOW
               
                                      if(timestamp.equals(line2[1].substring(11, 24))){ //the same timestamp as the before one
                
                                            value = value + Integer.parseInt(line2[17]); //add the value
                                      }
                                      else{ //if it's not the same timestamp as the before one
                  
                                             if(!timestamp.equals("0")){ //if it's not the first line
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
                                               timestamp = line2[1].substring(11, 24);  //start again with the new timestamp
                                               value = Integer.parseInt(line2[17]);  //star again with the new value
                                             }
                                             
                                             else if(timestamp.equals("0")){  //if it's the first line of the file
                                                 timestamp = line2[1].substring(11, 24);  //start
                                                 value = Integer.parseInt(line2[17]);  //start
                                             }
                  
                                     }
           
              
            
                                  }
            
            
                      }
                      
                      
                      else if(line.contains(pattern1)){ // MMEX start
                          
                        if(line.contains(pattern2) && !line.contains(pattern3)){ //if the line contains the pattern
                          
                            line2 = line.split(" "); //split the line read 
                            MMEX = line2[1].substring(11,30)+" "+line2[14].replace("'", "").replace(",", "") +" "+line2[17].replaceAll(".*/", "");
                            iniTime.add(MMEX);
                            
                           // System.out.println(MMEX);
                           
                          
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
                      
                      
 
                 }  
 
            }
              
         }
            
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

ArrayList<ArrayList<String>> listOlists = new ArrayList<ArrayList<String>>();  //final lista de listas
ArrayList<String> List = new ArrayList<String>();


for (int i = 0; i < result.size(); i++) {
       
    List.add(result.get(i)+"/"+result2.get(i));  // timestamp/value
   
}

Collections.sort(List);

for (int i = 0; i < List.size(); i++) {

ArrayList<String> List2 = new ArrayList<String>();

List2.add(List.get(i).substring(0, 13));//timestamp
List2.add(List.get(i).substring(14));

listOlists.add(List2);

}

System.out.println(listOlists);

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
 
 
 for (int i = 0; i < obs.size(); i++) {
     System.out.println(obs.get(i));
 }
 // join START and END Observations
 
 
 long endTime   = System.currentTimeMillis();
 long totalTime = endTime - startTime;
 float tTime = (float) totalTime/60000;
 System.out.println("the program took "+tTime+" minutes");
 
   

      }     
 }