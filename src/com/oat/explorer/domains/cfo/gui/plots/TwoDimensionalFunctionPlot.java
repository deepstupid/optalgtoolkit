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
package com.oat.explorer.domains.cfo.gui.plots;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.InterpolatedPlot;

import com.oat.Algorithm;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.cfo.CFOProblem;
import com.oat.domains.cfo.CFOSolution;
import com.oat.explorer.gui.AlgorithmRunStateChangedListener;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;



/**
 * Type: TwoDimensionalFunctionPlot<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 08/07/2007   JBrownlee   Modified to accept whatever function is provided, as long as it is 2D
 * </pre>
 */
public class TwoDimensionalFunctionPlot extends GenericProblemPlot
    implements SolutionEvaluationListener, ClearEventListener, ActionListener, AlgorithmRunStateChangedListener
{
    public final static int RESOLUTION = 100;
    
    public final static int QUEUE_SIZE = 1000;
    
	protected CFOProblem problem;
    protected final LinkedList<CFOSolution> solutionsQueue;
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected InterpolatedPlot plot;       

    protected JButton clearButton;
    protected JCheckBox showOptima;
    
    public TwoDimensionalFunctionPlot()
    {   
    	solutionsQueue = new LinkedList<CFOSolution>();        
        prepareGui();
    }
    
    protected void prepareGui()
    {        
        setName("2D Plot");        
        setLayout(new BorderLayout());
        drawingPanel = preparePlot();
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        showOptima = new JCheckBox("Show optima", true);
        showOptima.addActionListener(this);
        JPanel p = new JPanel();
        p.add(clearButton);
        p.add(showOptima);
        
        clearButton.setToolTipText("Clear all samples from the plot");
        showOptima.setToolTipText("Whether or not to show function optima (if known)");
        
        add(drawingPanel, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
    } 
    
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if(src == clearButton)
        {
            clear();
        }
        else if(src == showOptima)
        {
            drawingPanel.repaint();
        }
    }
    
	protected DrawingPanel preparePlot()
	{
        gridPointData = new GridPointData(RESOLUTION, RESOLUTION, 1);		
		plot = new InterpolatedPlot(gridPointData);        
		DrawingPanel dp = new SimpleDrawingPanel();
        dp.addDrawable(plot);	
		return dp;
	}
    
    private void updateBaseImage()
    {
        populationGridWithFunction(gridPointData, problem);
        plot.setGridData(gridPointData);
        plot.update();
    }    

    public void populationGridWithFunction(GridPointData pointdata, CFOProblem p)
	{	
		// set the scale
	    double [][] minMax = p.getMinmax();
		pointdata.setScale(minMax[0][0], minMax[0][1], minMax[1][0], minMax[1][1]);
		
		double [][][] data = pointdata.getData();
		
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				// calculate z
				data[x][y][2] = p.directFunctionEvaluation(data[x][y]);
			}
		}
	}    
    
    public void clear()
    {
        synchronized (solutionsQueue)
        {
        	solutionsQueue.clear();   
        }
        drawingPanel.repaint();
    }        
    public void solutionEvaluatedEvent(Solution s)
    {
    	addSolution(s);
        // redraw the plot
        drawingPanel.repaint();
    }
    
    
    protected void addSolution(Solution s)
    {
    	synchronized(solutionsQueue)
    	{
            // must have a problem
            if(problem == null)
            {
                return;
            }
            // add to the front
            solutionsQueue.addFirst((CFOSolution)s);
            // delete from the end until the size is suitable
            while(solutionsQueue.size()>QUEUE_SIZE)
            {
            	solutionsQueue.removeLast();
            }
    	}
    }
    
    public void problemChangedEvent(Problem p)
    {
        CFOProblem f = (CFOProblem) p;        
        
        synchronized(this)
        {            
            if(f.isDimensionalitySupported(2) && f.getDimensions() == 2)
            {
                problem = f;               

                // update controls
                clearButton.setEnabled(true);
                if(problem.getGlobalOptima() == null)
                {
                    showOptima.setEnabled(false);
                }
                else
                {
                    showOptima.setEnabled(true);
                }
                
                // populate the grid
                updateBaseImage();
            }
            else
            {
                problem = null;
                clearButton.setEnabled(false);
                showOptima.setEnabled(false);
            }
        }
        clear();
        repaint();
    }



    public void algorithmFinishNotify(Problem p, Algorithm a)
    {
        if(problem == null){return;}
        clearButton.setEnabled(true);        
    }

    public void algorithmStartNotify(Problem p, Algorithm a)
    {
        if(problem == null){return;}        
        clearButton.setEnabled(false);
    }



    /**
     * Type: SimpleDrawingPanel<br/>
     * Date: 10/03/2006<br/>
     * <br/>
     * Description:
     * <br/>
     * @author Jason Brownlee
     */
    protected class SimpleDrawingPanel extends DrawingPanel
    {
        public final static int BEST_COORD_WIDTH = 22;
        public final static int BEST_COORD_RADIUS = (BEST_COORD_WIDTH/2);
        public final static int SOLUTION_WIDTH = 2;
        public final static int SOLUTION_RADIUS = SOLUTION_WIDTH/2;        
        
        
        public SimpleDrawingPanel()
        {
            setSquareAspect(true);
            enableInspector(true);
        }        
        @Override
        public void paintComponent(Graphics g)
        {
            if(problem == null)
            {
                plotUnavailable(g);
                return;
            }
            
            Graphics2D g2d = (Graphics2D) g;
            // draw the plot
            super.paintComponent(g);    
            // draw the points
            drawPoints(g2d);
            // draw optima
            drawOptima(g2d);
        }        
        
        protected void drawOptima(Graphics2D g)
        {
            CFOSolution [] optima = problem.getGlobalOptima();
            if(optima != null)
            {
                if(!showOptima.isSelected())
                {
                    return;
                }
                
                g.setColor(Color.WHITE);
                for (int i = 0; i < optima.length; i++)
                {
                	double [] v = optima[i].getCoordinate();
                    g.drawOval(xToPix(v[0])-BEST_COORD_RADIUS, yToPix(v[1])-BEST_COORD_RADIUS, BEST_COORD_WIDTH, BEST_COORD_WIDTH);
                }
                g.setColor(Color.BLACK);
                for (int i = 0; i < optima.length; i++)
                {
                	double [] v = optima[i].getCoordinate();
                    g.drawOval(xToPix(v[0])-BEST_COORD_RADIUS-1, yToPix(v[1])-BEST_COORD_RADIUS-1, BEST_COORD_WIDTH+2, BEST_COORD_WIDTH+2);
                }
            }
        }
        
        protected void drawPoints(Graphics2D g)
        {             
            synchronized(solutionsQueue)
            {
                for(CFOSolution s : solutionsQueue)
                {
                    double [] u = s.getCoordinate();
                    drawSolution(g, xToPix(u[0]), yToPix(u[1]));
                }                
            }
        }
        protected void drawSolution(Graphics2D g, int x, int y)
        {                                    
            // draw the thing
            g.setColor(Color.BLACK);
            g.fillRect(x-SOLUTION_RADIUS-1, y-SOLUTION_RADIUS-1, SOLUTION_WIDTH+2, SOLUTION_WIDTH+2);
            g.setColor(Color.WHITE);
            g.drawRect(x-SOLUTION_RADIUS, y-SOLUTION_RADIUS, SOLUTION_WIDTH, SOLUTION_WIDTH);
        }
    }
}