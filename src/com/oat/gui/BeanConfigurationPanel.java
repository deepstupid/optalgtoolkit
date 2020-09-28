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
package com.oat.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oat.AlgorithmRunException;
import com.oat.utils.BeanUtils;

/**
 * Type: BeanConfigurationPanel<br/>
 * Date: Dec 10, 2006<br/>
 * <br/>
 * Description: Provides generic facility for a configuration panel for a java bean
 * object. Provides support for primitive numbers and enums
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 06/07/2007   JBrownlee   Added support for strings
 * 24/07/2007   JBrownlee   Added support for disabled fields
 * </pre>
 */
public class BeanConfigurationPanel extends JPanel
{
    public final static int TEXT_FIELD_SIZE = 10;
    
    protected Object bean;
    protected String [] disabledList;
    protected String [] propertyList;
    protected Class<?> [] propertyTypes;
    protected JComponent [] fieldList;
    protected JLabel [] labelList;
    
    /**
     * Not a singleton, just a careful construction process,
     * Use this static method to get a JPanel instance for the provided java bean
     * @param b
     * @return
     */
    public static BeanConfigurationPanel createInstance(Object b, String [] disabledList)
    {
        BeanConfigurationPanel i = new BeanConfigurationPanel();
        i.initialise(b, disabledList);
        return i;
    }
    
    protected BeanConfigurationPanel()
    {}
    
    
    /**
     * Initialise the object by using reflection to determine bean properties
     * and then construct an suitable GUI 
     * 
     * @param b
     */
    protected void initialise(Object b, String [] aDisabledList)
    {
        // get a list of bean properties
        bean = b;
        disabledList = aDisabledList;
        propertyList = BeanUtils.getBeanPropertyList(bean);
        // order 
        Arrays.sort(propertyList);
        // prepare the label list
        prepareLabelList();
        // prepare the field list
        prepareFieldList();
        // prepare the GUI
        prepareGUI();
    }
    
    /**
     * Prepare the GUI which consists of labels and fields
     */
    protected void prepareGUI()
    {
        // Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(labelList.length, 1));
        for (int i = 0; i < labelList.length; i++)
        {
            labelPane.add(labelList[i]);
        }
        // Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(fieldList.length, 1));
        for (int i = 0; i < fieldList.length; i++)
        {
            fieldPane.add(fieldList[i]);
        }
        // Put the panels in another panel, labels on left, text fields on right.
        JPanel spacer = new JPanel();
        spacer.setLayout(new BorderLayout());
        spacer.add(labelPane, BorderLayout.CENTER);
        spacer.add(fieldPane, BorderLayout.EAST);
        spacer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(spacer);
    }
    
    
    /**
     * Prepare a list of fields based on the property types in the bean
     * - Numerical types use a JTextField
     * - Boolean types use a JCheckBox
     * - Enums use a JComboBox
     * 
     */
    protected void prepareFieldList()
    {
        fieldList = new JComponent[propertyList.length];
        propertyTypes = new Class[propertyList.length];
        for (int i = 0; i < fieldList.length; i++)
        {
            try
            {
                Method m = bean.getClass().getMethod("get"+propertyList[i]);
                propertyTypes[i] = m.getReturnType();
                if(propertyTypes[i] == Double.TYPE || propertyTypes[i] == Long.TYPE
                        || propertyTypes[i]==Integer.TYPE || propertyTypes[i]==Float.TYPE)
                {
                    fieldList[i] = new JTextField(""+m.invoke(bean), TEXT_FIELD_SIZE);
                }
                else if(propertyTypes[i] == String.class)
                {
                    fieldList[i] = new JTextField(""+m.invoke(bean), TEXT_FIELD_SIZE);
                }
                else if(propertyTypes[i] == Boolean.TYPE)
                {
                    fieldList[i] = new JCheckBox();
                    ((JCheckBox)fieldList[i]).setSelected(((Boolean)m.invoke(bean)).booleanValue());                    
                }
                else if(propertyTypes[i].isEnum())
                {
                    fieldList[i] = new JComboBox(propertyTypes[i].getEnumConstants());
                    ((JComboBox)fieldList[i]).setSelectedItem(m.invoke(bean));
                }
                else
                {
                    throw new AlgorithmRunException("Unable to prepare bean configuration panel, unknown data type: " + propertyTypes[i] + 
                            " for method: " + m.getName());
                }
                
                // check for disabled
                if(isDisabled(propertyList[i]))
                {
                    fieldList[i].setEnabled(false);
                }
            }
            catch (Exception e)
            {
                throw new AlgorithmRunException("There was a problem while preparing fields for bean.", e);
            }
        }
    }
    
    protected boolean isDisabled(String aFieldName)
    {
        if(disabledList==null||disabledList.length<1)
        {
            return false;
        }
        for (int i = 0; i < disabledList.length; i++)
        {
            if(aFieldName.equalsIgnoreCase(disabledList[i]))
            {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Process all data collection fields and set the corrisponding values in the java bean
     * 
     */
    public void populateBeanFromGUI()
    {
        for (int i = 0; i < fieldList.length; i++)
        {
            if(!fieldList[i].isEnabled())
            {
                continue;
            }            
            
            Method m = null;
            try
            {
                m = bean.getClass().getMethod("set"+propertyList[i], propertyTypes[i]);
                
                if(fieldList[i] instanceof JTextField)
                {
                    if(propertyTypes[i] == Double.TYPE)
                    {
                        m.invoke(bean, Double.parseDouble(((JTextField)fieldList[i]).getText()));
                    }
                    else if(propertyTypes[i] == Integer.TYPE)
                    {
                        m.invoke(bean, Integer.parseInt(((JTextField)fieldList[i]).getText()));
                    }
                    else if(propertyTypes[i] == Long.TYPE)
                    {
                        m.invoke(bean, Long.parseLong(((JTextField)fieldList[i]).getText()));
                    }
                    else if(propertyTypes[i] == Float.TYPE)
                    {
                        m.invoke(bean, Float.parseFloat(((JTextField)fieldList[i]).getText()));
                    }
                    else if(propertyTypes[i] == String.class)
                    {
                        m.invoke(bean, ((JTextField)fieldList[i]).getText());
                    }
                    else if(propertyTypes[i] == Boolean.TYPE)
                    {
                        m.invoke(bean, Boolean.parseBoolean(((JTextField)fieldList[i]).getText()));
                    }
                    else
                    {
                        throw new AlgorithmRunException("Unknown field type " + propertyTypes[i]);
                    }
                }
                else if(fieldList[i] instanceof JCheckBox)
                {
                    m.invoke(bean, ((JCheckBox)fieldList[i]).isSelected());
                }
                else if(fieldList[i] instanceof JComboBox)
                {
                    m.invoke(bean, ((JComboBox)fieldList[i]).getSelectedItem());
                }
                else
                {
                    throw new AlgorithmRunException("Unknown object type " + fieldList[i].getClass().getName());
                }
            }
            catch(NumberFormatException nfe)
            {
                throw new AlgorithmRunException("Invalid configuration "+m.getName()+" value " + ((JTextField)fieldList[i]).getText(), nfe);
            }
            catch (Exception e)
            {
                throw new AlgorithmRunException("There was a problem populating the bean from the GUI.\n" + m + "\n"+e.getMessage(), e);
            }
        }
    }
    
    /**
     * Prepare a list of labels with the bean property names
     */
    protected void prepareLabelList()
    {
        labelList = new JLabel[propertyList.length];
        for (int i = 0; i < propertyList.length; i++)
        {
            labelList[i]  = new JLabel(propertyList[i] + ":");
        }
    }
}
