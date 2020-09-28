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
package com.oat.explorer.domains.bcr.gui.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.bcr.BCRProblem;
import com.oat.domains.bcr.BCRSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;
import com.oat.utils.AlgorithmUtils;

/**
 * Type: BCRPlot<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description: Simple plot to draw binary character problems and solution
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BCRPlot extends GenericProblemPlot
    implements AlgorithmEpochCompleteListener, ClearEventListener
{
    public static enum DRAW_MODE {PROBLEM, SOLUTION} 
    
    protected final DRAW_MODE drawMode;
    protected BCRSolution solution;
    protected BCRProblem problem;
    
    public BCRPlot(DRAW_MODE aDrawMode)
    {
        drawMode = aDrawMode;
        
        switch(drawMode)
        {
        case PROBLEM:
            setName("Problem");
            break;
        case SOLUTION:
            setName("Run Best");
            break;
        default:
            throw new AlgorithmRunException("Invalid draw mode " + drawMode);
        }
        
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void problemChangedEvent(Problem p)
    {
        synchronized(this)
        {
            problem = (BCRProblem) p;
        }
        repaint();
    }

    public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
    {
        if(drawMode != DRAW_MODE.SOLUTION)
        {
            return;
        }
        synchronized(this)
        {
            BCRSolution b = (BCRSolution) AlgorithmUtils.getBest(currentPop, p);
            if(solution == null || p.isBetter(b, solution))
            {
                solution = b;
            }
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        switch(drawMode)
        {
        case PROBLEM:
            drawProblem(g);
            break;
        case SOLUTION:
            drawSolution(g);
            break;
        default:
            throw new AlgorithmRunException("Invalid draw mode " + drawMode);
        }
    }
    
    protected void drawProblem(Graphics g)
    {
        synchronized(this)
        {
            if(problem == null)
            {
                plotUnavailable(g);
            }
            else
            {
                int w = getWidth() / (problem.getSinglePatternWidth() * problem.getTotalPatterns());
                int h = getHeight() / problem.getSinglePatternHeight();
                int size = Math.min(w,h);
                boolean [][] patterns = problem.getTrainingPatterns();
                // draw all patterns
                for (int i =0, xOffset = 0; i < patterns.length; i++, xOffset+=(w*problem.getSinglePatternWidth()))
                {
                    drawPattern(g, patterns[i], xOffset, size, size, problem.getSinglePatternWidth(), problem.getSinglePatternHeight());
                }
            }
        }
    }
    
    protected void drawPattern(Graphics g, boolean [] pattern, int xOffset, int w, int h, int pWidth, int pHeight)
    {
        int patternOffset = 0;
        // work down the rows
        for (int y = 0; y < pHeight; y++)
        {
            // work across the columns
            for (int x = 0; x < pWidth; x++)
            {
                Color c = (pattern[patternOffset++]) ? Color.LIGHT_GRAY : Color.BLACK;
                g.setColor(c);
                g.fillRect(xOffset+(x*w), y*h, w, h);                
            }
        }
        // draw a border
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(xOffset, 0, pWidth*w, pHeight*h);
    }
    
    protected void drawSolution(Graphics g)
    {
        synchronized(this)
        {
            if(solution == null)
            {
                plotUnavailable(g);
            }
            else
            {
                boolean [][] patterns = solution.getPatterns();
                
                int w = getWidth() / (problem.getSinglePatternWidth() * patterns.length);
                int h = getHeight() / problem.getSinglePatternHeight();
                int size = Math.min(w,h);
                
                // draw all patterns
                for (int i =0, xOffset = 0; i < patterns.length; i++, xOffset+=(w*problem.getSinglePatternWidth()))
                {
                    drawPattern(g, patterns[i], xOffset, size, size, problem.getSinglePatternWidth(), problem.getSinglePatternHeight());
                }
            }
        }
    }

    public void clear()
    {
        synchronized(this)
        {            
            solution = null;
        }
        repaint();
    }
}
