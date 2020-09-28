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
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;

/**
 * Type: M9<br/>
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
public class M9 extends BFOProblem
{    
    public final static int NUM_SUB_FUNC = 3;
    public final static int SUB_FUNC_LENGTH = 8;
    
    public final static boolean[][] SUB_FUNC_OPTIMA = 
    {
        {false,false,false,false,false,false,false,false},
        {true,false,false,false,true,true,false,false},
        {false,true,false,false,true,false,true,false}
    };
    
    
    public M9()
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
            sum += score(b, i*SUB_FUNC_LENGTH, SUB_FUNC_LENGTH);
        }
        
        return sum;
    }

    protected final static double score(boolean [] b, int start, int length)
    {
        double [] scores = new double[SUB_FUNC_OPTIMA.length];
        for (int i = 0; i < scores.length; i++)
        {
            scores[i] = BitStringUtils.hammingDistance(b,start,length, SUB_FUNC_OPTIMA[i], 0, SUB_FUNC_OPTIMA[i].length);
        }
        
        double min = ArrayUtils.min(scores);
        // check if the func matches one of the optima (no distance from the optima)
        if(min == 0.0)
        {
            return 10.0;
        }
        return min;
    }
    
    @Override
    public String getName()
    {
        return "M9 (minimum distance)";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }
}
