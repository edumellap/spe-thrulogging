package log;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;


public class Grafico extends ApplicationFrame {

  public Grafico(String title, String Timestamp[], String Bars[], double[][] data) {
  super(title);

  final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(Bars, Timestamp, data);
  final JFreeChart chart = createChart(dataset);
  final ChartPanel chartPanel = new ChartPanel(chart);
  
  
  chartPanel.setPreferredSize(new java.awt.Dimension(1000, 540));
  setContentPane(chartPanel);
  chartPanel.setMouseWheelEnabled(true);

  
  
  }

 

  private JFreeChart createChart(final CategoryDataset dataset) {

  final JFreeChart chart = ChartFactory.createBarChart(
  "Bar Chart","Hours", "MBytes", dataset,
  PlotOrientation.VERTICAL, true, true, false);
  
  
  return chart;
  }

  
}