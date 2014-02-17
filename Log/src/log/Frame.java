/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Frame.java
 *
 * Created on 31-Jan-2014, 09:46:58
 */
package log;
import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
/**
 *
 * @author striker
 */
public class Frame extends javax.swing.JFrame {

    private FirstFrame ini;
    private String currentDate; //the date that is being shown in the chart
    private GChart g; //the current GChart inside the panel
    private Client client;
    private String host;
    private int port;
    private int arrows[][]= {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //this array allows to plot the arrows in the chart 
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //without overlapping them
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //In the current design of the chart, 10 arrows can
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //be deployed in the Y-axis, so every row of the array
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //indicates the position in the chart
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //if a row has value 1, then an arrow has already been
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //deployed in that particular position
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                     {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
    
    /** Creates new form Frame */
    
    
    public Frame(Client client, String host, int port, FirstFrame ff) {
        initComponents();
        setLocationRelativeTo(null);
        this.client = client;
        this.host = host;
        this.port = port;
        this.ini = ff;
       
    }
    
    //when the app is running a new chart is deployed with this method, where "date" is the new chart date
    private void setNewgraph(String date) throws FileNotFoundException, IOException, ParseException{ 
        
        List<String> finalresult = new ArrayList<String>(); 
   
        LogExtracter le = new LogExtracter();
        
        finalresult = le.getDataStored("Successfully stored", date, this.client);
 
        this.jPanel1.remove(13); //remove the chart. The number can change, it depends of the components of the panel

        this.setGraph(finalresult); //set a new graph to the panel with the new data of the selected date
      
        this.jPanel1.repaint(); //reapint the panel
    
    }
    
    
    //same method as before but this time the data of 2 days is deployed in the same chart
    //this method is activated when the "merge" button is pressed
    //the variable data indicates the current day shown in the panel, and the variable "date2" indicates the new day data to be shown
    private void setNewgraph(String date, String date2) throws FileNotFoundException, IOException, ParseException{
        
        List<String> finalresult = new ArrayList<String>();
        List<String> finalresult2 = new ArrayList<String>();

        LogExtracter le = new LogExtracter();
        
        finalresult = le.getDataStored("Successfully stored", date, this.client);
        finalresult2= le.getDataStored("Successfully stored", date2, this.client);
 
        this.jPanel1.remove(13); //remove the chart. The number can change, it depends of the components of the panel

        this.setGraph(finalresult, finalresult2); //set a new graph to the panel with the new data of the selected date
      
        this.jPanel1.repaint(); //reapint the panel
    }
    
    
    //just a method which return the next day depneding of the value given to the parameter
    private String getNextday(String date){
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
                      
            c.setTime(sdf.parse(date));
        } 
        
        
        catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    
     c.add(Calendar.DATE, 1);  // number of days to add
     date = sdf.format(c.getTime());  // dt is now the new date
     
     return date;
        
    }
    
    private String getDaybefore(String date){
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    
     c.add(Calendar.DATE, -1);  // number of days to add
     date = sdf.format(c.getTime());  // dt is now the new date
     
     return date;
        
    }
    
    //a method to set the button into the panel
    //this method should be called every time a new frame is initialized
    private void setButton(String date[]) throws ParseException, FileNotFoundException, IOException{
       
         
     choice1.removeAll(); //to clean the observation list
     currentDate = date[0];
     String nextDay=this.getNextday(currentDate), dayBefore=this.getDaybefore(currentDate);
     
     jButton2.setFont(new Font("",Font.BOLD, 11));
     jButton1.setFont(new Font("",Font.BOLD, 11));
     jRadioButton1.setText(dayBefore);

     jRadioButton2.setText(nextDay);
     
     buttonGroup1.add(jRadioButton1);
     buttonGroup1.add(jRadioButton2);
     jTextField1.setText(currentDate);
     jLabel1.setText("");
     
     List<String> obs = new ArrayList<String>();
     obs = this.getObservation(currentDate);
     choice1.insert("Observation", 0);
     for(int i = 1; i<obs.size()+1;i++){
         choice1.insert(obs.get(i-1), i);
     }
  
    
     
    
        
        g.getChart().setTitle(currentDate);
        jPanel1.setBackground(new Color(204,204,255));
        this.setBackground(new Color(204,204,255));
        this.setResizable(false);
        this.setTitle("Bulk System's Data Transfer Rate Analysis");
        System.gc();
    }
    
    //the method which actually set the chart into the panel
    // the List "finalresult" has the next format: yyyy-mm-ddThh#float, example: 2013-12-01T22#456.234
    //the float value indicates the amount od data transfered in the indicated date
    public void setGraph(List<String> finalresult) throws ParseException, FileNotFoundException, IOException{

        String T[] = new String[finalresult.size()];  //T allocates the hour member of the timestamp
        String S[] = {finalresult.get(0).substring(0, 10)}; //S allocates the day which is being shown in the chart                                                           
        double data[][] = new double[1][finalresult.size()]; //data allocates the amount of data tranfered in the given day      

 
 
         for(int i=0;i<finalresult.size();i++){
   
              T[i] = finalresult.get(i).substring(10, 13);                          
              data[0][i] = Double.parseDouble(finalresult.get(i).substring(14));
            

         }
        
        final GChart chart = new GChart("Bulk System Analisys", T, S, data);
        g = chart;
      
        
        chart.pack();
  
        RefineryUtilities.centerFrameOnScreen(chart);
         
        jPanel1.add(chart.getContentPane());
     
     
        this.setButton(S);
        this.clean();
     
    
     
    }
    
    
    public void setGraph(List<String> finalresult, List<String> finalresult2) throws ParseException, FileNotFoundException, IOException{
        
        
        
        
        String T[] = new String[finalresult.size()];
        String S[] = {finalresult.get(0).substring(0, 10), finalresult2.get(0).substring(0, 10)};
        double data[][] = new double[2][finalresult.size()];
        

 
 
         for(int i=0;i<finalresult.size();i++){
   
              T[i] = finalresult.get(i).substring(10, 13);
              data[0][i] = Double.parseDouble(finalresult.get(i).substring(14));            
              data[1][i] = Double.parseDouble(finalresult2.get(i).substring(14));
            

         }
        
        final GChart chart = new GChart("Bulk System Analisys", T, S, data);
       g = chart;
        
        chart.pack();
  
        RefineryUtilities.centerFrameOnScreen(chart);
         
     jPanel1.add(chart.getContentPane());
     
     
     
     this.setButton(S);
     this.clean();
     
    
     
    }
    
    
    private void setArrow(String i, String e, String u, String o){
      
        
        
        String arrow = "";
        String T = i.substring(11, 13);
        int index = Integer.parseInt(T);
        int ihour = Integer.parseInt(i.substring(11, 13)); //initial hour
        int ehour = Integer.parseInt(e.substring(11, 13)); //ended hour
        int imin = Integer.parseInt(i.substring(14, 16)); //initial minute
        int emin = Integer.parseInt(e.substring(14, 16)); //ended minute
        
        int start = imin/5; //to position the started point in the chart
        
        int duration = ((ehour-ihour)*60)+(emin-imin); //duration of data transfer
        
        
        for(int j=0; j<start; j++){
            arrow = arrow+" ";
        } //start position finished
        
        
        String uid = arrow;
        String obs = arrow;
        
        double x = (duration-1)/7;  // 1 line are 7*2 minutes, so if the chart's size changes, this number can be adjusted
        x = (int) x;
        String gap="";
        
        if(x>=2){
            if(x%2 == 0){
                gap = arrow+"\u2501";
                arrow = arrow+" ";
                
            }
            else if(x%2 != 0){
                x = x+1;
            }
            if(x>3){
                x=x/2; //numero de lineas
                for(int k=0;k<x;k++){
                    arrow=arrow+"\u2501";
                }
            }
            
        }
        
        
        arrow = arrow+"\u2501\u25BA";
        
        uid = uid+u;
        obs=obs+o;
        
        
         int counter = 0;
         int row = 0;
         
         for(int r=row;r<10;r++){
             for(int k = index;k<index+7&&k<24;k++){
                 counter = counter + arrows[r][k]; //should be 0 if it's all clear, gt 0 otherwise
               
             }
             if(counter==0){
                 row = r; //the row where the arrow will be displayed            
                 r = 11; //to exit the loop
                
             }
             else{
                 counter = 0;
             }
         }
         
         final CategoryPlot plot = g.getChart().getCategoryPlot();

         int jump = (int) (plot.getRangeAxis(0).getUpperBound())/10;
         int position = jump/5;
          
          
          
         final CategoryTextAnnotation ca = new CategoryTextAnnotation(arrow,"T"+T,position+(jump*row));
         ca.setFont(new Font("f", Font.PLAIN, 10));
         ca.setTextAnchor(TextAnchor.CENTER_LEFT);
         ca.setCategoryAnchor(CategoryAnchor.START); 
         
         final CategoryTextAnnotation ca2 = new CategoryTextAnnotation(gap,"T"+T,position+(jump*row));
         ca2.setFont(new Font("f", Font.PLAIN, 10));
         ca2.setTextAnchor(TextAnchor.CENTER_LEFT);
         ca2.setCategoryAnchor(CategoryAnchor.START); 
         
         final CategoryTextAnnotation ca3 = new CategoryTextAnnotation(uid,"T"+T,(position+(jump*row))+(jump/3.1));
         ca3.setFont(new Font("f", Font.PLAIN, 12));
         ca3.setTextAnchor(TextAnchor.CENTER_LEFT);
         ca3.setCategoryAnchor(CategoryAnchor.START);
         
         
         final CategoryTextAnnotation ca4 = new CategoryTextAnnotation(obs,"T"+T,(position+(jump*row))+(jump/1.5));
         ca4.setFont(new Font("f", Font.PLAIN, 12));
         ca4.setTextAnchor(TextAnchor.CENTER_LEFT);
         ca4.setCategoryAnchor(CategoryAnchor.START);
         
         plot.addAnnotation(ca);
         plot.addAnnotation(ca2);
         plot.addAnnotation(ca3);
         plot.addAnnotation(ca4);
         
       if(index<18){    
        arrows[row][index]=1;
        arrows[row][index+1]=1;
        arrows[row][index+2]=1;
        arrows[row][index+3]=1;
        arrows[row][index+4]=1;
        arrows[row][index+5]=1;
        arrows[row][index+6]=1;
       }
       else{
           
           for(int w=index;w<24;w++){
               arrows[row][w]=1;
           }
       }
    }
    
    
    private List<String> getObservation(String date) throws FileNotFoundException, IOException{
     List<String> finalresult2 = new ArrayList<String>();
     LogExtracter le = new LogExtracter();   
     
     finalresult2 = le.getObservationTime(date, this.client);
     
     return finalresult2;
     

    }
    
    private void clean(){
          final CategoryPlot plot = g.getChart().getCategoryPlot();
    
    plot.clearAnnotations();
    for(int i=0;i<24;i++){
        arrows[0][i] = 0;
        arrows[1][i] = 0;
        arrows[2][i] = 0;
        arrows[3][i] = 0;
        arrows[4][i] = 0;
        arrows[5][i] = 0;
        arrows[6][i] = 0;
        arrows[7][i] = 0;
        arrows[8][i] = 0;
        arrows[9][i] = 0;
    }
    }
    
    private void setConnection(){
        
        Client newclient = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));
        client = newclient;
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        choice1 = new java.awt.Choice();
        jButton5 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 540));

        jButton1.setText("Next Day");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Previous Day");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Merge");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jRadioButton1.setBackground(new java.awt.Color(-3355393,true));
        jRadioButton1.setText("jRadioButton1");

        jRadioButton2.setBackground(new java.awt.Color(-3355393,true));
        jRadioButton2.setText("jRadioButton2");

        jButton4.setText("Draw Arrow");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        choice1.setBackground(new java.awt.Color(-3355393,true));

        jButton5.setText("Clean");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Go");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setText("Invalid Date");

        jButton7.setText("Reconnect");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Restart");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(698, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addGap(59, 59, 59))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(50, 50, 50))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(jButton8)))
                                .addGap(9, 9, 9))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(choice1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)))
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(jRadioButton2, 0, 0, Short.MAX_VALUE)
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                        .addGap(43, 43, 43))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addGap(8, 8, 8)
                .addComponent(jButton3)
                .addGap(34, 34, 34)
                .addComponent(choice1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addGap(41, 41, 41)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addGap(36, 36, 36)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// "Next Day" button
    
    String date = this.getNextday(currentDate);
        try {
            try {
                this.setNewgraph(date);
            } catch (ParseException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// "Previous Day" button
    
    String date = this.getDaybefore(currentDate);
        try {
            this.setNewgraph(date);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
// "Merge" button
    
    if(jRadioButton1.isSelected()){
            try {
                try {
                    this.setNewgraph(currentDate, this.getDaybefore(currentDate));
                } catch (ParseException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    else if(jRadioButton2.isSelected()){
            try {
                try {
                    this.setNewgraph(currentDate, this.getNextday(currentDate));
                } catch (ParseException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    
    
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
// "Draw Arrow" button
    
        if(!choice1.getSelectedItem().equals("Observation")){
                
                 String obs = choice1.getSelectedItem();
                 String lines[];
                 lines = obs.split(" ");
                            
                 String start = lines[0];
                 String end = lines[1];
                 String uid = lines[2];
                 String ob = lines[3];
              
                 setArrow(start, end, uid, ob); 
        }
      
}//GEN-LAST:event_jButton4ActionPerformed

private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// "Clean" button
    this.clean();
  
}//GEN-LAST:event_jButton5ActionPerformed

private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        
    String date =  jTextField1.getText();
    
    if(date.matches("^((19|20|21)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")){
        
   

      
    
    try {
            // "GO" Button
                
                this.setNewgraph(date);
                
                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    else {
        jLabel1.setText("Invalid Date");
        System.out.println("Please enter a valid date, its format should be yyyy-mm-dd (e.g. 2013-01-01), with a range from 1900-01-01 to 2199-12-31");
    }
    
    
    
}//GEN-LAST:event_jButton6ActionPerformed

private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
// TODO add your handling code here:
    
    client.close();
    this.setConnection();
    
}//GEN-LAST:event_jButton7ActionPerformed

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
// TODO add your handling code here:
    
    this.dispose();
    ini.setVisible(true);
}//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
       
         
         
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                
               
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private java.awt.Choice choice1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables


}
