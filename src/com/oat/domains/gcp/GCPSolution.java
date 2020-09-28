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
package com.oat.domains.gcp;

import java.util.Arrays;

import com.oat.Solution;

/**
 * Type: GCPSolution<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: Solution to the graph coloring problem, defined by a list of colours for each nodes
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GCPSolution extends Solution
{
    protected final int [] nodeColors;
    protected boolean isFeasible = false;
    protected int penalty = Integer.MIN_VALUE;
    protected int numColors = Integer.MIN_VALUE;
    
    
    public GCPSolution(int [] aNodeColours)
    {
        nodeColors = aNodeColours;
    }
    
    public GCPSolution(int numNodes)
    {
        nodeColors = new int[numNodes];
    }
    
    public GCPSolution(GCProblem p)
    {
        nodeColors = new int[p.getTotalNodes()];
    }
    
    @Override
    public boolean equals(Object o)
    {
        GCPSolution s = (GCPSolution) o;
        return Arrays.equals(nodeColors, s.nodeColors);
    }
    
    @Override
    public String toString()
    {
        return super.toString() +
        "Feasible="+isFeasible+", " +
        "Penalty="+penalty+", " +
        "NumColors="+numColors+", " +
        "Assignment: " + Arrays.toString(nodeColors);
    }

    public int[] getNodeColors()
    {
        return nodeColors;
    }

    public boolean isFeasible()
    {
        return isFeasible;
    }

    public int getPenalty()
    {
        return penalty;
    }

    public int getNumColors()
    {
        return numColors;
    }
}
