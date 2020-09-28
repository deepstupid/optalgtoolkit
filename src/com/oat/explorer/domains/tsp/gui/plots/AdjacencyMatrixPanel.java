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
package com.oat.explorer.domains.tsp.gui.plots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import com.oat.AlgorithmRunException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;


/**
 * Type: AdjacencyMatrixPanel<br/>
 * Date: 27/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public class AdjacencyMatrixPanel extends GenericProblemPlot 
    implements SolutionEvaluationListener, ClearEventListener
{
    public final static int MIN_SQUARE_SIZE = 2;
    
    protected int [][] adjacencyMatrix;
    protected int globalMax;
    protected int squareSize;
    protected TSPProblem problem;
    
    public AdjacencyMatrixPanel()
    {
        setName("Matrix");
        prepareGUI();
    }
    
    protected void prepareGUI()
    {}
    
    public void problemChangedEvent(Problem p)
    {
        clear();
        
        synchronized(this)
        {
            problem = (TSPProblem) p;
            adjacencyMatrix = new int[problem.getTotalCities()][problem.getTotalCities()];
        }
        repaint();
    }
    
    public void clear()
    {
        synchronized(this)
        {
            if(adjacencyMatrix!=null)
            {
                for (int i = 0; i < adjacencyMatrix.length; i++)
                {
                    Arrays.fill(adjacencyMatrix[i], 0);
                }
            }
        }
        repaint();
    }
    
    
    public void solutionEvaluatedEvent(Solution as)
    {  
        synchronized(this)
        {
            if(adjacencyMatrix == null)
            {
                throw new AlgorithmRunException("Unable to update matrix, unprepared!");
            }
            
            TSPSolution s = (TSPSolution) as;
            int [] v = s.getPermutation();
            
            for (int i = 0; i < v.length; i++)
            {
                int x = v[i];
                int y = -1;
                if(i == v.length-1)
                {
                    y = v[0];
                }
                else
                {
                    y = v[i+1];
                }
                
                if(x > y)
                {
                    // swap
                    int a = x;
                    x = y;
                    y = a;
                }
                // update and kep track of global max frequency            
                if(++adjacencyMatrix[x][y] > globalMax)
                {
                    globalMax = adjacencyMatrix[x][y];
                }
            }
        }
        
        repaint();
    }
    
    
    public boolean canDraw()
    {
        if(problem == null || adjacencyMatrix == null)
        {
            return false;
        }
        
        // prepare square size
        squareSize = (int) ((double)Math.min(getHeight(),getWidth()) / (double)problem.getTotalCities());
        
        if(squareSize >= MIN_SQUARE_SIZE)
        {
            return true;
        }
        
        return false;
    }
    
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getWidth(),getHeight());
        
        synchronized(this)
        {
            if(canDraw())
            {
                double max = globalMax;
                // draw the squares
                for (int i = 0, y = 0; i < adjacencyMatrix.length; i++, y+=squareSize)
                {
                    for (int j = 0, x=0; j < adjacencyMatrix[i].length; j++, x+=squareSize)
                    {
                        if(x>y)
                        {
                            double v = adjacencyMatrix[i][j];
                            float f = 1.0f;
                            if(max > 0 && v<=max)
                            {
                                f = (float) (1.0-(v / max));
                            }
                            Color c = new Color(1.0f,f,f);
                            g2d.setColor(c);
                            g2d.fillRect(x, y, squareSize, squareSize);
                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.drawRect(x, y, squareSize, squareSize);
                        }
                    }                
                }
            }
            else
            {
                plotUnavailable(g);
            }
        }
    }    
}
