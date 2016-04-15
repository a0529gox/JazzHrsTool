package com.mysterion.jht2.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.IContributorManager;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.client.IDetailedStatus;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.IWorkItemWorkingCopyManager;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.expression.AttributeExpression;
import com.ibm.team.workitem.common.expression.Expression;
import com.ibm.team.workitem.common.expression.IQueryableAttribute;
import com.ibm.team.workitem.common.expression.QueryableAttributes;
import com.ibm.team.workitem.common.expression.Term;
import com.ibm.team.workitem.common.expression.Term.Operator;
import com.ibm.team.workitem.common.internal.model.WorkItemAttributes;
import com.ibm.team.workitem.common.model.AttributeOperation;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.Identifier;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResolvedResult;
import com.ibm.team.workitem.common.workflow.IWorkflowAction;
import com.ibm.team.workitem.common.workflow.IWorkflowInfo;
import com.mysterion.jht2.log.AnnoyLogger;
import com.mysterion.jht2.util.URIUtil;

public class Repository {
	private static Repository self = null;
	private final String jazzUrl;
	private final String partialName;
//	private final String partialOwnerName;
	private final String username;
	private final String password;
	
	private IProgressMonitor monitor = null;
	private ITeamRepository itRepo = null;
//	private IWorkspaceManager workspaceManager = null;
//	private IItemManager itemManager = null;
	private IWorkItemClient workItemClient = null;
//	private ILinkManager linkManager = null;
	private IProcessClientService processClient = null;
	private IAuditableClient auditableClient = null;
	private IProjectArea projectArea = null;
	private IQueryClient queryClient = null;
	
	
	
	private Repository(String jazzUrl, String partialName, String partialOwnerName, String username, String password) {
		this.jazzUrl = jazzUrl;
		this.partialName = partialName;
//		this.partialOwnerName = partialOwnerName;
		this.username = username;
		this.password = password;
	}
	
	public static Repository getRepository(String jazzUrl, String partialName, String partialOwnerName, String username, String password) {
		if (self == null) {
			synchronized(Repository.class){
                if(self == null) {
                	self = new Repository(jazzUrl, partialName, partialOwnerName, username, password);

                }
            }
		}
		return self;
	}
	
	public void login() throws TeamRepositoryException {
		itRepo = TeamPlatform.getTeamRepositoryService().getTeamRepository(jazzUrl);
		itRepo.registerLoginHandler(new ILoginHandler2() {
			@Override
			public ILoginInfo2 challenge(ITeamRepository arg0) {
				return new UsernameAndPasswordLoginInfo(username, password);
			}
		});
		itRepo.login(monitor);
		
		initParamsAfterLogin();
	}
	
	public IWorkItem getWorkItem(int workItemId) {
		IWorkItem workItem = null;
		try {
			workItem = workItemClient.findWorkItemById(workItemId, IWorkItem.FULL_PROFILE, monitor);
		} catch (Exception e) {
			AnnoyLogger.severe(e);
		}
		return workItem;
	}
	
	public IAttribute getAttribute(String attr) {
		IAttribute attribute = null;
		try {
			attribute = workItemClient.findAttribute(projectArea, attr, monitor);
		} catch (TeamRepositoryException e) {
			AnnoyLogger.severe(e);
		}
		return attribute;
	}
	
