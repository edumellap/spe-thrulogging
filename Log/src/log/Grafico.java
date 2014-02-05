package log;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


public class Grafico extends ApplicationFrame {
    
    private JFreeChart ch;

  public Grafico(String title, String Timestamp[], String Bars[], double[][] data) {
  super(title);

  final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(Bars, Timestamp, data);
  
  final JFreeChart chart = createChart(dataset,title);
  
  ch = chart;
  chart.setBackgroundPaint(Color.white);
  
   final CategoryPlot plot = chart.getCategoryPlot();
    
   //to set the margins
   CategoryAxis axis = plot.getDomainAxis();
   axis.setLowerMargin(0.03);
   axis.setUpperMargin(0.03);
   axis.setCategoryMargin(0.0);
   BarRenderer renderer = (BarRenderer) plot.getRenderer();
   renderer.setItemMargin(0.0);

  
  
     
     
  /*
  final CategoryMarker target = new CategoryMarker("T04");
 
        target.setLabel("Target Range \n a");
         target.setLabelAnchor(RectangleAnchor.CENTER);
        target.setLabelTextAnchor(TextAnchor.CENTER);
        target.setPaint(Color.LIGHT_GRAY);
        plot.addDomainMarker(target, Layer.FOREGROUND);
    */    
        
  //     final ValueMarker marker = new ValueMarker(1000);  // position is the value on the axis
   //     marker.setPaint(Color.black);
   //     marker.setLabel("here"); // see JavaDoc for labels, colors, strokes

 
     //   plot.addRangeMarker(marker);
        
        
  
  final ChartPanel chartPanel = new ChartPanel(chart);
 
  chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
  setContentPane(chartPanel);
  chartPanel.setMouseWheelEnabled(true);
  

  
  
  }

 

  private JFreeChart createChart(final CategoryDataset dataset, String title) {

  final JFreeChart chart = ChartFactory.createBarChart(          
          title,
          "Hours", 
          "MBytes", 
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