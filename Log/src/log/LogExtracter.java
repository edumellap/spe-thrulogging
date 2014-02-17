/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author striker
 */
public class LogExtracter {
           
      private String prefix = "@fields"; //this prefix vary depending of the DB tha app connect to. With a newer version
                                        //of alogging2mq this should be "_source", and  with an odler version "@field"
    
    public List<String> getDataStored(String pattern, String date, Client client) throws FileNotFoundException, IOException{
            
        
            
            String line = null;          
            String line2[]; //array that has each 'word' of a single line
            String timestamp="0"; //variable which has the timestamp
            float value = 0; //variable which stores the amount of data tranfered
            List<String> result = new ArrayList<String>();  //final array that has each timestamp value 
            List<String> result2 = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp        
            
            String data[];
            String bytes;
            String message, ts;
            List<String> datalist = new ArrayList<String>();
            long from  =  this.getMilliseconds(date);
            
            date = this.getTimestamp(from).substring(0, 10);
            long to = from + 86400000L; //next day
        

      SearchResponse response = client.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", pattern)).setFrom(0).setSize(10000).execute().actionGet();
            
 
            
            long hits = response.getHits().totalHits(); //number of results
            long day;
    
            for(int i = 0; i<hits ; i++){
        
              //timestamp YY/MM/DDTHH    
           
              day = response.getHits().getAt(i).field(prefix+".date").getValue();          
              ts = this.getTimestamp(day).substring(0, 13);
              
        
        
              //bytes written
              message = response.getHits().getAt(i).field(prefix+".logMessage").getValue();
              data = message.split(" ");             
              bytes = data[8];
              
              float mb = (Float.parseFloat(bytes))/1048576;
              bytes = Float.toString(mb);
              
              if(data[0].equals("FLOW")){
                datalist.add(ts+" "+bytes);
              }        
            }
            
            Collections.sort(datalist);
            int datasize = datalist.size();
            
            for(int i = 0; i<datasize;i++){
                                                     
                line = datalist.get(i);       
            
                line2 = line.split(" "); //split the line read
             
                if(timestamp.equals(line2[0])){ //the same timestamp as the before one
                    value = value + Float.parseFloat(line2[1]); //add the value
                }
                                      
                else{ //if it's not the same timestamp as the before one                  
                     if(!timestamp.equals("0")){ //if it's not the first line                       
                         result.add(timestamp+"#"+value);  //add the value to the array                                             
                         timestamp = line2[0];  //start again with the new timestamp
                         value = Float.parseFloat(line2[1]);  //star again with the new value
                     }
                                             
                     else if(timestamp.equals("0")){  //if it's the first line of the file
                         timestamp = line2[0];  //start
                         value = Float.parseFloat(line2[1]);  //start
                     }
                  
                }
            }
            
                if(!timestamp.equalsIgnoreCase("0")){
                    result.add(timestamp+"#"+value);
                }

                Collections.sort(result);
                
                String[] hour = {"T00","T01","T02","T03","T04","T05","T06","T07","T08","T09","T10","T11",
                                  "T12","T13","T14","T15","T16","T17","T18","T19","T20","T21","T22","T23"};
                
                if(result.isEmpty()){
                   result.add(date+"T00"+"#"+"0");
               }
                
                int l = result.size();
                int j = 0;
                
                for(int i = 0;i<l;){
                                                           
                    if(!result.get(i).substring(0, 13).equals(date+hour[j])){
                        result.add(date+hour[j]+"#0");     
                        
                        j++;
                       
                    }
                    else {
                        j++;
                        i++;
                    }
                    
                    
                }
                
                for(int k=j;k<24;k++){
                    result.add(date+hour[k]+"#0");
                }
                
               Collections.sort(result);
     
            
           return result;
        }
    
