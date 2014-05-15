package alfresco.extension.de;

import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;


public class GrantPermissionsAction extends ActionExecuterAbstractBase {

	private NodeService nodeService;
	private PermissionService permissionService;

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action arg0, NodeRef arg1) {
		System.out.println("da");
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//
	}
}
