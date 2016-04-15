package com.mysterion.jht2.factory;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mysterion.jht2.monitor.SysoutProgressMonitor;

public class MonitorFactory {
	public static IProgressMonitor getMonitor() {
		return new SysoutProgressMonitor();
	}
}
