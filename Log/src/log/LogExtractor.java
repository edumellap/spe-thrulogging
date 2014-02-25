/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;



import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.FileNotFoundException;
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
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;

/**
 *
 * @author striker
 */
public class LogExtractor {
           
      private String prefix = "_source"; //this prefix varies depending of the DB tha app connect to. With a newer version
                                        //of alogging2mq this should be "_source", and  with an odler version "@field"
    
    public List<String> getDataStored(String date, Client esClient, MongoClient mongoClient, int indicator) throws FileNotFoundException, IOException{
            
        //indicator = 0 means, MB/Hours
        //indicator = 1,2,3,4,5,6 means, MB/min
            
            String line = null;          
            String line2[]; //array that has each 'word' of a single line
            String timestamp="0"; //variable which has the timestamp
            float value = 0; //variable which stores the amount of data tranfered
            List<String> result = new ArrayList<String>();  //final array that has each timestamp value 
            String data[];
            String bytes;
            String message, ts = null;
            List<String> datalist = new ArrayList<String>();
            long from = 0, to = 0;
            
            if(indicator==0 ){
                from  =  this.getMilliseconds(date);            
                date = this.getTimestamp(from).substring(0, 10);
                to = from + 86400000L; //next day
            }
            else if(indicator>0 && indicator<7){
                from  =  this.getMilliseconds(date)+(14400000L*(indicator-1));            
                date = this.getTimestamp(from).substring(0, 10);
                to = from + 14400000L; //next day
            }

            if(mongoClient != null){
                
                long day;
                
                DB db = mongoClient.getDB( "almalogs" );        
                DBCollection coll = db.getCollection("almalogs");

                BasicDBObject query = new BasicDBObject();
                query.put("logMessage", java.util.regex.Pattern.compile("Successfully stored"));
                query.put("date", new BasicDBObject("$gte", from).append("$lt", to));
        
                DBCursor cursor = coll.find(query);
                DBObject ob = new BasicDBObject();
       
                while(cursor.hasNext()) {
                     ob = cursor.next();
                     
                     message = ob.get("logMessage").toString();
                     day = Long.parseLong(ob.get("date").toString());
              
                     if(indicator==0){
                           ts = this.getTimestamp(day).substring(0, 13);
                     }
                     else if(indicator>0 && indicator<7){
                           ts = this.getTimestamp(day).substring(0, 15);
                     }
                     data = message.split(" ");             
                     bytes = data[8];
              
                     float mb = (Float.parseFloat(bytes))/1048576;
                     bytes = Float.toString(mb);
              
                     if(data[0].equals("FLOW")){
                            datalist.add(ts+" "+bytes);
                     }    
                   
                 }
            }
            
            else if(esClient != null){
                
      SearchResponse response = esClient.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "Successfully stored")).setFrom(0).setSize(10000).execute().actionGet();
            
 
            
            long hits = response.getHits().totalHits(); //number of results
            long day;
    
            for(int i = 0; i<hits ; i++){
        
              //timestamp YY/MM/DDTHH    
           
              day = response.getHits().getAt(i).field(prefix+".date").getValue(); 
              
              if(indicator==0 ){
                     ts = this.getTimestamp(day).substring(0, 13);
              }
              else if(indicator>0 && indicator<7){
                     ts = this.getTimestamp(day).substring(0, 15);
              }
        
        
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
              
                List<String> finaldata = this.parseData(result, date, indicator);
               
     
            
           return finaldata;
        }
    
