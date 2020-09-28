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
package com.oat.explorer.domains.bfo.gui.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.bfo.BFOProblem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;
import com.oat.utils.AlgorithmUtils;

/**
 * Type: BinaryStringPlot<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Added support for SolutionEvaluationListener
 * </pre> 
 */
public class StringPlot extends GenericProblemPlot
    implements ClearEventListener, AlgorithmEpochCompleteListener, SolutionEvaluationListener
{
    protected BFOProblem problem;
    protected BFOSolution bestSolution;
    
    public StringPlot()
    {
        setName("Run Best");
    }
    
    
    public void problemChangedEvent(Problem p)
    {
        synchronized(this)
        {
            problem = (BFOProblem) p;
            clear();
        }
        repaint();
    }

    public void clear()
    {
        synchronized(this)
        {
            bestSolution = null;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        synchronized(this)
        {
            if(problem == null || bestSolution == null)
            {
                plotUnavailable(g);
                return;
            }       
            g.setColor(Color.WHITE);
            g.fillRect(0,0,getWidth(), getHeight());
            drawBitString(g, bestSolution.getBitString());
        }
    }
    
    public final static int YOFFFSET = 20;
    
    protected void drawBitString(Graphics g, boolean [] b)
    {
        int squareSize = getWidth() / b.length;
        for (int i = 0; i < b.length; i++)
        {
            if(!b[i])
            {
                g.setColor(Color.BLACK);
                g.fillRect(i*squareSize, YOFFFSET, squareSize, squareSize);
            }
            
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(i*squareSize, YOFFFSET, squareSize, squareSize);                     
        }
    }

    public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
    {
        synchronized(this)
        {
            BFOSolution b = (BFOSolution) AlgorithmUtils.getBest(currentPop, p);
            if(bestSolution == null || p.isBetter(b, bestSolution))
            {
                bestSolution = b;
            }
        }
        repaint();
    }


    @Override
    public void solutionEvaluatedEvent(Solution evaluatedSolution)
    {
        synchronized(this)
        {
            BFOSolution b = (BFOSolution) evaluatedSolution;
            if(bestSolution == null || problem.isBetter(b, bestSolution))
            {
                bestSolution = b;
            }
        }
        repaint();        
    }    
    
}
