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
import java.awt.Graphics;

import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display2d.GridPointData;
import org.opensourcephysics.display2d.SurfacePlot;
import org.opensourcephysics.display2d.SurfacePlotMouseController;

import com.oat.Problem;
import com.oat.domains.cfo.CFOProblem;
import com.oat.explorer.gui.plot.GenericProblemPlot;


/**
 * Type: ThreeDimensionalFunctionPlot<br/>
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
public class ThreeDimensionalFunctionPlot extends GenericProblemPlot
{
    public final static int RESOLUTION = 32;
    
    
    
    protected DrawingPanel drawingPanel;
    protected GridPointData gridPointData;
    protected SurfacePlot plot;  	
    
    protected CFOProblem problem;
    
	
    public ThreeDimensionalFunctionPlot()
    {
        prepareGui(RESOLUTION);
    }	
    
    public ThreeDimensionalFunctionPlot(int aResolution, double aScalingFactor)
    {
        prepareGui(aResolution);
        plot.set2DScaling(aScalingFactor);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        synchronized(this)
        {
            if(problem == null)
            {
                plotUnavailable(g);
                return;
            }
            super.paintComponent(g);
        }
    }
    @Override
    public void paint(Graphics g)
    {
        synchronized(this)
        {
            if(problem == null)
            {
                plotUnavailable(g);
                return;
            }
            super.paint(g);
        }
    }
    protected void prepareGui(int aRes)
    {
        gridPointData = new GridPointData(aRes, aRes, 1);
        plot = new SurfacePlot(gridPointData);  
        plot.setShowGridLines(true);
        
        // prepare the panel that holds the plot
        drawingPanel = new DrawingPanel();
        drawingPanel.setLayout(new BorderLayout());
        drawingPanel.addDrawable(plot);  
        SurfacePlotMouseController mouseController = new SurfacePlotMouseController(drawingPanel, plot);
        drawingPanel.addMouseListener(mouseController);
        drawingPanel.addMouseMotionListener(mouseController);
        
        
        setName("3D Plot");        
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);
    }
    
    	
	protected void populationGridWithFunction(CFOProblem p)
	{
		// set the scale
	    double [][] minMax = p.getMinmax();
        gridPointData.setScale(minMax[0][0], minMax[0][1], minMax[1][0], minMax[1][1]);
		
		// set the z axis data points
		double [][][] data = gridPointData.getData();		
		for (int x = 0; x < data.length; x++)
		{
			for (int y = 0; y < data[x].length; y++)
			{
				// calculate z
				data[x][y][2] = p.directFunctionEvaluation(data[x][y]);
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
                populationGridWithFunction(problem);
                plot.setGridData(gridPointData);
                plot.update();
            }
            else
            {
                problem = null;
            }
        }       

        repaint();        
    }
}