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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.cfo.CFOProblem;
import com.oat.domains.cfo.CFOSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;
import com.oat.utils.ArrayUtils;


/**
 * Type: OneDimensionalFunctionPlot<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: One Dimensional function plot
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 08/07/2007   JBrownlee   Modified to accept whatever function as long as it is 1D
 * </pre>
 */
public class OneDimensionalFunctionPlot extends GenericProblemPlot
    implements ClearEventListener, SolutionEvaluationListener
{
    /**
     * Really light gray
     */
    public final static Color COLOR_GRID_LINES = new Color(220, 220, 220);
    /**
     * Dark blue
     */
    public final static Color COLOR_PLOT_LINE = new Color(0, 0, 255);
    
    public final static int INSETS = 10;	
    public final static int POINT_SIZE = 4;
    public final static int RESOLUTION = 500;
    
    public final static int QUEUE_SIZE = 1000;
    
    protected CFOProblem problem;
    protected LinkedList<CFOSolution> solutionsQueue;
	
    protected double xmin, xmax, ymin, ymax;
    protected double [][] fitnessFunction;	
    protected int [] xFitnessPoints;
    protected int [] yFitnessPoints;
	
    protected double [][] populationPoints;	
    protected int [] xPopulationPoints;
    protected int [] yPopulationPoints;
    
    
    /**
     * Constructor
     */
	public OneDimensionalFunctionPlot()
	{
	    super.setDoubleBuffered(true);
		setBackground(Color.white);        
		setOpaque(true);
        setName("1D Plot");
        
        fitnessFunction = new double[2][RESOLUTION];
        xFitnessPoints = new int[RESOLUTION];
        yFitnessPoints = new int[RESOLUTION];
        solutionsQueue = new LinkedList<CFOSolution>();
	}	
	
    @Override
	protected void paintComponent(Graphics g)
	{
        synchronized (this)
        {
            // ensure we have a problem
            if(problem == null)
            {
                plotUnavailable(g);
                return;
            }
        }
        
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;		
		
		// border
        graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, getWidth()-1, getHeight()-1);		
		int xEdge = getWidth()-1-(INSETS*2);
		int yEdge = getHeight()-1-(INSETS*2);
        graphics.setColor(COLOR_GRID_LINES);
		// background				
		graphics.drawRect(INSETS, INSETS, xEdge, yEdge);
		// grid lines
        drawGridLines(graphics, xEdge, yEdge);        
        // draw the problem polygon
        drawProblemPolygon(graphics);
        // draw solutions
        drawSolutions(graphics);
	}
    
    protected void drawSolutions(Graphics2D g)
    {
        synchronized(solutionsQueue)
        {
            if(solutionsQueue == null)
            {
                return;
            }
            
            g.setColor(Color.RED);                
            for (CFOSolution f : solutionsQueue)
            {
                if(f.isEvaluated())
                {
                    int x = xToPix(f.getCoordinate()[0]);
                    int y = yToPix(f.getScore());
                    g.drawOval(x-2, y-2, 4, 4);
                }
            }    
        }
    }
    
    protected int xToPix(double x)
    {
        // convert to ratio of domain
        double domainRatio = (x-xmin) / (xmax-xmin);
        // use ratio as ratio of drawable area
        double drawRatio = domainRatio * (getWidth()-1-(INSETS*2));
        // convert to usable pixel        
        return INSETS + (int) Math.round(drawRatio);
    }
    
    protected int yToPix(double y)
    {
        // convert to ratio of domain
        double domainRatio = (y-ymin) / (ymax-ymin);            
        // use ratio as ratio of drawable area
        double drawRatio = (1-domainRatio) * (getHeight()-1-(INSETS*2));
        // convert to usable pixel
        return INSETS + (int) Math.round(drawRatio);
    }    
    
    
    protected void drawProblemPolygon(Graphics2D g)
    {
        // convert fitness points to pixels
        for (int i = 0; i < RESOLUTION; i++)
        {
            // calculate the x
            xFitnessPoints[i] = xToPix(fitnessFunction[0][i]);
            // calculate the y
            yFitnessPoints[i] = yToPix(fitnessFunction[1][i]);
        }       
        // draw fitness function
        g.setColor(COLOR_PLOT_LINE); // a nice blue
        g.drawPolyline(xFitnessPoints, yFitnessPoints, xFitnessPoints.length);
    }
    
    protected void drawGridLines(Graphics2D g, double xEdge, double yEdge)
    {
        int xIncrement = (int) Math.round(xEdge / 10.0);
        for (int x = INSETS+xIncrement; x < xEdge; x+=xIncrement)
        {
            g.drawLine(x, INSETS, x, getHeight()-1-INSETS);
        }       
        int yIncrement = (int) Math.round(yEdge / 10.0);
        for (int y = INSETS+yIncrement; y < yEdge; y+=yIncrement)
        {
            g.drawLine(INSETS, y, getWidth()-1-INSETS, y);
        }
    }
    
    public void problemChangedEvent(Problem p)
    {
        CFOProblem f = (CFOProblem) p;
        
        synchronized(this)
        {            
            if(f.isDimensionalitySupported(1) && f.getDimensions() == 1)
            {
                problem = f;
                prepareProblemPolygon();
            }
            else
            {
                problem = null;
            }
        }
        clear();
        repaint();
    }
    
    protected void prepareProblemPolygon()
    {
        double [][] minmax = problem.getMinmax();
        double slice = (minmax[0][1] - minmax[0][0]) / RESOLUTION;
        // coords
        for (int i = 0; i < RESOLUTION; i++)
        {
            fitnessFunction[0][i] = minmax[0][0] + (i*slice);
            fitnessFunction[1][i] = problem.directFunctionEvaluation(new double[]{fitnessFunction[0][i]});            
        }   
        // bounds
        xmin = ArrayUtils.min(fitnessFunction[0]);
        xmax = ArrayUtils.max(fitnessFunction[0]);
        ymin = ArrayUtils.min(fitnessFunction[1]);
        ymax = ArrayUtils.max(fitnessFunction[1]);
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
    
    
    @Override
    public void solutionEvaluatedEvent(Solution evaluatedSolution)
    {
    	addSolution(evaluatedSolution);
        repaint();
        
    }

    public void clear()
    {
        synchronized(solutionsQueue)
        {
        	solutionsQueue.clear();
        }
        repaint();
    }
}
