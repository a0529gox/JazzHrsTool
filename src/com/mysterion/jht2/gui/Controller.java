package com.mysterion.jht2.gui;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.ibm.team.repository.client.TeamPlatform;
import com.mysterion.jht2.factory.RepositoryFactory;
import com.mysterion.jht2.log.AnnoyLogger;
import com.mysterion.jht2.repository.Repository;
import com.mysterion.jht2.util.RandomTimeUtil;
import com.mysterion.jht2.vo.WorkItemBean;

import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	Stage stage;
	
	@FXML
	Label lbMsg;
	@FXML
	Button btnRl;
	@FXML
	Button btnRd;
	@FXML
	Button btnSave;
	@FXML
	TableView<WorkItemBean> tvWorkItems;
	@FXML
	TableColumn<WorkItemBean, CheckBox> tcCb;
	@FXML
	TableColumn<WorkItemBean, String> tcID;
	@FXML
	TableColumn<WorkItemBean, String> tcSummary;
	@FXML
	TableColumn<WorkItemBean, String> tcEstimate;
	@FXML
	TableColumn<WorkItemBean, String> tcReal;
	@FXML
	TableColumn<WorkItemBean, TextField> tcHrs;
	@FXML
	TableColumn<WorkItemBean, CheckBox> tcClose;

	public Controller() {
		ControllerHelper.instance().bind(this);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initMainTab(location, resources);
	}
	
	protected void initMainTab(URL location, ResourceBundle resources) {
		tcCb.setCellValueFactory(new PropertyValueFactory<WorkItemBean, CheckBox>("selected"));
		tcID.setCellValueFactory(new PropertyValueFactory<WorkItemBean, String>("id"));
		tcSummary.setCellValueFactory(new PropertyValueFactory<WorkItemBean, String>("summary"));
		tcEstimate.setCellValueFactory(new PropertyValueFactory<WorkItemBean, String>("estimateHrs"));
		tcReal.setCellValueFactory(new PropertyValueFactory<WorkItemBean, String>("realHrs"));
		tcHrs.setCellValueFactory(new PropertyValueFactory<WorkItemBean, TextField>("hrs"));
		tcClose.setCellValueFactory(new PropertyValueFactory<WorkItemBean, CheckBox>("close"));
		
		btnRl.setOnAction((e) -> ControllerHelper.instance().loadWorkItems());
		btnRd.setOnAction((e) -> btnRdActionEvent(e));
		btnSave.setOnAction(e -> btnSaveActionEvent(e));
		

		ControllerHelper.instance().loadWorkItems();
	}
	
	protected void btnRdActionEvent(ActionEvent e) {
		FilteredList<WorkItemBean> list = tvWorkItems
									.getItems()
									.filtered(wi -> wi.getSelected().isSelected());
		
		FilteredList<WorkItemBean> needRandomList = list.filtered(wi -> wi.getHrsValue() <= 0D);

		
		double totalHrs = RandomTimeUtil.getTotalHrs();
		for (WorkItemBean wi : list.filtered(wi -> !needRandomList.contains(wi))) {
			totalHrs -= wi.getHrsValue();
		}
		
		double[] hrs = RandomTimeUtil.getRandomHrs(totalHrs, needRandomList.size());
		for (int i = 0; i < needRandomList.size(); i++) {
			needRandomList.get(i).setHrsValue(hrs[i]);
		}
	}
	
	protected void btnSaveActionEvent(ActionEvent e) {
		if (!validateMainTab()) {
			return;
		}
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are you sure?");
		alert.setHeaderText("確定要填入工作時數了？");
		Optional<ButtonType> cfm = alert.showAndWait();
		
		if (cfm.isPresent() && cfm.get() == ButtonType.OK) {
			
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					boolean success = false;
					try {
						btnSave.setDisable(true);
						updateMessage("啟動中…");
						TeamPlatform.startup();
						
						updateMessage("連接Jazz平台中…");
						Repository repo = RepositoryFactory.getRepository();

						updateMessage("儲存中…");
						tvWorkItems.getItems()
							.filtered(wi -> wi.getSelected().isSelected())
							.forEach(wi -> {
								repo.addHrs2WorkItem(
										wi.getId(),
										wi.getHrsValue(), 
										true, 
										wi.getClose().isSelected());
							});
						success = true;
						
					} catch (Exception e) {
						updateMessage(e.toString());
						AnnoyLogger.severe(e);
					} finally {
						btnSave.setDisable(false);
						updateMessage("");
						TeamPlatform.shutdown();
						
						//儲存完成，refresh頁面
						if (success) {
							ControllerHelper.instance().loadWorkItems();
						}
					}
					return null;
				}
			};
			
			lbMsg.textProperty().bind(task.messageProperty());
			
			Thread t = new Thread(task);
			t.setDaemon(true);
			t.start();
			
		}
	}
	
	protected boolean validateMainTab() {
		boolean isValid = true;
		
		String msg = new String();
		double totalHrs = 0d;

		boolean isAnyWISelected = false;
		boolean isHrsValid = true;
		for (WorkItemBean bean : tvWorkItems.getItems()) {
			if (!bean.getSelected().isSelected()) {
				continue;
			} else {
				isAnyWISelected = true;
			}
			
			Double hrs = bean.getHrsValue();
			if (hrs <= 0D) {
				isHrsValid = false;
				break;
			}
		}
		if (!isAnyWISelected) {
			msg += "請勾選至少一個Work Item。" + System.getProperty("line.separator");
		}
		if (!isHrsValid) {
			msg += "時數請輸入大於0的數字。" + System.getProperty("line.separator");
		}
		if (totalHrs > 8d) {
			msg += "總時數不可大於8。" + System.getProperty("line.separator");
		}
		

		isValid = msg.length() == 0;
		if (!isValid) {
			Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
			alert.setTitle("Are you stupid user?");
			alert.setHeaderText("輸入的資料有誤");
			alert.show();
		}
		
		return isValid;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
