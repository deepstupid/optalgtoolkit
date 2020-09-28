/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.experimenter.stats;

import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.experimenter.stats.analysis.npopulation.ANOVATest;
import com.oat.experimenter.stats.analysis.npopulation.KruskalWallisTest;
import com.oat.experimenter.stats.analysis.twopopulation.MannWhitneyUTest;
import com.oat.experimenter.stats.analysis.twopopulation.StudentTTest;
import com.oat.experimenter.stats.normality.AndersonDarlingTest;
import com.oat.experimenter.stats.normality.CramerVonMisesCriterion;
import com.oat.experimenter.stats.normality.KolmogorovSmirnovTest;
import com.oat.experimenter.stats.normality.NormalityTest;

/**
 * Type: StatisticUtilities<br/>
 * Date: 07/08/2007<br/>
 * <br/>
 * Description: Collection of statistics utilities
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class StatisticUtilities
{
	
	/**
	 * Returns a list of statistical tests
	 * @return
	 */
    public static StatisticalComparisonTest [] loadStatisticalComparisonTests()
    {
        return new StatisticalComparisonTest[]
                                {
                new StudentTTest(),
                new KruskalWallisTest(),
                new MannWhitneyUTest(),
                new ANOVATest()
                };
    }
    
    /**
     * Returns a list of normality tests
     * @return
     */
    public static NormalityTest [] loadNormalityTests()
    {
    	return new NormalityTest[]
         {
    			new AndersonDarlingTest(),
    			new CramerVonMisesCriterion(),
    			new KolmogorovSmirnovTest()
         };
    }
}
