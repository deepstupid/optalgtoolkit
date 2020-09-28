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
package com.oat.experimenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

import com.oat.Domain;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.RunProbe;
import com.oat.StopCondition;
import com.oat.utils.BeanUtils;

/**
 * Date: 31/07/2007<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 21/08/2007	JBrownlee	Improved documentation and embedded validation
 * </pre>
 *
 */
public class Experiment 
	implements Comparable<Experiment>
{
    public final static String EXPERIMENT_EXT = ".exp";    
    public final static String EXPERIMENT_SUFFIX = "-config";
    
    public final static String KEY_NAME = "name";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_DOMAIN = "domain";
    public final static String KEY_STOPCONDITION = "stopcondition";
    public final static String KEY_RUNSTATISTICS = "runstatistics";
	
	
	/**
	 * The name of the experiment
	 */
    protected String name;
    /**
     * Free text description of the experiment
     */
    protected String description;
    /**
     * The restricted domain for the experiment (problems and algorithms)
     */
    protected Domain domain;
    /**
     * The consistent stop condition for the experiment
     */
    protected StopCondition stopCondition;
    /**
     * The consistent statistics to collect for the experiment
     */
    protected RunProbe [] runStatistics;
    /**
     * The runs defined for this experiment
     */
    protected final LinkedList<ExperimentalRun> runs;
    

    /**
     * The base directory for the experiment if created
     */
    protected File experimentHomeDir;
    /**
     * The date/time when the experiment (directory) was last modified
     */
    protected Date lastModified;
    
    
    /**
     * Default Constructor
     */
    public Experiment()
    {
    	runs = new LinkedList<ExperimentalRun>();
    }
    
    @Override
    public int compareTo(Experiment o)
    {
        return name.compareTo(o.name);
    }

    @Override 
    public String toString()
    {
        return name;
    }   
    
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {    	
        this.name = name;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public Domain getDomain()
    {
        return domain;
    }
    public void setDomain(Domain domain)
    {    	
        this.domain = domain;
    }
    public StopCondition getStopCondition()
    {
        return stopCondition;
    }
    public void setStopCondition(StopCondition stopCondition)
    {
        this.stopCondition = stopCondition;
    }


    public File getExperimentHomeDir()
    {
        return experimentHomeDir;
    }

    public Date getLastModified()
    {
        return lastModified;
    }
   

    public RunProbe[] getRunStatistics()
    {
        return runStatistics;
    }

    public void setRunStatistics(RunProbe[] runStatistics)
    {
        this.runStatistics = runStatistics;
    }
    
    public void setRunStatisticsList(LinkedList<RunProbe> probes)
    {
        this.runStatistics = probes.toArray(new RunProbe[probes.size()]);
    }


    public int getRunsDefined()
    {
        return runs.size();
    }
   
    public int getRunsCompleted()
    {
        int count = 0;
        
        for(ExperimentalRun r : runs)
        {
            if(r.isCompleted())
            {
                count++;
            }
        }
        
        return count;
    }

    
    public LinkedList<ExperimentalRun> getRuns()
    {
        return runs;
    }
    
    public void addRun(ExperimentalRun r)
    {
        runs.add(r);
    }
    
    public void addRuns(ExperimentalRun [] rr)
    {
        for(ExperimentalRun r : rr)
        {
            runs.add(r);
        }        
    }
    
    
    /**
     * Whether or not the provided experiment name is in use
     * @param name
     * @param homeDir
     * @return
     */
    public static boolean isExperimentNameInUse(String name, File homeDir)
    {
    	File proposedDir = new File(homeDir, name);
    	return proposedDir.exists();
    }
    
    /**
     * Validate this experiment, assumes that the details are being updated, 
     * that it is not a new experiment and thus a check to see if the name is in use
     * is NOT performed
     * @throws ExperimentException
     */
    public void validate()
		throws ExperimentException
	{
    	validate(false, null);
	}
    
    /**
     * Validate this experiment. 
     * 
     * @param newExperiment
     * @param experimentHomeDir
     * @throws ExperimentException
     */
    public void validate(boolean newExperiment, File experimentHomeDir)
    	throws ExperimentException
    {
        // check for valid name
        if (name==null || name.length() < 1 || name.length() > 15)
        {
           throw new ExperimentException("Invalid Experiment Name: Experiment name must be between 1 and 15 characters.");
        }
        // check for valid directory
        if (newExperiment)
        {
        	if(isExperimentNameInUse(name, experimentHomeDir))
        	{
        		throw new ExperimentException("Invalid Experiment Name: Experiment name already in use.");
        	}
        }
        // check for valid description
        if (description==null || description.length() < 1 || description.length() > 256)
        {
        	throw new ExperimentException("Invalid Experiment Summary: Experiment summary must be between 1 and 256 characters.");
        }
        // check for valid domain
        if (domain == null)
        {
        	throw new ExperimentException("Invalid Experiment Domain: A problem domain must be selected.");
        }
        // have stop condition
        if(stopCondition == null)
        {
        	throw new ExperimentException("No Stop Condition: A stop condition must be selected.");            
        }
        // validate stop condition
        try
        {
        	stopCondition.validateConfiguration();
        }
        catch(InvalidConfigurationException e)
        {
        	throw new ExperimentException("Invalid stop condition configuration: "+e.getMessage(), e);
        }        
        // run statistics
        if(runStatistics==null || runStatistics.length<1)
        {
        	throw new ExperimentException("No Run Statistics: At least one run statistic must be selected.");
        }
    }        
    
    
    /**
     * Converts the provided experiment name to an experiment definition filename
     * @param experimentName
     * @return
     */
    public static String experimentNameToFilename(String experimentName)
    {
        return experimentName + EXPERIMENT_SUFFIX + EXPERIMENT_EXT;
    }
    
   

    /**
     * Update the details of this experiment to disk
     * 
     * @throws ExperimentException
     */
    public void update()
    	throws ExperimentException    	
	{   
    	// must already exist (saved or loaded)
        if(!isSaved())
        {
            throw new ExperimentException("Unable to update, experiment does not exist " + name);
        }
        // must have a valid configuration
        validate();
        
        // prepare a properties file
        Properties properties = new Properties();        
        properties.setProperty(KEY_NAME, getName());
        properties.setProperty(KEY_DESCRIPTION, getDescription());
        properties.setProperty(KEY_DOMAIN, getDomain().getClass().getName());
        properties.setProperty(KEY_STOPCONDITION, BeanUtils.beanToTokenisedString(getStopCondition()));
        properties.setProperty(KEY_RUNSTATISTICS, ExperimentUtils.tokeniseRunStatistics(getRunStatistics()));        
                
        // store
        String filename = experimentNameToFilename(getName());     
        File expConfig = new File(experimentHomeDir, filename);
        FileOutputStream out = null;
        try
        {            
            out = new FileOutputStream(expConfig);
            properties.store(out, null);
        }
        catch (IOException e)
        {            
            throw new ExperimentException("Error saving experiment config file in " + expConfig);
        }
        finally
        {
            if(out != null)
            {
                try
				{
					out.close();
				} 
                catch (IOException ioe)
				{}
            }
        }
        
        // update last modified time
        lastModified = new Date(experimentHomeDir.lastModified());
	}
    
  
    /**
     * Save the details of this new experiment to disk
     * 
     * @param homeDir
     * @throws ExperimentException
     */
    public void save(File homeDir)
    	throws ExperimentException
    {
    	// must be valid
    	validate(true, homeDir);
    	// prepare name
        File expDir = new File(homeDir, getName());
        String filename = experimentNameToFilename(getName());     
        File expConfig = new File(expDir, filename);
        // create
        if(!expDir.mkdir())
        {           
            throw new ExperimentException("Error creating experiment directory: " + expDir);
        }  
        // prepare a properties file
        Properties properties = new Properties();        
        properties.setProperty(KEY_NAME, getName());
        properties.setProperty(KEY_DESCRIPTION, getDescription());
        properties.setProperty(KEY_DOMAIN, getDomain().getClass().getName());
        properties.setProperty(KEY_STOPCONDITION, BeanUtils.beanToTokenisedString(getStopCondition()));
        properties.setProperty(KEY_RUNSTATISTICS, ExperimentUtils.tokeniseRunStatistics(getRunStatistics()));        
                
        // store
        FileOutputStream out = null;
        try
        {            
            out = new FileOutputStream(expConfig);
            properties.store(out, null);
        }
        catch (IOException e)
        {            
            throw new ExperimentException("Error saving experiment config file in " + expDir.getPath());
        }
        finally
        {
            if(out != null)
            {
                try
				{
					out.close();
				} 
                catch (IOException ioe)
				{}
            }
        }
        
        // update last modified time
        lastModified = new Date(expDir.lastModified());
        // update base
        experimentHomeDir = expDir;
    }
    
    
    /**
     * Load the experiment details from disk including the run schedule
     * 
     * @param experimentHomeDir
     * @param experimentName
     * @throws ExperimentException
     */
    public void load(File homeDir, String experimentName)
    	throws ExperimentException
    {
        Properties properties = new Properties();
        File expDir = new File(homeDir, experimentName);
        File expConfig = new File(expDir, experimentNameToFilename(experimentName));
        
        // ensure the directory exists
        if(!expDir.exists() || !expConfig.exists())
        {
            throw new ExperimentException("Unable to load, experiment does not exist " + experimentName);
        }
        
        FileInputStream in = null;
        try
        {            
            in = new FileInputStream(expConfig);
            properties.load(in);
        }
        catch (IOException e)
        {            
            throw new ExperimentException("Error loading experiment: " + experimentName + ", in " + expConfig);
        }
        finally
        {
            if(in!=null)
            {
                try{in.close();}catch(IOException ioe){}
            }
        }
        
        // populate        
        setName(properties.getProperty(KEY_NAME));
        setDescription(properties.getProperty(KEY_DESCRIPTION));
        String domainClassName = properties.getProperty(KEY_DOMAIN);
        try
		{
			setDomain((Domain) (Class.forName(domainClassName)).newInstance());
		} 
        catch (Exception e)
		{
        	throw new ExperimentException("Error loading domain: " + e.getMessage(), e);
		}
        // stop condition
        String sc = properties.getProperty(KEY_STOPCONDITION);
        setStopCondition((StopCondition)BeanUtils.beanFromString(sc));
        // run statistics
        String rs = properties.getProperty(KEY_RUNSTATISTICS); 
        setRunStatistics(ExperimentUtils.parseRunStatistics(rs));        
        // calculated things
        experimentHomeDir = expDir;
        lastModified = new Date(expDir.lastModified());                 
        // load runs for experiment
        try
		{
        	loadRuns();
		} 
        catch (Exception e)
		{
        	throw new ExperimentException("Error loading runs: " + e.getMessage(), e);
		}        
    }
    
    public void loadRuns()
    	throws ExperimentException, InvalidConfigurationException, InitialisationException
    {
    	runs.clear();
        ExperimentalRun [] runs = ExperimentalRunUtils.loadRunsForExperiment(this);
        if(runs != null)
        {
            // store
            addRuns(runs);
        }
    }
    
    /**
     * Deletes this experiment definition from disk
     * Must exist (dir and config file), and have no completed runs
     * 
     * @throws ExperimentException
     */
    public void delete()
    	throws ExperimentException
    {
        // must already exist
        if(!canDelete())
        {
            throw new ExperimentException("Cannot delete experiment, experiment must exist and have no completed runs.");
        } 
        // delete all children
        String [] children = experimentHomeDir.list();
        for (int i = 0; i < children.length; i++)
        {
            File f = new File(experimentHomeDir, children[i]);
            if(!f.delete())
            {
                throw new RuntimeException("Cannot delete experiment file: " + f);
            }
        }
        // delete experiment dir
        if(!experimentHomeDir.delete())
        {
            throw new ExperimentException("Cannot delete experiment, error." + getName());
        }
        // clear aspects of this experiment definition
    	experimentHomeDir = null;
    	lastModified = null;
    	runs.clear();     
    }
        
    /**
     * Whether or not the experiment can be deleted
     * @return
     */
    public boolean canDelete()
    {
    	// can only delete if the run exists
    	if(!isSaved())
    	{
    		return false;
    	}
    	// must have no runs defined
    	if(getRunsCompleted() != 0)
    	{
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Whether or not this experiment has been saved and exists on disk
     * @return
     */
    public boolean isSaved()
    {
    	if(experimentHomeDir == null)
    	{
    		return false;
    	}
    	
    	File expConfig = new File(experimentHomeDir, experimentNameToFilename(getName()));
    	
    	if(experimentHomeDir.exists() && expConfig.exists())
    	{
    		return true;
    	}

    	return false;
    }
}
