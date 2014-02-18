package log;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;



public class GChart extends ApplicationFrame {
    
  private JFreeChart ch;

  public GChart(String title, String Timestamp[], String Bars[], double[][] data, String domain, String range) throws IOException {
  super(title);

  final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(Bars, Timestamp, data);
  
  final JFreeChart chart = createChart(dataset,title, domain, range);
  
  ch = chart;

  
  
  
   final CategoryPlot plot = chart.getCategoryPlot();
    
   //to set the margins
   CategoryAxis axis = plot.getDomainAxis();
   axis.setLowerMargin(0.03);
   axis.setUpperMargin(0.13);
   axis.setCategoryMargin(0.0);
   BarRenderer renderer = (BarRenderer) plot.getRenderer();
   renderer.setItemMargin(0.0);
   axis.setTickLabelFont(new Font("",Font.BOLD, 10));
   axis.setTickLabelPaint(Color.white);
   axis.setLabelPaint(Color.white);
   
   plot.getRangeAxis().setTickLabelFont(new Font("",Font.BOLD,12));
   plot.getRangeAxis().setTickLabelPaint(Color.white);
   plot.getRangeAxis().setLabelPaint(Color.white);
   
   Image img = null;
   try {
   img = ImageIO.read(new File("image/alma.jpg"));
   } catch (Exception e) {
        e.fillInStackTrace();
   }
   plot.setBackgroundPaint(new Color(204,204,255));
   chart.setBackgroundImage(img);
   chart.setBackgroundPaint(Color.BLUE);
   chart.getTitle().setPaint(Color.white);
   

  final ChartPanel chartPanel = new ChartPanel(chart);
 
  chartPanel.setPreferredSize(new java.awt.Dimension(1040, 540));
  setContentPane(chartPanel);
  chartPanel.setMouseWheelEnabled(true);

  
  }

 

  private JFreeChart createChart(final CategoryDataset dataset, String title, String domain, String range) {

  final JFreeChart chart = ChartFactory.createBarChart(          
          title,
          domain, 
          range, 
          dataset,
          PlotOrientation.VERTICAL, 
          true, //incude legends
          true, //tooltips
          false); //URL
  
  
  return chart;
  }

  
  
  public JFreeChart getChart(){
      return ch;
  }
  
}