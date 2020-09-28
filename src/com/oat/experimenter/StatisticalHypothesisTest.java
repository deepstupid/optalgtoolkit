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
package com.oat.experimenter;

/**
 * Description: Generic interface for statistical hypothesis testing 
 *  
 * Date: 23/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public interface StatisticalHypothesisTest
{
	/**
	 * Default significance level (alpha) of 0.05 (5%)
	 */
	double ALPHA_FIVE_PERCENT = 0.05;
	
	
    /**
     * Whether or not the NULL Hypothesis (H0) can be rejected 
     * with a P-value of at least a 0.05 (95% confidence). 
     * 
     * A simple usage interface of a statistical test
     * 
     * @return
     */
	boolean canRejectNullHypothesis();
	
	/**
	 * A Human readable description of the NULL hypothesis
	 * @return
	 */
	String nullHypothesisDescription();
	
	/**
	 * The significance of a result is also called its p-value; 
	 * the smaller the p-value, the more significant the result is said to be.
	 * See: 
	 * (1) http://en.wikipedia.org/wiki/Statistical_significance
	 * (2) http://en.wikipedia.org/wiki/P_value
	 * @return
	 */
	double getPValue();
	
	/**
	 * The name of this statistical test
	 * @return
	 */
	String getName();
}
