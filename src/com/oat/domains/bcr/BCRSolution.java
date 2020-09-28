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
package com.oat.domains.bcr;

import com.oat.Problem;
import com.oat.Solution;

/**
 * Type: CharRecSolution<br/>
 * Date: 18/12/2006<br/>
 * <br/>
 * Description: Generic binary character recognition solution, provides an interface into the model
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Simplified as an interface into the model
 * </pre>
 */
public abstract class BCRSolution extends Solution
{   
    /**
     * Generate a response for a hidden pattern (just get a match scoring)
     * @param match
     * @param problem
     * @return
     */
    public abstract boolean [] response(MatchFunction match, Problem problem);        
    
    /**
     * Access to the patterns represented in this solution
     * @return
     */
    public abstract boolean [][] getPatterns();
}
