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
package com.oat.domains.psp;

import java.util.Arrays;

import com.oat.Solution;

/**
 * Type: ProtFoldSolution<br/>
 * Date: 12/12/2006<br/>
 * <br/>
 * Description: Represents a solution to the 2DHP Protein Structure Prediction Problem (Protein Folding)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre> 
 */
public abstract class PSPSolution extends Solution
{
    protected final byte [] permutation;
    protected int totalTopologicalHH = 0;
    protected int lengthBeforeInfeasible = 0;
    protected boolean isFeasibleConformation = false;
    
    /**
     * Constructor
     * @param s
     */
    public PSPSolution(byte [] s)
    {
        permutation = s;
    }
    
    
    public abstract byte [][] retrieveLattice(boolean [] aDataset);
    public abstract String permutationToString();
    
    
    @Override
    public String toString()
    {
        return super.toString() + ", " + 
        "TopologicalHHConnections="+totalTopologicalHH+", " +
        "Feasible="+isFeasibleConformation+", " +
        "LengthBeforeInfeasible="+lengthBeforeInfeasible+", " +
        "Permutation="+permutationToString();
    }
    
    @Override
    public boolean equals(Object o)
    {
        PSPSolution s = (PSPSolution) o;
        return Arrays.equals(permutation, s.permutation);
    }

    public byte[] getPermutation()
    {
        return permutation;
    }

    public int getTotalTopologicalHH()
    {
        return totalTopologicalHH;
    }

    public int getLengthBeforeInfeasible()
    {
        return lengthBeforeInfeasible;
    }

    public boolean isFeasibleConformation()
    {
        return isFeasibleConformation;
    }
}
