/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2007  Jason Brownlee

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
package com.oat.explorer.gui.plot;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.explorer.gui.ClearEventListener;

/**
 * Type: SolutionQualityLineGraph<br/>
 * Date: 18/01/2007<br/>
 * <br/>
 * Description: Plots the solution quality of every solution it is notified about
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class SolutionQualityLineGraph extends JPanel 
    implements ClearEventListener, SolutionEvaluationListener
{
    protected final JFreeChart chart;
    protected final XYSeries series; 
    protected final XYSeriesCollection allSeries;
    protected final ChartPanel chartPanel;
    protected long pointNumber;
    
    /**
     * Constructor
     */
    public SolutionQualityLineGraph()
    {
        series = new XYSeries("Quality", false, true);
        
        allSeries = new XYSeriesCollection();
        allSeries.addSeries(series);
        chart = ChartFactory.createXYLineChart(
                "Solution Quality",      // chart title
                "Solution",                      // x axis label
                "Quality",                      // y axis label
                allSeries,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
            );
        chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        setName("Solution Quality");
        add(chartPanel, BorderLayout.CENTER);
        add(prepareControlPanel(), BorderLayout.SOUTH);
    }
    
    protected JCheckBox enabled;
    
    protected JPanel prepareControlPanel()
    {
        enabled = new JCheckBox("Plot Enabled", false);
        enabled.setToolTipText("Enable and disable this plot because it can be slow");
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Control Panel"));
        p.add(enabled);
        return p;
    }

    /**
     * Clear all solutions from the graph
     */
    public void clear()
    {
        pointNumber = 0;
        series.clear();
    }

    /**
     * Add another point to the graph
     */
    public void solutionEvaluatedEvent(Solution evaluatedSolution)
    {
        if(!enabled.isSelected())
        {
            return;
        }
        
        series.add(pointNumber++, evaluatedSolution.getScore());
    }
}
