package log;
 
import java.awt.Color;
import java.io.*;
import java.io.BufferedReader;
import java.util.*;
import org.jfree.ui.RefineryUtilities;

 
public class Log {
 
	public static void main(String[] args) throws FileNotFoundException, IOException {
            
            long startTime = System.currentTimeMillis();
 
            File folder = new File("/media/Respaldo/log/");
            File[] listOfFiles = folder.listFiles();
            
             int parameter = 24;
             String line; //each line of the log file
             String line_anterior="nada";
             String line_anterior2="vacio";
             String timestamp="0"; //variable which has the timestamp
             String line2[]; //array that has each 'word' of a single line
             String start[], end[];
             List<String> result = new ArrayList<String>();  //final array that has each timestamp value 
             List<String> result2 = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
             List<String> finalresult = new ArrayList<String>();
             List<String> finalresult2 = new ArrayList<String>();
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
         /*   
            for (int i = 0; i < listOfFiles.length; i++) {  //for each (log) file in the folder
               // for (int i = 0; i < 1; i++) {
              if (listOfFiles[i].isFile()) {
                  
               
                //  System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
               System.out.println((i+1)+"/"+listOfFiles.length); //the file that is being read
               
               
               
        

              // BufferedReader reader = new BufferedReader(new FileReader("log/log2014-01-05T23:42:28.250--2014-01-05T23:54:55.979--AOS.xml"));
               BufferedReader reader = new BufferedReader(new FileReader("/media/Respaldo/log/" + listOfFiles[i].getName()));

      

                 while (  (line = reader.readLine()) != null) { //reads every line of the file
                    
                      if(line.contains(pattern)){ //if the line contains the pattern Successfully stored
                          
         
                             line2 = line.split(" "); //split the line read
           
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
                      
                      
                      else if(line.contains(pattern1)){ // MMEX start
                          
                        if(line.contains(pattern2) && !line.contains(pattern3)){ //if the line contains the pattern
                          
                            line2 = line.split(" "); //split the line read 
                           MMEX = line2[1].substring(11,30)+" "+line2[14].replace("'", "").replace(",", "") +" "+line2[17].replaceAll(".*//*", "");
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





for (int i = 0; i < result.size(); i++) {
       
    finalresult.add(result.get(i)+"#"+(float) (Long.parseLong(result2.get(i)))/1000000);
  //  List.add(result.get(i)+"/"+result2.get(i));  // timestamp/value
   
}

Collections.sort(finalresult);

System.out.println(finalresult);

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
 
 */
 long endTime   = System.currentTimeMillis();
 long totalTime = endTime - startTime;
 float tTime = (float) totalTime/60000;
 System.out.println("the program took "+tTime+" minutes to ");
 
 
 //finalresult.addAll("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""); 

 finalresult.add("2014-01-06T00#323.90988");
 finalresult.add("2014-01-06T01#229.06938");
 finalresult.add("2014-01-06T02#740.6015");
 finalresult.add("2014-01-06T03#8383.697");
 finalresult.add("2014-01-06T04#8998.523");
 finalresult.add("2014-01-06T05#4822.339");
 finalresult.add("2014-01-06T06#3153.6895");
 finalresult.add("2014-01-06T07#0");
 finalresult.add("2014-01-06T08#0");
 finalresult.add("2014-01-06T09#708.0818");
 finalresult.add("2014-01-06T10#984.643");
 finalresult.add("2014-01-06T11#1032.6699");
 finalresult.add("2014-01-06T12#0");
 finalresult.add("2014-01-06T13#0.002754");
 finalresult.add("2014-01-06T14#0.008262");
 finalresult.add("2014-01-06T15#0.011318");
 finalresult.add("2014-01-06T16#0");
 finalresult.add("2014-01-06T17#3.886679");
 finalresult.add("2014-01-06T18#3.883925");
 finalresult.add("2014-01-06T19#0");
 finalresult.add("2014-01-06T20#0.021995");
 finalresult.add("2014-01-06T21#931.83215");
 finalresult.add("2014-01-06T22#54.01544");  
 finalresult.add("2014-01-06T23#376.354");
 
 finalresult2.add("2014-01-09T00#0");
 finalresult2.add("2014-01-09T01#0");
 finalresult2.add("2014-01-09T02#0");
 finalresult2.add("2014-01-09T03#107.325874");
 finalresult2.add("2014-01-09T04#0.164664");
 finalresult2.add("2014-01-09T05#310.02725");
 finalresult2.add("2014-01-09T06#3430.5542");
 finalresult2.add("2014-01-09T07#984.6627");
 finalresult2.add("2014-01-09T08#78.09194");
 finalresult2.add("2014-01-09T09#1439.7659");
 finalresult2.add("2014-01-09T10#765.5652");
 finalresult2.add("2014-01-09T11#16.601421");
 finalresult2.add("2014-01-09T12#0.002754");
 finalresult2.add("2014-01-09T13#0");
 finalresult2.add("2014-01-09T14#0");
 finalresult2.add("2014-01-09T15#0");
 finalresult2.add("2014-01-09T16#0");
 finalresult2.add("2014-01-09T17#0");
 finalresult2.add("2014-01-09T18#0");
 finalresult2.add("2014-01-09T19#0");
 finalresult2.add("2014-01-09T20#0");
 finalresult2.add("2014-01-09T21#471.00443");
 finalresult2.add("2014-01-09T22#452.7578");  
 finalresult2.add("2014-01-09T23#3078.8188");

      

 
 
 
 
 String T[] = new String[parameter];
 String S[] = {finalresult.get(0).substring(0, 10)};
 double data[][] = new double[1][parameter];
 double amount[] = new double[parameter];
 double amount2[] = new double[parameter];
 
 
 for(int i=0;i<parameter;i++){
   
     T[i] = finalresult.get(i).substring(10, 13);
     amount[i] = Double.parseDouble(finalresult.get(i).substring(14));
     //amount[i] = Double.parseDouble(finalresult2.get(i).substring(14));
     data[0][i] = amount[i];

 }
 

  
 
 
   Grafico chart = new Grafico("Vertical Bar Chart", T, S, data);
   chart.pack();
   RefineryUtilities.centerFrameOnScreen(chart); 
   
   chart.setVisible(true);
   
  
 

      }     
 }