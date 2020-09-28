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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.LinkedList;

import javax.swing.BorderFactory;

import com.oat.AlgorithmEpochCompleteListener;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.psp.PSPProblem;
import com.oat.domains.psp.PSPSolution;
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
public class HPModelPanel extends GenericProblemPlot 
    implements AlgorithmEpochCompleteListener, ClearEventListener
{
    protected HPModelPlot proteinPlot;
    
    public HPModelPanel()
    {
        setName("Run Best");
        prepareGUI();
    }
    
    protected void prepareGUI()
    {
        proteinPlot = new HPModelPlot();
        setLayout(new BorderLayout());
        add(proteinPlot, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Optimal Solution"));
    }    
    
    public void problemChangedEvent(Problem p)
    {
        clear();
        proteinPlot.setProblem((PSPProblem)p);
    }
    public void setPermutation(PSPSolution s)
    {
        proteinPlot.setSolution(s);
    }
    public <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop)
    {        
        PSPSolution b = (PSPSolution) AlgorithmUtils.getBest(currentPop, p);
        PSPSolution c = proteinPlot.getSolution();
        if(c == null || p.isBetter(b,c))
        {
            setPermutation(b);
        }
    }
    
    public void clear()
    {
        proteinPlot.setSolution(null);
    }
}
