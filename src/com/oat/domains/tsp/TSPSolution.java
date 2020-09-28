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
package com.oat.domains.tsp;

import java.util.Arrays;

import com.oat.Solution;
import com.oat.utils.ArrayUtils;

/**
 * Type: TSPSolution<br/>
 * Date: 10/03/2006<br/>
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
public class TSPSolution extends Solution
{    
    protected final int [] permutation;    

    public TSPSolution(TSPSolution s)
    {        
        this(ArrayUtils.copyArray(s.permutation));
    }
    
    public TSPSolution(int [] p)
    {
        permutation = p;
    }
    
    @Override
    public boolean equals(Object o)
    {
        TSPSolution s = (TSPSolution) o;
        return Arrays.equals(permutation, s.permutation);
    }
    
    public int[] getPermutation()
    {
        return permutation;
    }
    
    @Override
    public String toString()
    {
        return Arrays.toString(permutation);
    }
}