    public List<String> getObservationTime(String date, Client client) throws FileNotFoundException, IOException{
             
            String line = null; 
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
            List<String> finalresult = new ArrayList<String>();
            
            long from  =  this.getMilliseconds(date);
            long to = from + 86399999L; //next day
            
            SearchResponse response = client.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "Received ScriptInformationEvent for ExecBlock")).setFrom(0).setSize(10000).execute().actionGet();
        
            long hits = response.getHits().totalHits(); //number of results
            long day;
            String message, timestamp;
    
            for(int i = 0; i<hits ; i++){
                         
               day = response.getHits().getAt(i).field(prefix+".date").getValue();  
               timestamp = this.getTimestamp(day);
               message = response.getHits().getAt(i).field(prefix+".logMessage").getValue();
               
               if(message.contains("uid")&&!message.contains("'/X00/X00'")){
                    finalresult.add(timestamp+" "+message);
               
               }
               
            
            }
                 
                                                                                                    
                        //MMEX START
                        for(int i = 0; i<finalresult.size();i++){
                             line = finalresult.get(i);
                             line2 = line.split(" "); //split the line read 
                              MMEX = line2[0]+" "+line2[5].replace("'", "").replace(",", "") +" "+line2[8].replaceAll(".*//*", "");
                             iniTime.add(MMEX);
                       }
                        //MMEX START
                        
                        
                        //SBEX START
                        SearchResponse response3 = client.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "Created new SBEX")).setFrom(0).setSize(10000).execute().actionGet();

      long hits3 = response3.getHits().totalHits(); 
      long day3, from3, day4;
      String message3,timestamp3;
     
                         for(int i = 0; i<hits3 ; i++){
                         
           day3 = response3.getHits().getAt(i).field(prefix+".date").getValue();
           day3 = day3 - 1;
           from3 = day3 - 1000; //1 second before
           
          SearchResponse response4 = client.prepareSearch().addFields(prefix+".logMessage", prefix+".date", prefix+".sourceObject").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from3).to(day3))
.setQuery(QueryBuilders.matchPhraseQuery("sourceObject", "OBOPS_SCHEDULING_EVENT_LISTENER")).setFrom(0).setSize(10000).execute().actionGet();
long hits4 = response4.getHits().totalHits();
if(hits4>0){
                              day4 = response4.getHits().getAt(0).field(prefix+".date").getValue();
                              timestamp3 = this.getTimestamp(day4);
                              message3 = response4.getHits().getAt(0).field(prefix+".logMessage").getValue();
                              line2 = message3.split(" ");
                              if(line2.length>6){
                              if(line2[6].equalsIgnoreCase("started")){
                                 SBEX = timestamp3+" "+line2[4].replace("'", "").replace(",", "")+" SBEX";    
                                  iniTime.add(SBEX);
                              }
                              }
} 
                                
                        }
                              
                              
                        //SBEX START
                        
                        
                        
                        
                        
                        
                        
                        //END
                        SearchResponse response2 = client.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "which ended with status")).setFrom(0).setSize(10000).execute().actionGet();

                        long hits2 = response2.getHits().totalHits(); //number of results
                        long day2;
                        String message2, timestamp2;
                        List<String> finalresult2 = new ArrayList<String>();
                        
                        for(int i = 0; i<hits2 ; i++){
                         
                               day2 = response2.getHits().getAt(i).field(prefix+".date").getValue();  
                               timestamp2 = this.getTimestamp(day2);
                               message2 = response2.getHits().getAt(i).field(prefix+".logMessage").getValue();
               
              
                               finalresult2.add(timestamp2+" "+message2);

                        }
                        
                        for(int i=0;i<finalresult2.size();i++){
                               line = finalresult2.get(i);
                               line2 = line.split(" ");
                               END = line2[0]+" "+line2[5].replace("'", "").replace(",", "");
                               finTime.add(END);                        
                        }
                        //END
             
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
    
       /**
     * Method to convert a millisecond date to its String equivalent
     * The date is returned in GMT
     * @param milliseconds
     * @return  A String date corresponding to the given milliseconds (e.g. "2014-01-01T23:55:09")
     */
       public String getTimestamp(long milliseconds){
         
             Date date=new Date(milliseconds);
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             sdf.setTimeZone(TimeZone.getTimeZone("GMT-3")); //GMT for ariadne
             String s = sdf.format(date);
             String timestamp = s.replace(" ", "T");
             
             return timestamp;
       } 
       
       /**
        * The date should be formatted as YYYY-MM-DD or YYYY/MM/DD.
        * Method to convert a String date to its millisecond equivalent.
        * The date is returned in GMT.
        * @param date
        * @return A long number corresponding to the given date in milliseconds
        */
       public long getMilliseconds(String date){
             
             date = date.replace("-", "/");
             Date newdate = new Date(date);
            
             long l = newdate.getTime()/*-10800000L*/; //3 hours less (for ariadne)
             
             return l;
       }
       
     
    
}
