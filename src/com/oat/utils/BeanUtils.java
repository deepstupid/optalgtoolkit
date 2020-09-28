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

import java.lang.reflect.Method;
import java.util.LinkedList;

import com.oat.AlgorithmRunException;
import com.oat.Populator;

/**
 * Type: BeanUtils<br/>
 * Date: 08/12/2006<br/>
 * <br/>
 * Description: Utilities for treating objects like java beans
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 21/08/2007	JBrownlee	Added a bean copy method
 * </pre>
 */
public class BeanUtils
{
    
    public final static char TOKEN = ':';
    
    /**
     * Format is: class=value:name=value:...
     * 
     * @param bean
     * @return
     */
    public static String beanToTokenisedString(Object bean)
    {
        StringBuffer b = new StringBuffer();
        b.append("class="+bean.getClass().getCanonicalName());
        String cvs = BeanUtils.getBeanDetails(bean);
        if(cvs!=null&&cvs.length()>0)
        {
        	// replace the '
            cvs = cvs.replace(',', BeanUtils.TOKEN);
            // get rid of the spaces
            cvs = cvs.replace(" ", "");
            b.append(BeanUtils.TOKEN);
            b.append(cvs);
        }
        return b.toString();
    }
    
    
        
    
    
    /**
     * create a bean from string, where string is in the format
     * class=value:name=value:...
     * where ':' is the token
     * 
     * @param s
     * @return
     */
    public static Object beanFromString(String s)
    {
    	try
    	{
            String [] parts = s.split(""+TOKEN);
            // class name
            String className = parts[0].substring(parts[0].indexOf('=')+1);
            Object bean = (Class.forName(className)).newInstance();
            
            int index = s.indexOf(TOKEN);
            if(index == -1)
            {
            	// nothing to populate
            	return bean;
            }
            // remove the class part
            s = s.substring(index+1);
            
            if(bean instanceof Populator)
            {
            	((Populator)bean).populateFromString(s);
            }
            else
            {
            	populateBeanFromString(s, bean);
            }
            
            return bean;            
    	}
    	catch(Exception e)
    	{
    		throw new RuntimeException("Unable to populate bean: " + e.getMessage() + " - " + s, e);
    	}    	
    }
    
    
    public static void populateBeanFromString(String s, Object bean)
    {
        try
        {
            String [] parts = s.split(""+TOKEN);
            // populate
            for (int i = 0; i < parts.length; i++)
            {
                // split property into component parts
                String [] nv = parts[i].split("=");
                if(nv.length!=2)
                {
                    throw new RuntimeException("Invalid name value pair: " + parts[i]);
                }
                // get method
                Method m = findMutatorForName(bean, nv[0]);
                if(m==null)
                {
                    throw new RuntimeException("Unable to locate mutator method with name " + nv[0] + " on class " + bean.getClass().getName());
                }
                Class [] params = m.getParameterTypes();                
                // must have a single parameter
                if(params==null || params.length!=1)
                {
                    throw new RuntimeException("Unable to locate suitable mutator method with name " + nv[0] + " on class " + bean.getClass().getName());
                }
                
                // populate
                m.invoke(bean, stringToSuitableParamter(params[0], nv[1]));
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to populate bean: " + e.getMessage() + " - " + s, e);
        }
               
    }
    
    
//    
//    
//    public static void readExternalBean(ObjectInput in, Object bean) 
//    	throws IOException, ClassNotFoundException
//	{
//    	// get a list of properties
//    	Method [] mutatorMethods = getBeanPropertyListMutatorMethod(bean);
//    	
//    	// sorted
//    	//Arrays.sort(mutatorMethods);
//    	
//    	for (int i = 0; i < mutatorMethods.length; i++)
//		{
//            Class [] params = mutatorMethods[i].getParameterTypes();                
//            // must have a single parameter
//            if(params==null || params.length!=1)
//            {
//                throw new RuntimeException("Unable to locate suitable mutator method with name " + mutatorMethods[i].getName() + " on class " + bean.getClass().getName());
//            }            
//            // read in the value
//            Class c = params[0];
//            Object value = null;
//            if(c == Long.TYPE)
//            {
//            	value = in.readLong();	
//            }
//            else if(c == Double.TYPE)
//            {
//            	value = in.readDouble();	
//            }
//            else if(c == Integer.TYPE)
//            {
//            	value = in.readInt();	
//            }    
//            else if(c == Float.TYPE)
//            {
//            	value = in.readFloat();	
//            } 
//            else if(c == Byte.TYPE)
//            {
//            	value = in.readByte();	
//            } 
//            else if(c == Boolean.TYPE)
//            {
//            	value = in.readBoolean();                
//            }
//            else if(c == String.class || c.isEnum())
//            {
//            	value = in.readObject();
//            }
//            else
//            {
//                throw new AlgorithmRunException("Unable to internalize property, unknown data type: " + c.getName() + " for method: " + mutatorMethods[i].getName());
//            }
//            
//            
//            // populate the bean
//            try
//			{
//				mutatorMethods[i].invoke(bean, value);
//			}
//			catch (Exception e)
//			{
//				throw new IOException("Error setting value on bean: " + e.getMessage(), e);
//			}
//		}
//	}
//    
//    public static void writeExternalBean(ObjectOutput out, Object bean) 
//    	throws IOException
//    {
//    	// get a list of properties
//    	Method [] accessorMethods = getBeanPropertyListAccessorMethod(bean);
//    	// sorted    	
//    	//Arrays.sort(accessorMethods);
//    	
//    	// populate the copy with all values set in the bean
//    	for (int i = 0; i < accessorMethods.length; i++)
//		{
//    		// get the thing
//    		Object value = null;
//            try
//            {
//            	value = accessorMethods[i].invoke(bean, (Object[])null);
//            }
//            catch (Exception e)
//            {
//                throw new AlgorithmRunException("Unable to get field: " + accessorMethods[i].getName() + ": "+ e.getMessage(), e);
//            }    		
//
//            // externalize appropriately
//            Class c = value.getClass();
//            if(c == Long.TYPE || c == Long.class)
//            {
//            	out.writeLong((Long)value);	
//            }
//            else if(c == Double.TYPE || c == Double.class)
//            {
//            	out.writeDouble((Double)value);	
//            }
//            else if(c == Integer.TYPE || c == Integer.class)
//            {
//            	out.writeInt((Integer)value);	
//            }    
//            else if(c == Float.TYPE || c == Float.class)
//            {
//            	out.writeFloat((Float)value);	
//            } 
//            else if(c == Byte.TYPE || c == Byte.class)
//            {
//            	out.writeByte((Byte)value);	
//            } 
//            else if(c == String.class)
//            {
//            	out.writeObject(value);
//            }
//            else if(c == Boolean.TYPE || c == Boolean.class)
//            {
//            	out.writeBoolean((Boolean)value);                
//            }
//            else if(c.isEnum())
//            {
//            	out.writeObject(value);
//            }
//            else
//            {
//                throw new AlgorithmRunException("Unable to externalize property, unknown data type: " + c.getName() + " for method: " + accessorMethods[i].getName());
//            }
//		}
//    }
    
    
    
    public static Object [] stringToSuitableParamter(Class c, String s)
    {
        // string
        if(c == String.class)
        {
            return new Object[]{s};
        }        
        // primitives
        else if(c == Double.TYPE)
        {
            return new Object[]{Double.parseDouble(s)};
        }
        else if(c == Float.TYPE)
        {
            return new Object[]{Float.parseFloat(s)};
        }
        else if(c == Integer.TYPE)
        {
            return new Object[]{Integer.parseInt(s)};
        }
        else if(c == Long.TYPE)
        {
            return new Object[]{Long.parseLong(s)};
        }
        else if(c == Short.TYPE)
        {
            return new Object[]{Short.parseShort(s)};
        }
        else if(c == Byte.TYPE)
        {
            return new Object[]{Byte.parseByte(s)};
        }
        // boolean
        else if(c == Boolean.TYPE)
        {
            return new Object[]{Boolean.parseBoolean(s)};
        }
        // enum
        else if(c.isEnum())
        {
            return new Object[]{ Enum.valueOf(c, s)};
        }
        
        throw new RuntimeException("Unknown type: " + c.getName());
    }
    
    
    public static void beanSetSeed(Object bean, long seed)
    {
        Method m = findMutatorForName(bean, "seed");
        if(m == null)
        {
            throw new AlgorithmRunException("Bean does not have a setSeed() method: " + bean.getClass().getCanonicalName());
        }
        
        try
        {
            m.invoke(bean, new Object[]{new Long(seed)});
        }
        catch (Exception e)
        {
            throw new AlgorithmRunException("Unable to set the random number seed: " + e.getMessage(), e);
        }
    }
    
    
    public static Method findMutatorForName(Object bean, String name)
    {
        Method [] methods = bean.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if(methods[i].getName().equalsIgnoreCase("set"+name))
            {
                return methods[i];
            }
        }
        return null;
    }
    
