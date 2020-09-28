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

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import com.oat.AlgorithmExecutor;
import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.utils.BeanUtils;
import com.oat.utils.FileUtils;

/**
 * Description: 
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
public class ExperimentalRunUtils
{
    public final static String SCHEDULE_SUFFIX = "-schedule";
    public final static String SCHEDULE_EXT = ".sch";
    
    public final static String RESULT_EXT = ".csv";    

	
    /**
     * Execute the defined run under the defined experiment 
     * 
     * @param run - defined run 
     * @param repeatNumber - the repeat of the run
     * @return
     */
    public static RunResult executeSingleRepeat(Experiment exp, ExperimentalRun run, int repeatNumber)
    {
    	// prepare the executor
    	AlgorithmExecutor executor = new AlgorithmExecutor(); 
    	executor.setProblem(run.getProblem());
    	executor.setAlgorithm(run.getAlgorithm());    	
    	executor.addStopCondition(exp.getStopCondition());
    	executor.addRunProbes(exp.getRunStatistics());

        // prepare result
        RunResult result = new RunResult();
        result.setRepeatNumber(repeatNumber);
        
        // set the random number seed
        BeanUtils.beanSetSeed(executor.getAlgorithm(), repeatNumber);  
        
        // run
        try
        {
        	executor.executeAndWait();
        }
        // bad  configuration
        catch(final InvalidConfigurationException e)
        {
            String error = "Problem with configuraiton: "+e.getMessage();
            result.setErrorMessage(error);
            result.setWasError(true);
        }                
        catch(final InitialisationException e)
        {
            String error = "Problem with initialisation: "+e.getMessage();
            result.setErrorMessage(error);
            result.setWasError(true);
        }
        // some unknown fatal condition 
        catch(final AlgorithmRunException e)
        {
            String error = "Unexpected error during algorithm run: "+e.getMessage();
            result.setErrorMessage(error);
            result.setWasError(true);
        }           
        finally
        {
            if(!result.isWasError())
            {
                // only collect results if no error
                result.collectResults(exp, run);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param run
     * @return
     */
    public static String runToResultsFilename(ExperimentalRun run)
    {
        return run.getId()+RESULT_EXT;
    }
    
   
    /**
     * Executes all repeats of the provided run, stores the results to disk and informs the run
     * instances that it has completed
     * @param run
     * @param experiment
     * @throws ExperimentException
     */
    public static void executeRunAndStoreResult(ExperimentalRun run, Experiment experiment)
    	throws ExperimentException
    {
    	// execute run
    	LinkedList<RunResult> runRepeats = ExperimentalRunUtils.executeAllRepeats(run, experiment);
    	// output results
    	ExperimentalRunUtils.outputResults(experiment, run, runRepeats);
    }
    
    
    
    
    /**
     * Execute a run, with n-repeats.
     * Simple implementation that does not consider any stop requests. 
     * 
     * @param run
     * @param experiment
     * @return
     */
    public static LinkedList<RunResult> executeAllRepeats(ExperimentalRun run, Experiment experiment)
    {
        LinkedList<RunResult> runRepeats = new LinkedList<RunResult>(); 
                    
        // execute repeats from 1 to n inclusive
        for (int i = 1; i <= run.getRepeats(); i++)
        {        
            // execute the single run
            RunResult r = ExperimentalRunUtils.executeSingleRepeat(experiment, run, i);
            // store the result
            runRepeats.add(r);
        }
        
        return runRepeats;
    }
    
    
    
    /**
     * Store the results for the specified run under the specified experiment
     * 
     * @param exp - defined experiment
     * @param run - defined run
     * @param runRepeats - run results
     * 
     */
    public static void outputResults(Experiment exp, ExperimentalRun run, LinkedList<RunResult> runRepeats)
    	throws ExperimentException
    {
        // write to string
        StringBuffer b = new StringBuffer();
        b.append("# Experimental Run " + new Date() + "\n");
        //b.append("# Configuration " + run.toStringEntry() + "\n");
        // header
        b.append(runRepeats.getFirst().toHeaderResultString() + "\n");
        // output all results
        for(RunResult r : runRepeats)
        {
            b.append(r.toResultString() + "\n");
        }
        
        // out in file
        File f = new File(exp.getExperimentHomeDir(), runToResultsFilename(run));
        try
        {
            FileUtils.writeToFile(b.toString(), f);
        }
        catch (Exception e)
        {
            throw new ExperimentException("Unable to write result file: " + e.getMessage(), e);
        }
        
        // update status
        run.completed(getRunCompletionDate(exp, run));
    }
    
    

    
    /**
     * Writes the entire experiment run schedule to file, overwriting if required
     * Facilitates modifications to experiments, and any other changes to the schedule 
     * such as amendments and deletions
     * 
     * @param exp - an experiment definition, if no runs, an empty file is written
     */
    public static void externaliseEntireRunSchedule(Experiment exp)
    	throws ExperimentException
    {
        File expDir = exp.getExperimentHomeDir();        
        String schName = experimentNameToScheduleFilename(exp.getName());
        File f = new File(expDir, schName); 
        
        // prepare data
        StringBuffer b = new StringBuffer();
        LinkedList<ExperimentalRun> list = exp.getRuns();
        for(ExperimentalRun r : list)
        {
            b.append(r.toStringEntry());
            b.append("\n");
        }
        
        //externalise, overwriting the file if necessary
        try
		{
			FileUtils.writeToFile(b.toString(), f);
		}
		catch (Exception e)
		{
			throw new ExperimentException("Unable to create experimental run schedule: " + f);
		}
    }
    

    
    /**
	 * 
	 * @param experimentName
	 * @return
	 */
    public static String experimentNameToScheduleFilename(String experimentName)
    {
        return experimentName + SCHEDULE_SUFFIX + SCHEDULE_EXT;
    }
    

    
    /**
     * Load all runs for the specified experiment definition, may return null if none defined (no schedule)
     * Requires that the experiment base directory is defined in the object
     * 
     * @param exp - defined experiment
     * @return - list of runs, or null
     */
    public static ExperimentalRun [] loadRunsForExperiment(Experiment exp)
    	throws ExperimentException, InvalidConfigurationException, InitialisationException
    {
        File expDir = exp.getExperimentHomeDir();
        String schName = experimentNameToScheduleFilename(exp.getName());
        File f = new File(expDir, schName);        
        // does the file exist
        if(!f.exists())
        {
            return null;
        }
        
        // load file in
        String raw = null;
        try
        {
            raw = FileUtils.loadFile(f);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to load schedule file " + f.getName() + " " + e.getMessage(), e);
        }
        
        LinkedList<ExperimentalRun> entries = new LinkedList<ExperimentalRun>();
        String [] lines = raw.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            if(lines[i].startsWith("#") || lines[i].startsWith("//"))
            {
                continue;
            }
            // populate
            ExperimentalRun e = new ExperimentalRun();
            e.fromStringEntry(lines[i]);
            // experiment run is complete or not
            Date completed = getRunCompletionDate(exp, e);
            e.completed(completed);
            
            entries.add(e);
        }
        
        
        return entries.toArray(new ExperimentalRun[entries.size()]);        
    }
    
    
    /**
     * 
     * @param exp
     * @param run
     * @return
     */
    public static Date getRunCompletionDate(Experiment exp, ExperimentalRun run)
    {
        File f = new File(exp.getExperimentHomeDir(), runToResultsFilename(run));
        if(f.exists())
        {
            return new Date(f.lastModified());
        }
        
        return null;
    }
    
    
    public static boolean hasResults(Experiment exp, ExperimentalRun run)
    {
    	File f = new File(exp.getExperimentHomeDir(), runToResultsFilename(run));
    	if(f.exists())
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @param e
     * @return
     */
    public static int getNextValidRunIdNumber(Experiment e)
    {
    	int id = e.getRunsDefined();    	
    	if(id == 0)
    	{
    		return 0;
    	}        
        LinkedList<ExperimentalRun> runs = e.getRuns();
        // keep guessing until a valid id is found
        boolean isValidId = false;
        do
        {
            // optimistic
            isValidId = true;
            // try
            for (int i = 0; i < runs.size(); i++)
            {
                ExperimentalRun r = runs.get(i);
                if(r.getId().equals("R"+id))
                {
                    // failed
                    isValidId = false;
                    id++;
                    break;
                }
            }
        }
        while(!isValidId);
        
        return id;
    }
    
    
    public static String toRunId(int aRunNumber)
    {
    	return "R"+aRunNumber;
    }
    
    /**
     * 
     * @param e
     * @return
     */
    public static String getNextValidRunId(Experiment e)
    {               
        return toRunId(getNextValidRunIdNumber(e));
    }
    
    
    /**
     * 
     * @param exp
     * @param run
     * @return
     */
    public static RunResult [] loadRunResult(Experiment exp, ExperimentalRun run)
    	throws ExperimentException
    {
        LinkedList<RunResult> results = new LinkedList<RunResult>();
        
        File f = new File(exp.getExperimentHomeDir(), runToResultsFilename(run));
        if(!f.exists())
        {
            return null;
        }        
        // load file in
        String raw = null;
        try
        {
            raw = FileUtils.loadFile(f);
        }
        catch(Exception e)
        {
            throw new ExperimentException("Unable to load result file " + f.getName() + " " + e.getMessage(), e);
        }
        
        // process each line
        String [] lines = raw.split("\n");
        boolean haveHeader = false;
        String [] header = null;
        int repeatCount = 0;
        for (int i = 0; i < lines.length; i++)
        {
            if(lines[i].startsWith("#") || lines[i].startsWith("//"))
            {
                continue;
            }
            else if(!haveHeader)
            {
                // we have a header
                haveHeader = true;
                header = lines[i].split(",");
            }
            else
            {
                RunResult r = new RunResult();
                // read in the result
                r.collectResults(header, lines[i]);
                // set the repeat number
                r.setRepeatNumber(repeatCount++); // is this really needed? 
                // add to the list
                results.add(r);
            }
        }
        
        return results.toArray(new RunResult[results.size()]);
    }
    
    
    
    
    public static void deleteAllRuns(Experiment exp)
    	throws ExperimentException
    {
    	LinkedList<ExperimentalRun> runs = (LinkedList<ExperimentalRun>) exp.getRuns().clone();
    	for(ExperimentalRun run : runs)
    	{	
    		deleteRun(exp, run);
    	}    	
    }
    
    public static void deleteRun(Experiment exp, ExperimentalRun run)
		throws ExperimentException
	{
		// ensure the run exists in the experiment
		if (!exp.getRuns().contains(run))
		{
			throw new ExperimentException(
					"Unable to delete run, does not exist in experiment schedule");
		}
		// check for completed results
		if (hasResults(exp, run))
		{
			// attempt to delete run results
			deleteRunResults(exp, run);
		}
		// remove from experiment
		if (!exp.getRuns().remove(run))
		{
			throw new ExperimentException(
					"Unable to delete run from the experiment, not found!");
		}
	}
    
    
    public static void deleteRunAndExternalizeSchedule(Experiment exp, ExperimentalRun run)
    	throws ExperimentException
    {
    	// ensure the run exists in the experiment
    	if(!exp.getRuns().contains(run))
    	{
    		throw new ExperimentException("Unable to delete run, does not exist in experiment schedule");
    	}
    	// check for completed results
    	if(hasResults(exp, run))
    	{
    		// attempt to delete run results
    		deleteRunResults(exp, run);
    	}    	
    	// remove from experiment    	
    	if(!exp.getRuns().remove(run))
    	{
    		throw new ExperimentException("Unable to delete run from the experiment, not found!");
    	}
    	// update the schedule on disk
    	externaliseEntireRunSchedule(exp);
    }
    
    public static void deleteAllRunResults(Experiment exp)
    		throws ExperimentException
	{
    	if(exp.getRunsCompleted()>0)
    	{
    		LinkedList<ExperimentalRun> runs = exp.getRuns();
    		for(ExperimentalRun run : runs)
    		{
    			if(run.isCompleted())
    			{
    				deleteRunResults(exp, run);
    			}
    		}
    	}
	}
    
    public static void deleteRunResults(Experiment exp, ExperimentalRun run)
		throws ExperimentException
	{
    	// the file
    	File resultFile = new File(exp.getExperimentHomeDir(), runToResultsFilename(run));
    	// must exist
    	if(!resultFile.exists())
    	{
    		throw new ExperimentException("Unable to delete run result file, does not exist.");
    	}
    	// delete the result
        if(!resultFile.delete())
        {
            throw new ExperimentException("Cannot delete experiment run result: " + resultFile);
        }
        // no longer completed
        run.completed(null);
	}
}
