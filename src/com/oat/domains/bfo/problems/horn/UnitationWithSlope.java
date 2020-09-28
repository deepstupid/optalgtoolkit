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
package com.oat.domains.bfo.problems.horn;

import com.oat.Solution;
import com.oat.domains.bfo.BFOProblem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.BitStringUtils;

/**
 * Type: OneMax<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: Seek Odd/Even Unitation with slope
 * As described in:  Genetic Algorithm Difficulty and the Modality of Fitness Landscapes (1994)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class UnitationWithSlope extends BFOProblem
{    
    public UnitationWithSlope()
    {
        length = 30; 
    }

    @Override
    protected double problemSpecificCost(Solution s)
    {
        boolean oddLength = ((length%2)!=0);        
        double u = BitStringUtils.unitation(((BFOSolution)s).getBitString());
        
        double fmm = 0.0;        
        if(oddLength && (u%2)!=0)
        {
            fmm = 1;
        }
        else if(!oddLength && (u%2)==0)
        {
            fmm = 1;
        }
        
        return u + 2.0 * fmm;
    }

    @Override
    public String getName()
    {
        return "Odd or Even Unitation with Slope";
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
