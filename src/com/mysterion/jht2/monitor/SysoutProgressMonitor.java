/*******************************************************************************
 * Licensed Materials - Property of IBM
 * SysoutProgressMonitor.java
 * (c) Copyright IBM Corporation 2012. 
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.mysterion.jht2.monitor;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Progress monitor that reports progress to standard out.
 */
public class SysoutProgressMonitor implements IProgressMonitor {

    public void beginTask(String name, int totalWork) {
        print(name);
    }

    public void done() {
    }

    public void internalWorked(double work) {
    }

    public boolean isCanceled() {
        return false;
    }

    public void setCanceled(boolean value) {
    }

    public void setTaskName(String name) {
        print(name);
    }

    public void subTask(String name) {
        print(name);
    }

    public void worked(int work) {
    }
    
    private void print(String name) {
        if(name != null && ! "".equals(name))
            System.out.println(name);
    }
}
