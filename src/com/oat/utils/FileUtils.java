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
package com.oat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Type: FileUtils<br/>
 * Date: 12/05/2006<br/>
 * <br/>
 * Description: Utilities for handling files
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public class FileUtils
{
    /**
     * Read in a file as a string
     * @param filename
     * @return
     * @throws IOException
     */
    public final static String loadFile(File file)
        throws IOException
    {       
        InputStream in = null;
        byte [] b = new byte[1024*5];
        
        int offset = 0;
        
        try
        {
            in = new FileInputStream(file);
            int t = 0;
            while((t=in.read(b, offset, b.length-offset)) != -1)
            {
                offset += t;
                if(offset == b.length)
                {
                    // not finished, and there are still bytes to read
                    byte [] newbuffer = new byte[b.length*2];
                    System.arraycopy(b, 0, newbuffer, 0, offset);
                    b = newbuffer;
                }
            }
        }
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e)
                {}
            }
        }
        
        return new String(b, 0, offset);
    }    
    
    
    /**
     * Read in a file as a string
     * @param filename
     * @return
     * @throws IOException
     */
    public final static String loadFile(String filename)
        throws IOException
    {       
        InputStream in = null;
        byte [] b = new byte[1024*5];
        
        int offset = 0;
        
        try
        {
            in = FileUtils.class.getResourceAsStream("/"+filename);
            int t = 0;
            while((t=in.read(b, offset, b.length-offset)) != -1)
            {
                offset += t;
                if(offset == b.length)
                {
                    // not finished, and there are still bytes to read
                    byte [] newbuffer = new byte[b.length*2];
                    System.arraycopy(b, 0, newbuffer, 0, offset);
                    b = newbuffer;
                }
            }
        }
        finally
        {
            if(in != null)
            {
                try
                {
                    in.close();
                }
                catch(Exception e)
                {}
            }
        }
        
        return new String(b, 0, offset);
    }
    
    /**
     * Write a string out as a file
     * @param data
     * @param fileout
     * @throws Exception
     */
    public static void writeToFile(String data, String fileout)
        throws Exception
    {
        FileWriter writer = null;
        
        try
        {
            writer = new FileWriter(fileout);
            writer.write(data);
            writer.flush();
        }
        finally
        {
            if(writer != null)
            {
                writer.close();
            }
        }       
    }
    
    /**
     * Write a string out as a file
     * @param data
     * @param fileout
     * @throws Exception
     */
    public static void writeToFile(String data, File fileout)
        throws Exception
    {
        FileWriter writer = null;
        
        try
        {
            writer = new FileWriter(fileout);
            writer.write(data);
            writer.flush();
        }
        finally
        {
            if(writer != null)
            {
                writer.close();
            }
        }       
    }
    
    /**
     * Append to specified file
     * @param data
     * @param fileout
     * @throws Exception
     */
    public static void appendToFile(String data, File fileout)
        throws Exception
    {
        FileWriter writer = null;
        
        try
        {
            writer = new FileWriter(fileout, true);
            writer.write(data);
            writer.flush();
        }
        finally
        {
            if(writer != null)
            {
                writer.close();
            }
        }       
    }
    
    /**
     * Suitable for loading a list of classes from a properties file or configuration file
     * All lines starting with a '//' or a '#' are ignored, as are empty lines. 
     * 
     * @return
     */
    public static String [] loadClassList(String filename)
        throws Exception
    {
        String data = FileUtils.loadFile(filename);
        if(data == null)
        {
            throw new Exception("Unable to load file: " + filename);
        }
        LinkedList<String> tmpList = new LinkedList<String>();
        String [] lines = data.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            line = line.trim();
            if(line.length()>0 && !line.startsWith("//") && !line.startsWith("#"))
            {
                tmpList.add(line);
            }
        }
        if(tmpList.isEmpty())
        {
            throw new Exception("Unable to load any classes from file: " + filename);
        }
        return tmpList.toArray(new String[tmpList.size()]);
    }
    
    
    public static String matrixToCVSString(Object [][] data)
    {
    	StringBuffer b = new StringBuffer(1024);
    	
    	// lines
    	for (int i = 0; i < data.length; i++)
		{
    		// values
    		for (int j = 0; j < data[i].length; j++)
			{
    			b.append("\"");
    			b.append(data[i][j]);
    			b.append("\"");
    			
    			if(j != data[i].length-1)
    			{
    				b.append(",");
    			}
			}
    		
			if(i != data.length-1)
			{
				b.append("\n");
			}
		}
    	
    	return b.toString();
    }
    
    
    public static void writeCSV(Object [][] data, File file)
    	throws Exception
    {    	
    	writeToFile(matrixToCVSString(data), file);
    }
    
    
    /**
     * Should be able to handle all kinds of common CSV lines
     * @param aLine
     * @return
     */
    public static String [] parseCSVLine(String aLine)
    {
		ArrayList<String> elements = new ArrayList<String>();
		// split line into segments we can work with
		Matcher m = Pattern.compile("(?:^|,)(\"(?:[^\"]|\"\")*\"|[^,]*)").matcher(aLine);
		// process the segments
		while (m.find())
		{			
			// get the piece we are working on
			String data = m.group();			
			// remove leading comma if any
			data = data.replaceAll("^,", "");
			// remove outer quotations if any
			data = data.replaceAll("^?\"(.*)\"$", "$1");
			// replace double inner quotations if any
			data = data.replaceAll("\"\"", "\"");
			// store
			elements.add(data);
			
		}
		return (String[]) elements.toArray(new String[elements.size()]);
    }
    
    
    /**
     * Load a CSV file
     * @param filename
     * @return
     */
    public static double [][] loadCSV(String filename)
    	throws Exception
    {
        // load file in
        String raw = null;
        try
        {
            raw = FileUtils.loadFile(new File(filename));
        }
        catch(Exception e)
        {
            throw new Exception("Unable to load result file " + filename + " " + e.getMessage(), e);
        }
        
        // process each line
        String [] lines = raw.split("\n");
        LinkedList<double[]> results = new LinkedList<double[]>();
        for (int i = 0; i < lines.length; i++)
        {
            if(lines[i].startsWith("#") || lines[i].startsWith("//"))
            {
                continue;
            }
            else
            {
            	// split
            	String [] parts = lines[i].split(",");            	 
            	// parse
            	double [] d = new double[parts.length];
            	for (int j = 0; j < d.length; j++)
				{
					d[j] = Double.parseDouble(parts[j]);
				}
            	// store
            	results.add(d);
            }
        }
        
        return results.toArray(new double[results.size()][]);
    }
    
    /**
     * Whether or not the specified string denotes a properties file
     * Example: name.properties 
     * @param filename
     * @return - true if the filename is a valid properties filename
     */
    public static boolean isPropertiesFile(String filename)
    {
    	if(filename.endsWith(".properties"))
    	{
    		return true;
    	}
    	
    	return false;
    }
}