	public List<IWorkItem> getWorkingWorkItems() {
		List<IWorkItem> result = new ArrayList<IWorkItem>(); 
		try {
			Term term = new Term(Operator.AND);

			IQueryableAttribute attrProjArea = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(projectArea, IWorkItem.PROJECT_AREA_PROPERTY, auditableClient, null);
			Expression expProjArea = new AttributeExpression(attrProjArea, AttributeOperation.EQUALS, projectArea);
			term.add(expProjArea);

			IContributorManager contributorManager = getiTeamRepository().contributorManager();
			IContributor contributor = contributorManager.fetchContributorByUserId(username, null);
			
			IQueryableAttribute attrOnwer = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(projectArea, IWorkItem.OWNER_PROPERTY, auditableClient, null);
			Expression expOnwer = new AttributeExpression(attrOnwer, AttributeOperation.EQUALS, contributor);
			term.add(expOnwer);

			Term attrTerm = new Term(Operator.OR);
			IQueryableAttribute attrState = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(projectArea, IWorkItem.STATE_PROPERTY, auditableClient, null);
			Expression expState = new AttributeExpression(attrState, AttributeOperation.EQUALS, 1);
			Expression expState2 = new AttributeExpression(attrState, AttributeOperation.EQUALS, 2);
			Expression expState3 = new AttributeExpression(attrState, AttributeOperation.EQUALS, 6);
			attrTerm.add(expState);
			attrTerm.add(expState2);
			attrTerm.add(expState3);
			term.add(attrTerm);
			
			IQueryResult<IResolvedResult<IWorkItem>> queryResults = queryClient.getResolvedExpressionResults(projectArea, term, IWorkItem.FULL_PROFILE);
			
			while (queryResults.hasNext(null)) {
				result.add(queryResults.next(null).getItem());
			}
		} catch (TeamRepositoryException e) {
			AnnoyLogger.severe(e);
		}
		
		return result;
	}
	
	public void addHrs2WorkItem(Integer workItemId, double hrs) {
		addHrs2WorkItem(workItemId, hrs, true, false);
	}
	
	public void addHrs2WorkItem(Integer workItemId, double hrs, boolean forced, boolean close) {
		IWorkItem wi = getWorkItem(workItemId);
		
		IWorkItemWorkingCopyManager wcm = getWorkItemClient().getWorkItemWorkingCopyManager();
		try {
			wcm.connect(wi, IWorkItem.FULL_PROFILE, null);
		
			WorkItemWorkingCopy wc = wcm.getWorkingCopy(wi);
			IAttribute timeSpentAttr = getAttribute(WorkItemAttributes.getAttributeId(WorkItemAttributes.TIME_SPENT));
			Long timeSpent = (Long) wi.getValue(timeSpentAttr);
			if (timeSpent < 0) {
				timeSpent = 0L;
			}
			Long newTimeSpent = timeSpent + (int)(hrs * 60 * 60 * 1000);
			
			wc.getWorkItem().setValue(timeSpentAttr, newTimeSpent);
			
			
			if (forced || close) {
				IWorkflowInfo info = getWorkItemClient().findWorkflowInfo(wi, null);
				for (Identifier<IWorkflowAction> action : info.getActionIds(wi.getState2())) {
					String actionName = info.getActionName(action);
					
					if (close && "解決".equals(actionName)) {
						wc.setWorkflowAction(action.getStringIdentifier());
						break;
					}
					if (forced && "開始工作".equals(actionName)) {
						wc.setWorkflowAction(action.getStringIdentifier());
					}
				}
			}
			
			IDetailedStatus ds = wc.save(null);
			if (!ds.isOK()) {

			} else {
				
			}
			
		} catch(TeamRepositoryException e) {
			AnnoyLogger.severe(e);
		} finally {
			wcm.disconnect(wi);
		}
	}
	
	private void initParamsAfterLogin() throws TeamRepositoryException {
//		workspaceManager = (IWorkspaceManager)itRepo.getClientLibrary(IWorkspaceManager.class);
//		itemManager = itRepo.itemManager();
		workItemClient = (IWorkItemClient)itRepo.getClientLibrary(IWorkItemClient.class);
//		linkManager = (ILinkManager) itRepo.getClientLibrary(ILinkManager.class);
		processClient = (IProcessClientService) itRepo.getClientLibrary(IProcessClientService.class);
		auditableClient = (IAuditableClient) itRepo.getClientLibrary(IAuditableClient.class);
		projectArea = (IProjectArea) processClient.findProcessArea(URIUtil.get(partialName), null, null);
		queryClient = (IQueryClient) itRepo.getClientLibrary(IQueryClient.class);
	}
	
	public boolean isLogin() {
		return itRepo != null;
	}

	public IWorkItemClient getWorkItemClient() {
		return workItemClient;
	}

	public ITeamRepository getiTeamRepository() {
		return itRepo;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
