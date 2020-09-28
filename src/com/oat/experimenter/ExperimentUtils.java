/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2007  Jason Brownlee

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
import java.util.LinkedList;

import com.oat.RunProbe;
import com.oat.utils.BeanUtils;

/**
 * Type: ExperimentUtils<br/>
 * Date: 30/07/2007<br/>
 * <br/>
 * Description: Utilities related to loading/creating experiments
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
public class ExperimentUtils
{
    public final static int DEFAULT_REPEATS = 30;    
    
    public final static String EXPERIMENT_HOME = "./experiments";
    

    
    /**
     * Creates the default experiment home directory if required
     * 
     * @return
     * @throws ExperimentException
     */
    public static File getDefaultHomeDirectory()
    	throws ExperimentException
    {
    	File homeDir = new File(EXPERIMENT_HOME);
    	
    	if(!homeDir.exists())
    	{
            if(!homeDir.mkdir())
            {
                throw new ExperimentException("Error creating experiment home directory " + EXPERIMENT_HOME);
            }
    	}
    	
    	return homeDir;
    }
    
    /**
     * Load all experiments in the specified home directory
     * 
     * @param homeDir
     * @return
     * @throws ExperimentException
     */
    public static Experiment [] loadExperiments(File homeDir)
    	throws ExperimentException
    {        
        // scan through all directories and find valid experiment directories
        File [] list = homeDir.listFiles();
        LinkedList<Experiment> experiments = new LinkedList<Experiment>();
        for (int i = 0; i < list.length; i++)
        {
            // must be a directory
            if(list[i].isDirectory())
            {
                // create
                Experiment e = new Experiment();
                // load
                e.load(homeDir, list[i].getName());
                // add
                experiments.add(e);
            }
        }
        
        return experiments.toArray(new Experiment[experiments.size()]);
    }
    
    /**
     * 
     * @param rs
     * @return
     */
    public static String tokeniseRunStatistics(RunProbe [] rs)
    {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < rs.length; i++)
        {
            b.append(rs[i].getClass().getName());
            if(i!=rs.length-1)
            {
                b.append(BeanUtils.TOKEN);
            }
        }
        return b.toString();
    }
    
    /**
     * 
     * @param sc
     * @return
     */
    public static RunProbe [] parseRunStatistics(String sc)
    {
        String [] s = sc.split(""+BeanUtils.TOKEN);
        RunProbe [] l = new RunProbe[s.length];
        for (int i = 0; i < s.length; i++)
        {
            try
            {
                l[i] = (RunProbe) (Class.forName(s[i])).newInstance();
            }
            catch (Exception e)
            {
               throw new RuntimeException("Unable to load run statistic " + s[i]);
            }
        }
        
        return l;
    }

}
