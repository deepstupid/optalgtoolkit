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
package com.oat.domains.bfo;

import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;

/**
 * Type: BinaryProblem<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 06/07/2007   JBrownlee   Added support for GUI configuration and validation
 * 07/08/2007   JBrownlee   Implemented generic binary problem interface
 * </pre>
 */
public abstract class BFOProblem extends Problem
    implements BFOProblemInterface
{
    /**
     * Length of the binary string
     */
    protected int length = -1;
    
    
    public BFOProblem()
    {}
    
    
    @Override
    public void checkSolutionForSafety(Solution b) 
        throws SolutionEvaluationException
    {
        if(((BFOSolution)b).getBitString().length != length)
        {
            throw new SolutionEvaluationException("bitstring length "+((BFOSolution)b).getBitString().length+" does not match expected length " + length);
        }
    }
    
    @Override
    protected void validateConfigurationInternal() throws InvalidConfigurationException
    {           
        if(length<1||length>=Integer.MAX_VALUE)
        {
            throw new InvalidConfigurationException("Invalid length " + length);
        }
    }

	@Override
    public String getDetails()
    {
    	StringBuffer b = new StringBuffer();
    	b.append(super.getDetails()+", ");
    	b.append("Length="+getBinaryStringLength());
    	return b.toString();
    }
    
    public int getBinaryStringLength()
    {
        return length;
    }
}
