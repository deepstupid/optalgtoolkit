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
import com.oat.domains.cfo.CFOProblem;
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
public class CFOPreview extends GenericProblemPlot
{
    protected GenericProblemPlot plot; 
    protected CFOProblem problem;
    
    protected ThreeDimensionalFunctionPlot plot3D;
    protected OneDimensionalFunctionPlot plot1D;
    
    public CFOPreview()
    {
        setLayout(new BorderLayout());
        plot3D = new ThreeDimensionalFunctionPlot(16, 6.0);
        plot1D = new OneDimensionalFunctionPlot();
    }

    public synchronized void problemChangedEvent(Problem aProblem)
    {
        CFOProblem p = (CFOProblem) aProblem;        
        
        if(plot != null)
        {
            this.remove(plot);            
        }
        problem = null;
        plot = null; 
        
        if(p.isDimensionalitySupported(2))
        {
            plot = plot3D;
            add(plot);
            problem = p.getCreateNewInstance(2);
            plot.problemChangedEvent(problem);            
        }
        else if(p.isDimensionalitySupported(1))
        {
            plot = plot1D;
            add(plot);
            problem = p.getCreateNewInstance(1);
            plot.problemChangedEvent(problem);
        }        
                
        this.validate();
        this.repaint();
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
