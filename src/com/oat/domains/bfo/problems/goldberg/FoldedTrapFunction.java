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
package com.oat.domains.bfo.problems.goldberg;

import com.oat.Solution;
import com.oat.domains.bfo.BFOProblem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.BitStringUtils;

/**
 * Type: FoldedTrapFunction<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: As specified in: Massive Multimodality deception and genetic algorithms (1992)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class FoldedTrapFunction extends BFOProblem
{
    protected double a = 0.95;
    protected double b = 1.00;
    protected double z = 2.00;
    
    public FoldedTrapFunction()
    {
        length = 30;
    }

    @Override
    protected double problemSpecificCost(Solution n)
    {
        BFOSolution s = (BFOSolution) n;
        double u = BitStringUtils.unitation(s.getBitString());
        if(u<z)
        {
            return (b/z) * (z-u);
        }
        
        return (a / (length-z)) * (u-z);
    }

    @Override
    public String getName()
    {
        return "Folded Trap Function";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }
 
    
    public void setBinaryStringLength(int l)
    {
        length = l;
    }
    @Override
    public boolean isUserConfigurable()
    {
    	return true;
    }
}
