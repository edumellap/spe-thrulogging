package log;
 

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import java.io.*;
import java.io.BufferedReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.apache.lucene.util.Version;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.internal.InternalSearchHit;





 
public class App {
  
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, InterruptedException {
            
         
           
          
            FirstFrame ff = new FirstFrame();
           ff.setVisible(true);
          
                           
            /*
            List<String> finalresult = new ArrayList<String>();
             
            LogExtracter le = new LogExtracter();
            String host = "ariadne.osf.alma.cl";
            int port=15040;
            
            
            
            
            
            
            Date date=new Date();
            String currentDate = le.getTimestamp(date.getTime()).substring(0, 10);
              
            finalresult = le.getDataStored("Successfully stored", currentDate, client);
         
        
            Frame f = new Frame(client, host, port);
            f.setGraph(finalresult);
            f.setVisible(true);//Plot the data
       
        
         */
            }
      
      
      
      
        
       
        
        
 }