    public static Method findAccessorForName(Object bean, String name)
    {
        Method [] methods = bean.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if(methods[i].getName().equalsIgnoreCase("get"+name))
            {
                return methods[i];
            }
        }
        return null;
    }
    
    
    /**
     * Return a CSV string of all properties of the provided bean. That is all accessors getXXX()
     * that have mirroring setXXX() function
     * 
     * Dose not support the .is accessor
     * 
     * @param bean
     * @return - CSV string of app properties
     */
    public static String getBeanDetails(Object bean)
    {
        LinkedList<String> l = new LinkedList<String>();
        Method [] methods = bean.getClass().getMethods();
        
        // search all methods for mutator methods
        for (int i = 0; i < methods.length; i++)
        {
            String n = methods[i].getName();
            if(n.startsWith("set"))
            {
                String end = n.substring(3);
                // if a mutator was found, seek the accessor so we can pull out the property
                for (int j = 0; j < methods.length; j++)
                {
                    String na = methods[j].getName();
                    if(na.equals("get"+end))
                    {
                        try
                        {
                            l.add(end+"="+methods[j].invoke(bean, (Object[])null));
                            break; // stop searching
                        }
                        catch (Exception e)
                        {
                            throw new AlgorithmRunException("Unable to prepare bean property " + methods[j], e);
                        }
                    }
                }
            }
        }
        
        // convert to a string
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < l.size(); i++)
        {
            b.append(l.get(i));
            if(i!=l.size()-1)
            {
                b.append(", ");
            }
        }
        return b.toString();
    }
    
    
    /**
     * Using the properties list, duplicates the provided bean and sets all known properties
     * @param <T>
     * @param bean
     * @return
     */
    public static <T extends Object> T beanCopy(T bean)
    {
    	T copy = null;
    	
    	try
		{
			copy = (T) bean.getClass().newInstance();
		} 
    	catch (Exception e)
		{
    		throw new RuntimeException("Error creating a copy of the bean: " + e.getMessage(), e);
		}
    	
    	if(bean instanceof Populator)
    	{
    		((Populator)copy).populateFromInstance(bean);
    	}
    	else
    	{
        	// populate
        	beanPopulate(bean, copy);
    	}    	

    	// return
    	return copy;
    }
    
    public static <T extends Object> void beanPopulate(T from, T to)
    {
    	// get a list of properties
    	String [] properties = getBeanPropertyList(from);
    	// populate the copy with all values set in the bean
    	for (int i = 0; i < properties.length; i++)
		{
    		// get the thing
    		Method a = findAccessorForName(from, properties[i]);   
    		Object value = null;
            try
            {
            	value = a.invoke(from, (Object[])null);
            }
            catch (Exception e)
            {
                throw new AlgorithmRunException("Unable to get field: " + properties[i] + ": "+ e.getMessage(), e);
            }    		
    		// set the thing
    		Method m = findMutatorForName(to, properties[i]);
            try
            {
                m.invoke(to, new Object[]{value});
            }
            catch (Exception e)
            {
                throw new AlgorithmRunException("Unable to set field: " + properties[i] + ": "+ e.getMessage(), e);
            }
		}
    }
    
    
    
    /**
     * Returns a list of property names that have accessible accessor and mutator methods
     * @param bean
     * @return
     */
    public static String [] getBeanPropertyList(Object bean)
    {
        Method [] methods = bean.getClass().getMethods();
        LinkedList<String> l = new LinkedList<String>();
        
        // search all methods for mutator methods
        for (int i = 0; i < methods.length; i++)
        {
            String n = methods[i].getName();
            if(n.startsWith("set"))
            {
                String end = n.substring(3);
                // if a mutator was found, seek the accessor so we can pull out the property
                for (int j = 0; j < methods.length; j++)
                {
                    String na = methods[j].getName();
                    if(na.equals("get"+end))
                    {
                        l.add(end);
                        break;
                    }
                }
            }
        }
        
        return l.toArray(new String[l.size()]);
    }
    
    
    public static Method [] getBeanPropertyListAccessorMethod(Object bean)
    {
        Method [] methods = bean.getClass().getMethods();
        LinkedList<Method> l = new LinkedList<Method>();
        
        // search all methods for mutator methods
        for (int i = 0; i < methods.length; i++)
        {
            String n = methods[i].getName();
            if(n.startsWith("set"))
            {
                String end = n.substring(3);
                // if a mutator was found, seek the accessor so we can pull out the property
                for (int j = 0; j < methods.length; j++)
                {
                    String na = methods[j].getName();
                    if(na.equals("get"+end))
                    {
                        l.add(methods[j]); // accessor
                        break;
                    }
                }
            }
        }
        
        return l.toArray(new Method[l.size()]);
    }
    
    public static Method [] getBeanPropertyListMutatorMethod(Object bean)
    {
        Method [] methods = bean.getClass().getMethods();
        LinkedList<Method> l = new LinkedList<Method>();
        
        // search all methods for mutator methods
        for (int i = 0; i < methods.length; i++)
        {
            String n = methods[i].getName();
            if(n.startsWith("set"))
            {
                String end = n.substring(3);
                // if a mutator was found, seek the accessor so we can pull out the property
                for (int j = 0; j < methods.length; j++)
                {
                    String na = methods[j].getName();
                    if(na.equals("get"+end))
                    {
                        l.add(methods[i]); // mutator
                        break;
                    }
                }
            }
        }
        
        return l.toArray(new Method[l.size()]);
    }
}
