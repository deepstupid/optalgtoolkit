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
package com.oat.experimenter.examples;

import java.io.File;

import com.oat.domains.cfo.CFODomain;
import com.oat.experimenter.Experiment;
import com.oat.experimenter.ExperimentException;
import com.oat.experimenter.ExperimentUtils;
import com.oat.stopcondition.EvaluationsStopCondition;

/**
 * 
 * Description: Provides API examples of creating, updating, loading and deleting an experiment 
 *  
 * Date: 21/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ExampleAPIExperiment
{
	/**
	 * Provides examples for working with an experiment object
	 */
	public void runExample()
	{
		//
		// create
		//
		Experiment exp = new Experiment();
		exp.setName("Example-API1");
		exp.setDescription("This is an example experiment to demonstrate the API.");
		exp.setDomain(new CFODomain());
		exp.setStopCondition(new EvaluationsStopCondition());
		exp.setRunStatisticsList(exp.getDomain().loadDomainRunProbes()); // all of them		
		// retrieve the home directory location
		File homeDir = null;		
		try
		{
			homeDir = ExperimentUtils.getDefaultHomeDirectory();
		} 
		catch (ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		// example of validation
		try
		{
			exp.validate(true, homeDir);
		}
		catch(ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}		
		// attempt to store experiment
		try
		{
			exp.save(homeDir);			
			System.out.println(">Successfully created experiment: " + exp);
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}		
		String nameOfKnownExperiment = exp.getName();
		//
		// attempt to recreate
		//
		try
		{
			exp.save(homeDir); // will fail
			throw new RuntimeException("Something unexpected happened in the example");
		}
		catch(ExperimentException e)
		{
			System.out.println(">Correctly failed to recreate: " + e.getMessage());
		}
		//
		// update correct
		//		
		String description = exp.getDescription();
		System.out.println(">Current Description: " + description);
		description = description.concat(" This is an amended description.");
		exp.setDescription(description);
		try
		{
			exp.update();
			System.out.println(">Successfully updated experiment description: " + exp);
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}	
		//
		// update fail
		//				
		try
		{
			exp.setName("This will fail, cannot change the name of a saved experiment"); // fail
			throw new RuntimeException("Something unexpected happened in the example");
		}
		catch(RuntimeException e)
		{			
			System.out.println(">Correctly failed to change the name of an experiment: " + e.getMessage());
		}
		//
		// load correct
		//				
		Experiment exp2 = new Experiment();		
		try
		{			
			exp2.load(homeDir, nameOfKnownExperiment);
			System.out.println(">Successfully loaded experiment: " + exp2);
			System.out.println(">Updated Description: " + exp2.getDescription());
		}
		catch(ExperimentException e)
		{			
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}
		//
		// load incorrect
		//
		Experiment exp3 = new Experiment();	
		try
		{
			// load experiment directory
			exp3.load(homeDir, nameOfKnownExperiment+"a"); // fail
			throw new RuntimeException("Something unexpected happened in the example");
		}
		catch(ExperimentException e)
		{
			System.out.println(">Correctly failed to load an invalid experiment name: " + e.getMessage());			
		}		
		// 
		// delete valid
		//
		try
		{
			exp2.delete();
			System.out.println(">Successfully deleted experiment: " + exp);
		}
		catch(ExperimentException e)
		{
			throw new RuntimeException("Something unexpected happened in the example.", e);
		}	
		//
		// delete invalid
		//
		try
		{
			exp2.delete(); // fail
			throw new RuntimeException("Something unexpected happened in the example");
		}
		catch(ExperimentException e)
		{
			System.out.println(">Correctly failed to delete a second time: " + e.getMessage());
		}	
	}
	


	public static void main(String[] args)
	{
		new ExampleAPIExperiment().runExample();
	}
}
