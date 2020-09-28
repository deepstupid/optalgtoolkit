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
package com.oat.explorer.domains.cfo.gui.plots;

import java.awt.BorderLayout;
import java.awt.Graphics;

import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.cfo.CFOProblem;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;

/**
 * Type: FuncOptPreview<br/>
 * Date: 08/07/2007<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 24/07/2007   JBrownlee   Modified to use the same two plot instances over and over
 * </pre>
 *
 */
public class CFOPlot extends GenericProblemPlot
    implements SolutionEvaluationListener, ClearEventListener
{
    protected GenericProblemPlot plot; 
    protected CFOProblem problem;
    
    protected TwoDimensionalFunctionPlot plot2D;
    protected OneDimensionalFunctionPlot plot1D;
    
    
    public CFOPlot()
    {
        setLayout(new BorderLayout());
        setName("Plot");
        plot2D = new TwoDimensionalFunctionPlot();
        plot1D = new OneDimensionalFunctionPlot();
    }

    public synchronized void problemChangedEvent(Problem aProblem)
    {
        CFOProblem p = (CFOProblem) aProblem;
        
        if(plot != null)
        {
            this.remove(plot);    
            plot2D.clear();
            plot1D.clear();
        }
        problem = null;
        plot = null; 
        
        if(p.isDimensionalitySupported(2) && p.getDimensions() == 2)
        {
            plot = plot2D;
            add(plot);
            problem = p;
            plot.problemChangedEvent(problem);            
        }
        else if(p.isDimensionalitySupported(1) && p.getDimensions() == 1)
        {
            plot = plot1D;
            add(plot);
            problem = p;
            plot.problemChangedEvent(problem);
        }    
        
        this.validate();
        this.repaint();
    }

    @Override
    public void solutionEvaluatedEvent(Solution evaluatedSolution)
    {
        if(plot != null)
        {
            ((SolutionEvaluationListener)plot).solutionEvaluatedEvent(evaluatedSolution);
        }        
    }

    @Override
    public synchronized void clear()
    {
        if(plot != null)
        {
            ((ClearEventListener)plot).clear();
        }      
    }       
    
    @Override
    public synchronized void paintComponent(Graphics g)
    {
        if(plot == null)
        {
            plotUnavailable(g);
            return;
        }
        
        super.paintComponent(g);
    }
}