    public List<String> getObservationTime(String date, Client esClient, MongoClient mongoClient, int indicator) throws FileNotFoundException, IOException{
             
            String line = null;          
            String line2[]; //array that has each 'word' of a single line    
            String MMEX, SBEX, END;
            List<String> iniTime = new ArrayList<String>();  //final array that has each timestamp value 
            List<String> finTime = new ArrayList<String>(); //final array that has the value of the data flow of each timestamp
            
            List<String> finalresult = new ArrayList<String>();
            
            long from  =  0;
            long to = 0;
            
            if(indicator==0 ){
                from  =  this.getMilliseconds(date);            
                date = this.getTimestamp(from).substring(0, 10);
                to = from + 86400000L; //next day
            }
            else if(indicator>0 && indicator<7){
                from  =  this.getMilliseconds(date)+(14400000L*(indicator-1));            
                date = this.getTimestamp(from).substring(0, 10);
                to = from + 14400000L; //next day
            }
            
            if(mongoClient!=null){
                long day;
                String message, timestamp;
                DB db = mongoClient.getDB( "almalogs" );        
                DBCollection coll = db.getCollection("almalogs");

                BasicDBObject query = new BasicDBObject();
                query.put("logMessage", java.util.regex.Pattern.compile("Received ScriptInformationEvent for ExecBlock"));
                query.put("date", new BasicDBObject("$gte", from).append("$lt", to));
        
                DBCursor cursor = coll.find(query);
                DBObject ob = new BasicDBObject();
       
                while(cursor.hasNext()) {
                      ob = cursor.next();
                      message = ob.get("logMessage").toString();
                      day = Long.parseLong(ob.get("date").toString());
                      timestamp=this.getTimestamp(day);
                      if(message.contains("uid")&&!message.contains("'/X00/X00'")){
                         finalresult.add(timestamp+" "+message);
               
                      }
                }
            }
            
            else if(esClient!=null){
                
            SearchResponse response = esClient.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
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
                        
                        if(mongoClient!=null){
                             
                              long day3, from3, day4;
                              String message3,timestamp3;
                              
                              DB db2 = mongoClient.getDB( "almalogs" );        
                              DBCollection coll2 = db2.getCollection("almalogs");

                              BasicDBObject query2 = new BasicDBObject();
                              query2.put("logMessage", java.util.regex.Pattern.compile("Created new SBEX"));
                              query2.put("date", new BasicDBObject("$gte", from).append("$lt", to));
        
                              DBCursor cursor2 = coll2.find(query2);
                              DBObject ob2 = new BasicDBObject();
       
                              while(cursor2.hasNext()) {
                                  
                                  ob2 = cursor2.next();
                                  day3 = Long.parseLong(ob2.get("date").toString());
                                  day3 = day3 - 1;
                                  from3 = day3 - 1000; //1 second before
                                  
                                  
                                  BasicDBObject query3 = new BasicDBObject();
                                  query3.put("sourceObject", java.util.regex.Pattern.compile("OBOPS_SCHEDULING_EVENT_LISTENER"));
                                  query3.put("date", new BasicDBObject("$gte", from3).append("$lt", day3));
                                  DBCursor cursor3 = coll2.find(query3);
                                  DBObject ob3 = new BasicDBObject();
                                  
                                 
                                  if(cursor3.size()>0){
                                         ob3 = cursor3.next();
                                         day4 = Long.parseLong(ob3.get("date").toString());
                                         timestamp3 = this.getTimestamp(day4);
                                         message3 = ob3.get("logMessage").toString();
                                         line2 = message3.split(" ");
                                         if(line2.length>6){
                                                if(line2[6].equalsIgnoreCase("started")){
                                                      SBEX = timestamp3+" "+line2[4].replace("'", "").replace(",", "")+" SBEX";    
                                                      iniTime.add(SBEX);
                                                }
                                         }
                                 } 
                              }
                         }
                        
                        else if(esClient!=null){
                        SearchResponse response3 = esClient.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "Created new SBEX")).setFrom(0).setSize(10000).execute().actionGet();

      long hits3 = response3.getHits().totalHits(); 
      long day3, from3, day4;
      String message3,timestamp3;
     
                         for(int i = 0; i<hits3 ; i++){
                         
           day3 = response3.getHits().getAt(i).field(prefix+".date").getValue();
           day3 = day3 - 1;
           from3 = day3 - 1000; //1 second before
           
          SearchResponse response4 = esClient.prepareSearch().addFields(prefix+".logMessage", prefix+".date", prefix+".sourceObject").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from3).to(day3))
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
                              
    }     
                        //SBEX START
                        
                        
                        
                        
                        
                        
                        
                        //END
                        List<String> finalresult2 = new ArrayList<String>();
                        
                        if(mongoClient!=null){
                            long day2;
                        String message2, timestamp2;
                       
                             DB db3 = mongoClient.getDB( "almalogs" );        
                             DBCollection coll3 = db3.getCollection("almalogs");

                             BasicDBObject query4 = new BasicDBObject();
                             query4.put("logMessage", java.util.regex.Pattern.compile("which ended with status"));
                             query4.put("date", new BasicDBObject("$gte", from).append("$lt", to));
        
                             DBCursor cursor4 = coll3.find(query4);
                             DBObject ob4 = new BasicDBObject();
       
                              while(cursor4.hasNext()) {
                                 ob4 = cursor4.next();
                                 day2 = Long.parseLong(ob4.get("date").toString());  
                                 timestamp2 = this.getTimestamp(day2);
                                 message2 = ob4.get("logMessage").toString();
                           
                                finalresult2.add(timestamp2+" "+message2);
                                
                              }
                        }
                        
                       else if(esClient!=null){
                        SearchResponse response2 = esClient.prepareSearch().addFields(prefix+".logMessage", prefix+".date").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFilter(FilterBuilders.rangeFilter("date").from(from).to(to))
.setQuery(QueryBuilders.matchPhraseQuery("logMessage", "which ended with status")).setFrom(0).setSize(10000).execute().actionGet();

                        long hits2 = response2.getHits().totalHits(); //number of results
                        long day2;
                        String message2, timestamp2;
                        
                        
                        for(int i = 0; i<hits2 ; i++){
                         
                               day2 = response2.getHits().getAt(i).field(prefix+".date").getValue();  
                               timestamp2 = this.getTimestamp(day2);
                               message2 = response2.getHits().getAt(i).field(prefix+".logMessage").getValue();
               
              
                               finalresult2.add(timestamp2+" "+message2);

                        }
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
          
          
          List<String> finaldata = this.matchObservation(iniTime, finTime);
         
         return finaldata;
         
         }
    private List<String> matchObservation(List<String> iniTime, List<String> finTime){
     
        List<String> obs = new ArrayList<String>();  //final array that has each timestamp value
        String start[], end[], last;
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
    private List<String> parseData( List<String> result, String date, int indicator){
        
         String[] hour = null;
                
                if(indicator==0 ){
                
                    
                    //this is to make sure that all the hours have a range value, so if an hour is not found, it is added
                    //with value 0
                    
                String[] h = {"T00","T01","T02","T03","T04","T05","T06","T07","T08","T09","T10","T11",
                                  "T12","T13","T14","T15","T16","T17","T18","T19","T20","T21","T22","T23"};
           
                if(result.isEmpty()){
                   result.add(date+"T00"+"#"+"0");
                }
                hour = h;
                
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
                
                
                
                
                }
                
                else if(indicator>0 && indicator<7){
                     String[] h = {"T00:0","T00:1","T00:2","T00:3","T00:4","T00:5","T01:0","T01:1","T01:2","T01:3","T01:4","T01:5"
                                ,"T02:0","T02:1","T02:2","T02:3","T02:4","T02:5","T03:0","T03:1","T03:2","T03:3","T03:4","T03:5"
                              
                                ,"T04:0","T04:1","T04:2","T04:3","T04:4","T04:5","T05:0","T05:1","T05:2","T05:3","T05:4","T05:5"
                                ,"T06:0","T06:1","T06:2","T06:3","T06:4","T06:5","T07:0","T07:1","T07:2","T07:3","T07:4","T07:5"
                              
                                ,"T08:0","T08:1","T08:2","T08:3","T08:4","T08:5","T09:0","T09:1","T09:2","T09:3","T09:4","T09:5"
                                ,"T10:0","T10:1","T10:2","T10:3","T10:4","T10:5","T11:0","T11:1","T11:2","T11:3","T11:4","T11:5"
                               
                                ,"T12:0","T12:1","T12:2","T12:3","T12:4","T12:5","T13:0","T13:1","T13:2","T13:3","T13:4","T13:5"
                                ,"T14:0","T14:1","T14:2","T14:3","T14:4","T14:5","T15:0","T15:1","T15:2","T15:3","T15:4","T15:5"
                                
                                ,"T16:0","T16:1","T16:2","T16:3","T16:4","T16:5","T17:0","T17:1","T17:2","T17:3","T17:4","T17:5"
                                ,"T18:0","T18:1","T18:2","T18:3","T18:4","T18:5","T19:0","T19:1","T19:2","T19:3","T19:4","T19:5"
                                
                                ,"T20:0","T20:1","T20:2","T20:3","T20:4","T20:5","T21:0","T21:1","T21:2","T21:3","T21:4","T21:5"
                                ,"T22:0","T22:1","T22:2","T22:3","T22:4","T22:5","T23:0","T23:1","T23:2","T23:3","T23:4","T23:5"};
                 
                     if(result.isEmpty()){
                         if(indicator<4){
                            result.add(date+"T0"+(4*(indicator-1))+":0"+"#"+"0");
                         }
                         else if(indicator>=4){
                            result.add(date+"T"+(4*(indicator-1))+":0"+"#"+"0"); 
                         }
                         
                     }
                     hour=h;
                     
                     
                     int l = result.size();
                     int j = 24*(indicator-1);
                     int n = 24*(indicator-1);
                
                for(int i = 0;i<l;){
                                                           
                    if(!result.get(i).substring(0, 15).equals(date+hour[j])){
                        result.add(date+hour[j]+"#0");     
                        
                        j++;
                       
                    }
                    else {
                        j++;
                        i++;
                    }
                    
                    
                }
                
                for(int k=j;k<n+24;k++){
                    result.add(date+hour[k]+"#0");
                }
                     
                     
                }
                
                
                
                
                
                
               Collections.sort(result);
               
               return result;
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
