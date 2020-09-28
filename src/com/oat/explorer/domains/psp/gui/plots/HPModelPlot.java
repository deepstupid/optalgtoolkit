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
package com.oat.explorer.domains.psp.gui.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.domains.psp.PSPProblem;
import com.oat.domains.psp.PSPSolution;
import com.oat.explorer.gui.plot.GenericProblemPlot;


/**
 * Type: ProteinPlot<br/>
 * Date: 21/11/2006<br/>
 * <br/>
 * Description: Plot a 2DHP conformation, whether it is valid or not 
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre> 
 */
public class HPModelPlot extends GenericProblemPlot
{    
    protected PSPProblem problem;  
    protected PSPSolution solution;
    protected byte [][] lattice;    
    
    public void setProblem(PSPProblem p)
    {
        problem = p;
    }
    
    public void setSolution(PSPSolution s)
    {
        synchronized(this)
        {
            if(s == null)
            {
                lattice = null;
                solution = null;
            }
            else
            {
                // only do work if there is a change
                if(solution==null || !s.equals(solution))
                {
                    lattice = s.retrieveLattice(problem.getDataset());
                    solution = s;
                }
            }
        }
        this.repaint();
    }
    
    
    @Override
    protected void paintComponent(Graphics graph)
    {
        // house keeping
        Graphics2D g = (Graphics2D) graph;
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        
        synchronized(this)
        {
            if(lattice == null)
            {
                plotUnavailable(g);
            }
            else
            {
                int divisor = Math.min(w,h);
                // check for less pixels than squares
                if(divisor >= lattice.length)
                {
                    // draw the map
                    drawMap(g, divisor);
                }
            }
        }
    }
    
    
    protected void drawMap(Graphics2D g, int divisor)
    {
        // do not draw all the map, just the middle 
        int offset = (lattice.length/2)/2;
        // only drawing half the map in each dimension (1/4)
        int squareSize = divisor / (lattice.length/2); 
        int x = 0, y = 0;
        
        for (int i = offset; i < lattice.length-offset; i++)
        {
            for (int j = offset; j < lattice[i].length-offset; j++)
            {
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, squareSize-1, squareSize-1);
                g.setColor(Color.WHITE);
                g.fillRect(x, y, squareSize-1, squareSize-1);
                
                switch(lattice[j][i])
                {
                    case PSPProblem.EMPTY:
                    {
                        break;
                    }
                    case PSPProblem.H:
                    {
                        g.setColor(solution.isFeasibleConformation() ? Color.BLACK : Color.RED);
                        g.fillOval(x, y, squareSize-1, squareSize-1);
                        break;
                    }
                    case PSPProblem.P:
                    {
                        g.setColor(Color.GRAY);
                        g.drawOval(x, y, squareSize-1, squareSize-1);
                        break;
                    }
                    default:
                    {
                        throw new AlgorithmRunException("Invalid value: " + lattice[j][i]);
                    }
                }                    
                
                x += squareSize;
            }
            y += squareSize;
            x = 0;
        }
    }
    
    public void problemChangedEvent(Problem p)
    {
        throw new AlgorithmRunException("Unsuppored!");
    }

    public PSPProblem getProblem()
    {
        return problem;
    }

    public PSPSolution getSolution()
    {
        return solution;
    }
    
    
}
