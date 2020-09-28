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
package com.oat.domains.cfo.problems.mahfoud;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: M6<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Niching Methods for Genetic Algorithms (Mahfoud) (1995)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class M6 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double sum = 0.0;
        for (int i = 0; i < 25; i++)
        {           
            double a = 16.0 * ((i % 5.0) - 2.0);
            double b = 16.0 * ((i / 5) - 2.0);
            double lower = 1.0 + i + Math.pow((x - a), 6.0) + Math.pow((y - b), 6.0);
            sum += 1.0 / lower;
        }
        
        return 500 - (1.0 / (0.002 + sum));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-65.536, +65.535}; // different from De Jong F5
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
    	return new double[][]{
        		{32, 32},
        		{32,16},
        		{32,0},
        		{32,-16},
        		{32,-32},
        		{16, 32},
        		{16,16},
        		{16,0},
        		{16,-16},
        		{16,-32},
        		{0, 32},
        		{0,16},
        		{0,0},
        		{0,-16},
        		{0,-32},
        		{-16, 32},
        		{-16,16},
        		{-16,0},
        		{-16,-16},
        		{-16,-32},
        		{-32, 32},
        		{-32,16},
        		{-32,0},
        		{-32,-16},
        		{-32,-32}
        		};
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "M6 (Shekel's Foxholes)";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
