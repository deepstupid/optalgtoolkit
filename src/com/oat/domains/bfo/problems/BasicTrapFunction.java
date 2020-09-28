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
package com.oat.domains.bfo.problems;

import com.oat.Solution;
import com.oat.domains.bfo.BFOProblem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.BitStringUtils;

/**
 * Type: BasicTrapFunction<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: As specified in: An analysis of the behavior of simplified evolutionary algorithms on trap functions (2003)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BasicTrapFunction extends BFOProblem
{
    protected double a = 100;
    protected double b = 74;
    protected double z = 25;
    
    
    public BasicTrapFunction()
    {
        length = 100;
    }

    @Override
    protected double problemSpecificCost(Solution n)
    {
        BFOSolution s = (BFOSolution) n;
        double u = BitStringUtils.unitation(s.getBitString());
        if(u<z)
        {
            return (a/z) * (z-u);
        }
        
        return (b / (length-z)) * (u-z);
    }
    


    @Override
    public String getName()
    {
        return "Simple Trap Function";
    }



    @Override
    public boolean isMinimization()
    {
        return true;
    }

    public double getA()
    {
        return a;
    }

    public void setA(double a)
    {
        this.a = a;
    }

    public double getB()
    {
        return b;
    }

    public void setB(double b)
    {
        this.b = b;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
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
