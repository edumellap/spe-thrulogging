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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.ui.RefineryUtilities;
/**
 *
 * @author striker
 */
public class Frame extends javax.swing.JFrame {

    String currentDate;
    /** Creates new form Frame */
    public Frame() {
        initComponents();
      
       
    }
    
    //when the app is running a new chart is deployed with this method, where "date" is the new chart date
    private void setNewgraph(String date){ 
        
        List<String> finalresult = new ArrayList<String>(); 
        
        
        //This is something tempora, should be replaced by an elasticsearch query to get the data or, if it took to long, by a buffer
        //read of the final data
        BufferedReader reader2 = null;
       
        try {
            reader2 = new BufferedReader(new FileReader("/media/Respaldo/log/data/"+date+".txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String linea;
        
        try {
            while (  (linea = reader2.readLine()) != null) {
            finalresult.add(linea);
        }                        
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //This is something temporal, just adding the needed data to the List    
        
        this.jPanel1.remove(6); //remove the chart. The number can change, it depends of the components of the panel
        this.dispose(); //totally delete the frame
        
         try {
             this.setGraph(finalresult); //set a new graph to the panel with the new data of the selected date
             
        } catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        this.setVisible(true); //deploy the frame
    
    }
    
    
    //same method as before but this time the data of 2 days is deployed in the same chart
    //this method is activated when the "merge" button is pressed
    //the variable data indicates the current day shown in the panel, and the variable "date2" indicates the new day data to be shown
    private void setNewgraph(String date, String date2){
        
        List<String> finalresult = new ArrayList<String>();
        List<String> finalresult2 = new ArrayList<String>();
 
     BufferedReader reader2 = null;
        try {
            reader2 = new BufferedReader(new FileReader("/media/Respaldo/log/data/"+date+".txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
     String linea;
        try {
            while (  (linea = reader2.readLine()) != null) {
            finalresult.add(linea);
        }
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        BufferedReader reader3 = null;
        try {
            reader3 = new BufferedReader(new FileReader("/media/Respaldo/log/data/"+date2+".txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
     String linea2;
        try {
            while (  (linea2 = reader3.readLine()) != null) {
            finalresult2.add(linea2);
        }
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
            
     this.jPanel1.remove(6); //remove the chart
     this.dispose();
        try {
            this.setGraph(finalresult, finalresult2);
        } catch (ParseException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
     this.setVisible(true);
    
    }
    
    
    //just a method which return the next day depneding of the value given to the parameter
    private String getNextday(String date){
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException ex) {
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
    private void setButton(String date[]) throws ParseException{
        
     currentDate = date[0];
     String nextDay, dayBefore;
     
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     Calendar c = Calendar.getInstance();
     c.setTime(sdf.parse(currentDate));
    
     c.add(Calendar.DATE, 1);  // number of days to add
     nextDay = sdf.format(c.getTime());  // dt is now the new date
    
     c.add(Calendar.DATE, -2);  // number of days to add
     dayBefore = sdf.format(c.getTime());  // dt is now the new date
     
     jLabel1.setFont(new Font("Verdana", Font.BOLD, 16));
     jLabel1.setText(currentDate);
     jLabel1.setForeground(Color.blue);
     
     jRadioButton1.setText(dayBefore);

     jRadioButton2.setText(nextDay);
     
     buttonGroup1.add(jRadioButton1);
     buttonGroup1.add(jRadioButton2);
     
    }
    
    //the method which actually set the chart into the panel
    // the List "finalresult" has the next format: yyyy-mm-ddThh#float, example: 2013-12-01T22#456.234
    //the float value indicates the amount od data transfered in the indicated date
    public void setGraph(List<String> finalresult) throws ParseException{
        
        String T[] = new String[finalresult.size()];  //T allocates the hour member of the timestamp
        String S[] = {finalresult.get(0).substring(0, 10)}; //S allocates the day which is being shown in the chart                                                           
        double data[][] = new double[1][finalresult.size()]; //data allocates the amount of data tranfered in the given day      

 
 
         for(int i=0;i<finalresult.size();i++){
   
              T[i] = finalresult.get(i).substring(10, 13);                          
              data[0][i] = Double.parseDouble(finalresult.get(i).substring(14));
            

         }
        
        final Grafico chart = new Grafico("Vertical Bar Chart", T, S, data);
        chart.pack();
  
        RefineryUtilities.centerFrameOnScreen(chart);
         
     jPanel1.add(chart.getContentPane());
     
     
     this.setButton(S);
     
     
    
     
    }
    
    
    public void setGraph(List<String> finalresult, List<String> finalresult2) throws ParseException{
        
        String T[] = new String[finalresult.size()];
        String S[] = {finalresult.get(0).substring(0, 10), finalresult2.get(0).substring(0, 10)};
        double data[][] = new double[2][finalresult.size()];
        

 
 
         for(int i=0;i<finalresult.size();i++){
   
              T[i] = finalresult.get(i).substring(10, 13);
              data[0][i] = Double.parseDouble(finalresult.get(i).substring(14));            
              data[1][i] = Double.parseDouble(finalresult2.get(i).substring(14));
            

         }
        
        final Grafico chart = new Grafico("Vertical Bar Chart", T, S, data);
        chart.pack();
  
        RefineryUtilities.centerFrameOnScreen(chart);
         
     jPanel1.add(chart.getContentPane());
     
     
     this.setButton(S);
     
     
    
     
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
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1200, 540));

        jButton1.setText("Next Day");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Day Before");
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

        jRadioButton1.setText("jRadioButton1");

        jRadioButton2.setText("jRadioButton2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(211, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(jButton3))))
                .addGap(52, 52, 52))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(52, 52, 52)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
    
    String date = this.getNextday(currentDate);
    this.setNewgraph(date);
    
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
    String date = this.getDaybefore(currentDate);
    this.setNewgraph(date);
    
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
// TODO add your handling code here:
    
    if(jRadioButton1.isSelected()){
        this.setNewgraph(currentDate, this.getDaybefore(currentDate));
    }
    else if(jRadioButton2.isSelected()){
        this.setNewgraph(currentDate, this.getNextday(currentDate));
    }
    
    
    
    
}//GEN-LAST:event_jButton3ActionPerformed

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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables
}
