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
package com.oat.explorer.domains.hbs.gui.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationListener;
import com.oat.domains.cfo.CFOSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;


/**
 * Type: BlindSamplePlot<br/>
 * Date: 29/03/2006<br/>
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
public class BlindSamplePlot extends GenericProblemPlot
    implements SolutionEvaluationListener, ClearEventListener
{
    protected final LinkedList<double []> coords;
    
    public BlindSamplePlot()
    {
        coords = new LinkedList<double []>();
        prepareGUI();
    }
    
    public void prepareGUI()
    {        
        setName("Sample Plot");
        DrawingPanel p = new DrawingPanel();
        setLayout(new BorderLayout());
        add(p);
    }
    
    public void solutionEvaluatedEvent(Solution s)
    {
        synchronized(coords)
        {
            double [] v = ((CFOSolution)s).getCoordinate();
            coords.add(new double[]{v[0], v[1], s.getScore()});
        }
        repaint();
    }
    
    public void problemChangedEvent(Problem p)
    {
        clear();
        repaint();
    }
    
    public void clear()
    {
        synchronized(coords)
        {
            coords.clear();
        }
        repaint();
    }
    
    protected class DrawingPanel extends JPanel
    {
        public final static int SIZE = 2;
        
        @Override
        protected void paintComponent(Graphics g)
        {
            int w = getWidth();
            int h = getHeight();
            
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            
            synchronized(coords)
            {
                double max = Double.NEGATIVE_INFINITY;
                double min = Double.POSITIVE_INFINITY;
                
                for(double [] c : coords)
                {
                    if(c[2] > max)
                    {
                        max = c[2];
                    }
                    if(c[2] < min)
                    {
                        min = c[2];
                    }
                }
                
                double range = max-min;                
                for(double [] c : coords)
                {
                    float v = (float)((c[2]-min)/range);
                    Color colour = new Color(1.0f-v, 0.0f, 0.0f);
                    int x = (int) (Math.round(c[0] * w) - (SIZE/2));
                    int y = (int) (Math.round(c[1] * h) - (SIZE/2));
                    g.setColor(colour);
                    g.fillOval(x,y,SIZE,SIZE);
                }
            }
        }
    }
}
