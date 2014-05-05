package alfresco.extension.de;

import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class WorkflowListWebScript extends DeclarativeWebScript  {

    private NodeService nodeService;
    private SearchService searchService;
    
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
    protected Map<String, Object> executeImpl(WebScriptRequest request,
            Status status) {
		AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
        ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"/app:company_home\"/* TYPE:\"cm:content\"");
        System.out.println(resultSet.length());
        NodeRef companyHome = resultSet.getNodeRef(0);
        resultSet.close();
        if (companyHome != null) {
        	
        }
        System.out.println(companyHome);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        return result;
	}

}
