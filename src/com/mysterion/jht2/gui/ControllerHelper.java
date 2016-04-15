package com.mysterion.jht2.gui;

import java.util.List;

import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.internal.model.WorkItemAttributes;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.mysterion.jht2.factory.RepositoryFactory;
import com.mysterion.jht2.log.AnnoyLogger;
import com.mysterion.jht2.repository.Repository;
import com.mysterion.jht2.vo.WorkItemBean;

import javafx.concurrent.Task;

public class ControllerHelper {
	
	private static ControllerHelper self;
	private Controller ctrl;

	public ControllerHelper() {
		// TODO Auto-generated constructor stub
	}

	public static ControllerHelper instance() {
		if (self == null) {
			self = new ControllerHelper();
		}
		return self;
	}
	
	public void bind(Controller ctrl) {
		this.ctrl = ctrl;
	}
	
	protected void loadWorkItems() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					ctrl.btnRl.setDisable(true);
					updateMessage("啟動中…");
					TeamPlatform.startup();

					updateMessage("連接Jazz平台中…");
					Repository repo = RepositoryFactory.getRepository();

					ctrl.tvWorkItems.getItems().removeAll(ctrl.tvWorkItems.getItems());

					updateMessage("取得Work Item中…");
					List<IWorkItem> list = repo.getWorkingWorkItems();
					
					IAttribute attrTimeSpent = repo.getAttribute(WorkItemAttributes.getAttributeId(WorkItemAttributes.TIME_SPENT));
					
					for (IWorkItem result : list) {
						WorkItemBean bean = new WorkItemBean();

						bean.setId(result.getId());
						bean.setSummary(result.getHTMLSummary().getPlainText());
						Object objTimeSpent = result.getValue(attrTimeSpent);
						Double dTimeSpent = objTimeSpent == null ? 0 : Double.valueOf(objTimeSpent.toString()) / ( 60 * 60 * 1000);
						bean.setRealHrs(dTimeSpent <= 0d ? 0d : dTimeSpent);
						Double dEstimateHrs = Double.valueOf(result.getDuration()) / ( 60 * 60 * 1000);
						bean.setEstimateHrs(dEstimateHrs <= 0d ? 0d : dEstimateHrs);
						
						ctrl.tvWorkItems.getItems().add(bean);
					}
					
				} catch (NumberFormatException e) {
					updateMessage(e.toString());
					AnnoyLogger.severe(e);
				} catch (TeamRepositoryException e) {
					updateMessage(e.toString());
					AnnoyLogger.severe(e);
				} catch (Exception e) {
					AnnoyLogger.severe(e);
				} catch (Throwable e) {
					AnnoyLogger.severe(e);
				} finally {
					ctrl.btnRl.setDisable(false);
					updateMessage("");
					TeamPlatform.shutdown();
				}
				return null;
			}
		};
		
		ctrl.lbMsg.textProperty().bind(task.messageProperty());
		
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
}
