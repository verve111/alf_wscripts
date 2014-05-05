package alfresco.extension.de;

import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class WorkflowListWebScript extends DeclarativeWebScript {

	private NodeService nodeService;
	private SearchService searchService;
	private PermissionService permissionService;

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public WorkflowListWebScript() {
		super();
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest request, Status status) {
		AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, 
				"PATH:\"/app:company_home//*\" AND TYPE:\"cm:content\" AND @cm\\:name:\"Att1\"");

		// folders 82
		// content 187
		// "PATH:\"/app:company_home//*\" AND TYPE:\"cm:content\" AND @cm\\:name:\"Att1\""
		System.out.println("length: " + resultSet.length());
		if (resultSet.length() < 20) {
			for (ResultSetRow row : resultSet) {
				//System.out.println(row.getNodeRef());
			}
		}
		if (resultSet.length() > 0) {
			NodeRef nr = resultSet.getNodeRef(0);
			System.out.println("currnode: " + nr);
			permissionService.getAllSetPermissions(nr);
		}
		resultSet.close();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		return result;
	}

}
