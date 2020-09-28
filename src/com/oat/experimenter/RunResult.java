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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.oat.RunProbe;
import com.oat.utils.FileUtils;

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
 * 
 * </pre>
 *
 */
public class RunResult
{    
    public final static String ERROR_TOKEN = "Error";
    
    protected int repeatNumber;
    
    protected String errorMessage;
    protected boolean wasError;

    // maintains ordering
    protected LinkedHashMap<String,String> results;


    /**
     * 
     */
    public RunResult()
    {
        results = new LinkedHashMap<String,String>(); 
    }
    


    public Object getResult(String name)
    {
        return results.get(name);
    }
    
        
    public void collectResults(Experiment exp, ExperimentalRun run)
    {        
        RunProbe [] stats = exp.getRunStatistics();
        for (int i = 0; i < stats.length; i++)
        {
            results.put(stats[i].getName(), stats[i].getProbeObservation().toString());
        }
    }
    
    public void collectResults(String [] names, String valueCSV)
    {        
        if(valueCSV.startsWith(ERROR_TOKEN))
        {
            errorMessage = valueCSV.substring(valueCSV.indexOf(':')+1);
            wasError = true;
        }
        else
        {
        	// always parse cleanly
        	String [] parts = FileUtils.parseCSVLine(valueCSV);
        	     	
            // validate
            if(parts.length != names.length)
            {
                throw new RuntimeException("Number of columns and the number of results do not match for line: " + valueCSV);
            }
            // store
            for (int i = 0; i < names.length; i++)
            {
                results.put(names[i], parts[i]);
            }
        }
    }
    
    public String toResultString()
    {
        if(wasError)
        {
            return ERROR_TOKEN + ": " + errorMessage;
        }
        
        Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
        int size = results.size();
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < size; i++)
        {                
            Map.Entry<String, String> e = it.next();            
            String v = e.getValue().toString(); 
            
            // always output results with quotes
        	b.append("\"");
        	b.append(v);
        	b.append("\"");                     
            
            if(i!=size-1)
            {   
                b.append(",");
            }
        }
        return b.toString();
    }
    
    public String toHeaderResultString()
    {
        Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
        int size = results.size();
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < size; i++)
        {                
            Map.Entry<String, String> e = it.next();
            b.append(e.getKey());
            if(i!=size-1)
            {   
                b.append(",");
            }
        }
        return b.toString();
    }
    
    public String [] toHeaderStrings()
    {
        Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
        int size = results.size();
        String [] s = new String[size]; 
        for (int i = 0; i < size; i++)
        {                
            Map.Entry<String, String> e = it.next();
            s[i] = e.getKey();
        }
        return s;
    }
    public Object [] toResultObjects()
    {
        if(wasError)
        {
            return null;
        }
        
        Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
        int size = results.size();
        Object [] o = new Object[size];
        for (int i = 0; i < size; i++)
        {                
            Map.Entry<String, String> e = it.next();
            o[i] = e.getValue();
        }
        return o;
    }
    

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public boolean isWasError()
    {
        return wasError;
    }

    public void setWasError(boolean wasError)
    {
        this.wasError = wasError;
    }

    public int getRepeatNumber()
    {
        return repeatNumber;
    }

    public void setRepeatNumber(int repeatNumber)
    {
        this.repeatNumber = repeatNumber;
    }    
}
