/*
 Optimization Algorithm Toolkit (OAT)
 http://sourceforge.net/projects/optalgtoolkit
 Copyright (C) 2006  Jason Brownlee

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.experimenter.gui.plots;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 * Description: Generic box plot panel for algorithm by problem results
 * Based on the example provided in http://www.java2s.com/Code/Java/Chart/JFreeChartBoxAndWhiskerDemo.htm
 *  
 * Date: 27/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BoxPlot extends JPanel
{	
	protected DefaultBoxAndWhiskerCategoryDataset dataset;	
	protected NumberAxis yAxis;
	protected CategoryAxis xAxis;
	protected JFreeChart chart;	
	protected CategoryPlot plot;
	
	protected boolean groupByAlgorithm = true;
	
	/**
	 * Default Constructor
	 */
	public BoxPlot()
	{
		createGUI();
	}	
	
	/**
	 * Creates the Panel
	 */
	protected void createGUI()
	{	
		// prepare the data set
		dataset = new DefaultBoxAndWhiskerCategoryDataset();		
		// prepare axis
		xAxis = new CategoryAxis("Algorithm");	
		yAxis = new NumberAxis("Result");				
		yAxis.setAutoRangeIncludesZero(false);		
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
	    renderer.setFillBox(true);  	    
	    renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
	    plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);	    
		// prepare the chart	
	    chart = new JFreeChart(
	            "Box-and-Whisker Plot",
	            new Font("SansSerif", Font.BOLD, 11),
	            plot,
	            true);	    
		// prepare the chart panel
	    ChartPanel chartPanel = new ChartPanel(chart);	
		// layout
		setLayout(new BorderLayout());
		add(chartPanel, BorderLayout.CENTER);        
	}
	
	public void clear()
	{
		// lazy, just blow away the entire dataset (memory leak possibility???)
		dataset = new DefaultBoxAndWhiskerCategoryDataset();
		plot.setDataset(dataset);
	}
	
	public void setYAxisLabel(String name)
	{
		yAxis.setLabel(name);
	}
	
	public void setXAxisLabel(String name)
	{
		xAxis.setLabel(name);
	}
	
	public void setChartLabel(String name)
	{
		chart.setTitle(name);
	}
	
	/**
	 * Add a result to the plot where results are grouped by algorithm 
	 * and in series with the problem
	 * 
	 * @param results
	 * @param algorithmName
	 * @param problemName
	 */
	public void addBoxAndWhiskerItem(double [] results, String algorithmName, String problemName)
	{				
		ArrayList<Double> list = new ArrayList<Double>(results.length);
		for (int i = 0; i < results.length; i++)
		{
			list.add(results[i]);
		}
		// problem is the series (row), algorithm is the type (column)
		if(groupByAlgorithm)
		{
			dataset.add(list, problemName, algorithmName);
		}
		// algorithm is the series (row), problem is the type (column)
		else
		{
			dataset.add(list, algorithmName, problemName);
		}
	}

	public boolean isGroupByAlgorithm()
	{
		return groupByAlgorithm;
	}

	public void setGroupByAlgorithm(boolean groupByAlgorithm)
	{
		this.groupByAlgorithm = groupByAlgorithm;
	}	
}
