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


/**
 * Type: RelativeProtFoldSolution<br/>
 * Date: 12/12/2006<br/>
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
public class PSPSolutionRelative extends PSPSolution
{
    public PSPSolutionRelative(byte[] s)
    {
        super(s);
    }

    @Override
    public String permutationToString()
    {        
        return "[" + PSPUtils.relativePermutationToString(permutation) + "]";
    }

    @Override
    public byte[][] retrieveLattice(boolean[] aDataset)
    {
        // ensure that the absolute permutation is valid
        PSPUtils.isValidRelativePermutation(permutation, aDataset);
        
        byte[][] lattice = null;
        try
        {
            lattice = PSPUtils.relativePermutationToLattice(permutation, aDataset);
            lengthBeforeInfeasible = 0;
            isFeasibleConformation = true;
        }
        catch(InvalidModelException e)
        {
            lattice = e.getLattice();
            lengthBeforeInfeasible = e.getCollisionLength();
            isFeasibleConformation = false;
        }
        
        return lattice;
    }

}
