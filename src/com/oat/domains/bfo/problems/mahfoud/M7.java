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
package com.oat.domains.bfo.problems.mahfoud;

import com.oat.Solution;
import com.oat.domains.bfo.BFOProblem;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.BitStringUtils;

/**
 * Type: M7<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: As described in: Niching Methods for Genetic Algorithms [PhD thesis] (1995)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class M7 extends BFOProblem
{
    public final static int NUM_SUB_FUNC = 5;
    public final static int SUB_FUNC_LENGTH = 6;
    
    public M7()
    {
        length = NUM_SUB_FUNC*SUB_FUNC_LENGTH;
    }

    @Override
    protected double problemSpecificCost(Solution n)
    {        
        BFOSolution s = (BFOSolution) n;
        boolean [] b = s.getBitString(); 
        double sum = 0.0;
                
        for (int i = 0; i < NUM_SUB_FUNC; i++)
        {
            int u = BitStringUtils.unitation(b, i*SUB_FUNC_LENGTH, SUB_FUNC_LENGTH);
            sum += calculateScore(u);
        }
        return sum;
    }
    
    /**
     * A strait intepritation of the bio-modal deceptive function 
     * from the graph in Mahfoud's thesis
     * @param u
     * @return
     */
    protected final static double calculateScore(int u)
    {
        double score = 0.0;
        
        switch(u)
        {
            case 0:
            case 6:
            {
                score = 1.0;
                break;
            }            
            case 1:
            case 5:
            {
                score = 0.0;
                break;
            }
            case 2:
            case 4:
            {
                score = 0.4;
                break;
            }
            case 3:
            {
                score = 0.6;
                break;
            }
            default:
            {                   
                throw new RuntimeException("Invalid number of ones: " + u);
            }
        }
        
        return score;
    }
    

    @Override
    public String getName()
    {
        return "M7 (massively multimodal)";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }
}
