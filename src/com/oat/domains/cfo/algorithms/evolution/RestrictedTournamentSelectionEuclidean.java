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
package com.oat.domains.cfo.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Problem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.algorithms.evolution.RestrictedTournamentSelection;
import com.oat.domains.cfo.CFOUtils;

/**
 * Type: RestrictedTournamentSelection<br/>
 * Date: 04/12/2006<br/>
 * <br/>
 * Description: Implementation of the Restructed Tournament Selection (RTS) algorithm
 * As specified in Finding Multimodal Solutions Using Restricted Tournament Selection (1995)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 09/01/2007   JBrownlee   Modified to use a generic replacement function
 * 20/08/2007	JBrownlee	Updated to extend the binary version with Euclidean distance instead of Hamming
 * </pre>
 */
public class RestrictedTournamentSelectionEuclidean extends RestrictedTournamentSelection
{    

    @Override
    public String getDetails()
    {
        return "As specified in G. R. Harik. Finding Multimodal Solutions Using Restricted Tournament SelectionLarry Eshelman. Proceedings of the Sixth International Conference on Genetic Algorithms (ICGA)San Francisco, CA: Morgan Kaufmann; 1995: 24-31. ISBN: 1558603700. " +
                "Using Hamming distance.";
    }


    @Override
    public String getName()
    {
        return "Restricted Tournament Selection Euclidean (RTS)";
    }
    
    /**
     * Perform replacement (Hamming distance based)
     * 
     * @param <T>
     * @param pop
     * @param children
     * @param p
     * @param r
     * @param sampleSize
     */
    @Override
    public <T extends BFOSolution> void doReplacement(
            LinkedList<T> pop, 
            LinkedList<T> children, 
            Problem p,
            Random r,
            int sampleSize    		
    		)
    {
    	// use Euclidean distance instead of Hamming distance
    	CFOUtils.euclideanBasedReplacement(pop, children, p, r, sampleSize);
    }
}
