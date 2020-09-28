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
import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;

/**
 * Type: GCPUtils<br/>
 * Date: 11/12/2006<br/>
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
public class GCPUtils
{
    
    /**
     * Generate a naive valid solution, where each node has a unique color assigned.
     * @param p
     * @return
     */
    public final static int [] generateNaiveSolution(GCProblem p)
    {
        int [] colors = new int[p.getTotalNodes()];
        for (int i = 0; i < colors.length; i++)
        {
            colors[i] = i + 1;
        }
        return colors;
    }
    
    /**
     * Generates a random solution that may or may not be feasible
     * First a number of colors is selected in the range [3, numnodes]
     * Then a random assignment of those colours occurs
     * 
     * @param p
     * @return
     */
    public final static int [] generateRandomSolution(GCProblem p, Random r)
    {
        // select a number of colours
        int numColours = r.nextInt(p.getTotalNodes()-3) + 3;
        // assign each node a random color
        int [] colors = new int[p.getTotalNodes()];
        for (int i = 0; i < colors.length; i++)
        {
            // ensure all colors aren in the range [1,numColours]
            colors[i] = r.nextInt(numColours) + 1;
        }
        return colors;
    }
    
    
    /**
     * Given an ordering in which to visit nodes, assign colors based on what colors have not been assigned yet
     * to neighboring nodes
     * 
     * Assumes node visit order is not zero offset as is the case in the problem definition
     * 
     * @param p
     * @param nodeVisitOrdering
     * @param colorAssignments
     */
    public static void nodeVisitOrderingToColorAssignments(GCProblem p, int [] nodeVisitOrdering, int [] colorAssignments)
    {
        if(nodeVisitOrdering.length != colorAssignments.length || nodeVisitOrdering.length != p.getTotalNodes())
        {
            throw new RuntimeException("Invalid length "+nodeVisitOrdering.length+" for node visit ordering and color assignments "+colorAssignments.length+", total nodes is " + p.getTotalNodes());
        }
        
        // unassing all colors to neighbours
        Arrays.fill(colorAssignments, -1);
        
        // process each node
        for (int i = 0; i < nodeVisitOrdering.length; i++)
        {
            int currentNodeIndex = nodeVisitOrdering[i];
            // get neighbours for the current node
            LinkedList<Integer> neighbours = p.getNeighbours(currentNodeIndex);
            // check for noneighbours
            if(neighbours.isEmpty())
            {
                colorAssignments[currentNodeIndex-1] = 1;
                continue; // and we are done
            }            
            // process all colors starting at 1 until we find a color that is not taken by the neighbours
            boolean assigned = false;            
            for(int currentColor=1; !assigned && currentColor<=colorAssignments.length; currentColor++)
            {
                // check if any of the neighbours have this color 'j'
                boolean colorUsed = false;
                for(Integer n : neighbours)
                {
                    if(colorAssignments[n-1] == currentColor)
                    {
                        colorUsed = true;
                        break; // skip this color
                    }
                }
                // we have checked all neighbours, check if the color has not been used
                if(!colorUsed)
                {
                    // use the color
                    colorAssignments[currentNodeIndex-1] = currentColor;
                    assigned = true; // stop searching
                }
            }
            // ensure we assigned a color to the current node (safe)
            if(!assigned)
            {
                throw new AlgorithmRunException("Failed to assign a color to the current node "+currentNodeIndex+" (should not occur).");
            }
        }
    }
}
