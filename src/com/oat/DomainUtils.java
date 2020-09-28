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
package com.oat;

import java.util.Collections;
import java.util.LinkedList;

import com.oat.utils.FileUtils;

/**
 * Description: Utilities for working with Domain objects
 *  
 * Date: 20/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class DomainUtils
{
	/**
	 * The properties file that contains a list of all common domains to be used in GUI's.
	 * Currenty hard-coded to 'domainlist.properties'
	 */
	public final static String DOMAIN_LIST = "domainlist.properties";
	
	
	/**
	 * Load a list of algorithm instances as specified in a configuration file.
	 * Each line of the file must contain a fully qualified class name or a properties (.properties)
	 * file that contains a list of classes. This allows a given file contain a mixed list of
	 * classes and recursive properties files.
	 * @param filename
	 * @return - list of algorithm instances defined in the file, one per line
	 * @throws Exception
	 */
    public static Algorithm[] defaultLoadAlgorithmList(String filename) throws Exception
    {      
        String [] classList = FileUtils.loadClassList(filename);        
        LinkedList<Algorithm> list = new LinkedList<Algorithm>();
        
        for (int i = 0; i < classList.length; i++)
        {
        	String name = classList[i];
        	
            try
            {
            	// check for properties file
            	if(FileUtils.isPropertiesFile(name))
            	{
            		// load recursively
            		Algorithm [] tmp = defaultLoadAlgorithmList(name);
            		for (int j = 0; j < tmp.length; j++)
					{
						list.add(tmp[j]);
					}
            	}
            	else
            	{
            		// create algorithm instances
            		list.add((Algorithm) (Class.forName(name)).newInstance());
            	}
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load algorithm class from properties file: " + name, e);
            }
        }
        // order
        Collections.sort(list);
        // return array
        return list.toArray(new Algorithm[list.size()]);
    }
    
	/**
	 * Load a list of problem instances as specified in a configuration file.
	 * Each line of the file must contain a fully qualified class name or a properties (.properties)
	 * file that contains a list of classes. This allows a given file contain a mixed list of
	 * classes and recursive properties files.
	 * @param filename
	 * @return - list of problem instances defined in the file, one per line
	 * @throws Exception
	 */
    public static Problem [] defaultLoadProblemList(String filename) throws Exception
    {      
        String [] classList = FileUtils.loadClassList(filename);        
        LinkedList<Problem> list = new LinkedList<Problem>();
        
        for (int i = 0; i < classList.length; i++)
        {
        	String name = classList[i];
        	
            try
            {
            	// check for properties file
            	if(FileUtils.isPropertiesFile(name))
            	{
            		// load recursively
            		Problem [] tmp = defaultLoadProblemList(name);
            		for (int j = 0; j < tmp.length; j++)
					{
						list.add(tmp[j]);
					}
            	}
            	else
            	{
            		// create algorithm instances
            		list.add((Problem) (Class.forName(name)).newInstance());
            	}
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load problem class from properties file: " + name, e);
            }
        }
        // order
        Collections.sort(list);
        // return array
        return list.toArray(new Problem[list.size()]);
    }
    
	/**
	 * Load a list of domain instances as specified in a configuration file.
	 * Each line of the file must contain a fully qualified class name or a properties (.properties)
	 * file that contains a list of classes. This allows a given file contain a mixed list of
	 * classes and recursive properties files.
	 * @param filename
	 * @return - list of domains instance defined in the file, one per line
	 * @throws Exception
	 */
    public static Domain [] loadDomainList(String filename) throws Exception
    {      
        String [] classList = FileUtils.loadClassList(filename);        
        LinkedList<Domain> list = new LinkedList<Domain>();
        
        for (int i = 0; i < classList.length; i++)
        {
        	String name = classList[i];
        	
            try
            {
            	// check for properties file
            	if(FileUtils.isPropertiesFile(name))
            	{
            		// load recursively
            		Domain [] tmp = loadDomainList(name);
            		for (int j = 0; j < tmp.length; j++)
					{
						list.add(tmp[j]);
					}
            	}
            	else
            	{
            		// create algorithm instances
            		list.add((Domain) (Class.forName(name)).newInstance());
            	}
            }
            catch (Exception e)
            {
                throw new Exception("Unable to load problem class from properties file: " + name, e);
            }
        }
        // order
        Collections.sort(list);
        // return array
        return list.toArray(new Domain[list.size()]);
    }
    

    
}
