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
package com.oat.explorer.gui.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.utils.AlgorithmUtils;


/**
 * Type: PopulationQualityLineGraph<br/>
 * Date: 24/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 18/01/2007   JBrownlee   Renamed to PopulationQualityLineGraph
 * 
 * </pre>
 */
public class PopulationQualityLineGraph extends JPanel 
    implements ActionListener, AlgorithmEpochCompleteListener, ClearEventListener
{      
    public final static int TOTAL_POINTS = 1000;    
    public final static int MIN=0, AVG=1, MAX=2, BESTPOP = 3, BESTEVER=4;
    
	protected final JFreeChart chart;
	protected final XYSeriesCollection allTraces;	
	protected final XYSeries [] traces;   

    protected boolean [] tracesEnabled;    
    protected JCheckBox [] boxes;
    protected int count = 0;
    protected double bestScoreEver = Double.NaN;

	public PopulationQualityLineGraph()
	{        
        setName(getGraphTitle());
	    // prepare traces
	    traces = prepareTraces();	    
		// add traces to graph		
		allTraces = new XYSeriesCollection();
		for (int i = 0; i < traces.length; i++)
        {
		    allTraces.addSeries(traces[i]);
        }

		chart = ChartFactory.createXYLineChart(getGraphTitle(), getXAxisLabel(), getYAxisLabel(), allTraces, PlotOrientation.VERTICAL, true, true, true);
		ChartPanel chartPanel = new ChartPanel(chart);		
		setName(getGraphTitle());
        
        JPanel controlPanel = prepareControlPanel();
        
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEtchedBorder());
		this.add(chartPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
	}
    
    
    
    public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
    {
        double [] stats = AlgorithmUtils.calculateFitnessStatistics(currentPop, p);
        // check for new best ever
        if(Double.isNaN(bestScoreEver) || p.isBetter(stats[AlgorithmUtils.BEST], bestScoreEver))
        {
            bestScoreEver = stats[AlgorithmUtils.BEST];
        }
        // store stats
        traces[MIN].add(count, stats[AlgorithmUtils.MIN]);
        traces[AVG].add(count, stats[AlgorithmUtils.AVG]);
        traces[MAX].add(count, stats[AlgorithmUtils.MAX]);
        traces[BESTPOP].add(count, stats[AlgorithmUtils.BEST]);
        traces[BESTEVER].add(count, bestScoreEver); // best so far, not best of pop
        count++;
    }
    
    
    public void newSolutionEvent(Solution s)
    {
        traces[1].add(count, s.getScore());
        count++;
    }
	
    protected String getXAxisLabel()
    {
        return "Iteration";
    }
    protected String getYAxisLabel()
    {
        return "Cost";
    }
    public String getGraphTitle()
    {
        return "Run Graph";
    }
   
    protected XYSeries[] prepareTraces()
    {
        return new XYSeries[]
        {
                prepareTrace("Min"),
                prepareTrace("Mean"),
                prepareTrace("Max"),
                prepareTrace("BestInPop"),
                prepareTrace("BestOfRun")
        };
    }
    

    
    protected JPanel prepareControlPanel()
    {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Control Panel"));
        
        tracesEnabled = new boolean[traces.length];
        boxes = new JCheckBox[traces.length];
        
        for (int i = 0; i < traces.length; i++)
        {
            tracesEnabled[i] = true;
            boxes[i] = new JCheckBox(traces[i].getDescription(), true);
            boxes[i].addActionListener(this);
            p.add(boxes[i]);
        }
        
        return p;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        boolean reWork = false;        
        
        // locate the traces to add or remove
        for (int i = 0; i < traces.length; i++)
        {
            if(src == boxes[i])
            {
                boolean checked = boxes[i].isSelected();
                
                // check if a change is required
                if(checked != tracesEnabled[i])
                {
                    reWork = true;
                    tracesEnabled[i] = checked;
                }
            }
        }
        
        // rework as required
        if(reWork)
        {
            // remove all series
            allTraces.removeAllSeries();
            // add all selected series
            for (int i = 0; i < tracesEnabled.length; i++)
            {
                if(tracesEnabled[i])
                {
                    allTraces.addSeries(traces[i]);
                }
            }
        }
    }
    
    public void clear()
    {
        count = 0; // reset
        bestScoreEver = Double.NaN;
        
        for (int i = 0; i < traces.length; i++)
        {   
            traces[i].clear();
        }
    }
	
    protected XYSeries prepareTrace(String aName)
    {
        XYSeries t = new XYSeries(aName, false, true);
        t.setMaximumItemCount(TOTAL_POINTS);
        t.setDescription(aName);
        return t;
    }
	
	protected void addPoint(double x, double y, XYSeries aTrace)
	{
	    aTrace.add(x,y);
	}
}