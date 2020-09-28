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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.LinkedList;

import javax.swing.BorderFactory;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.explorer.gui.ClearEventListener;
import com.oat.explorer.gui.plot.GenericProblemPlot;
import com.oat.utils.AlgorithmUtils;


/**
 * Type: TourPanel<br/>
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
public class TourPanel extends GenericProblemPlot 
    implements AlgorithmEpochCompleteListener, ClearEventListener
{
    protected TourPlot tourPlot;
    protected boolean showBestTour;
    
    public TourPanel()
    {
        setName("Run Best");
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        tourPlot = new TourPlot();
        setLayout(new BorderLayout());
        add(tourPlot, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Optimal Tour"));
    }
    
    public void problemChangedEvent(Problem p)
    {
        clear();
        
        tourPlot.setProblem((TSPProblem)p);
        if(showBestTour)
        {
            tourPlot.setTour(((TSPProblem)p).getSolutionCityList());
        }
        tourPlot.repaint();
    }
    
    public void setPermutation(TSPSolution s)
    {
        best = s;
        tourPlot.setTour(s.getPermutation());
        tourPlot.repaint();
    }
    
    protected TSPSolution best = null;

    public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
    {        
        TSPSolution b = (TSPSolution) AlgorithmUtils.getBest(currentPop, p);
        if(best == null || p.isBetter(b, best))
        {
            setPermutation(b);
        }
    }
    
    public void clear()
    {
        tourPlot.setTour(null);
        tourPlot.repaint();
        best = null;
    }

    public boolean isShowBestTour()
    {
        return showBestTour;
    }

    public void setShowBestTour(boolean showBestTour)
    {
        this.showBestTour = showBestTour;
    }    
}
