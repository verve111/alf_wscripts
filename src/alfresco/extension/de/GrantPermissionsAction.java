package alfresco.extension.de;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.commons.io.IOUtils;


public class GrantPermissionsAction extends ActionExecuterAbstractBase {

	private NodeService nodeService;
	private PermissionService permissionService;
	private ContentService contentService;
	private SearchService searchService;
	private ActionService actionService;
	private FileFolderService fileFolderService;

	private Repository repository;
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
	public void setRepository(Repository repository) {
	    this.repository = repository;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	protected void executeImpl(Action arg0, NodeRef uploadedArchive) {
		NodeRef tmpDir = recreateTmpDir();
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("destination", tmpDir);
		Action action = actionService.createAction("import", map);
		actionService.executeAction(action, uploadedArchive);
		nodeService.deleteNode(uploadedArchive);
		NodeRef permissFile = nodeService.getChildByName(tmpDir, ContentModel.ASSOC_CONTAINS, "permissions.txt");
		ContentReader reader = contentService.getReader(permissFile, ContentModel.PROP_CONTENT);
		if (true) return;
		if (reader != null) {
			InputStream is = reader.getContentInputStream();
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(is, writer, "UTF-8");
				String content = writer.toString();
				for (String row : content.split("\n")) {
					String arr[] = row.trim().split("%=%");
					if (arr.length == 2) {
						NodeRef node = getNodeRefByPath(arr[0]);
						for (String permission : arr[1].split("%;%")) {
							String accessPermission[] = permission.split("%,%");
							if (accessPermission.length != 5) {
								throw new AlfrescoRuntimeException("5 tokens in permit should be. AccessPermission = " + permission);
							}
							// /*authority*/ GROUP_site_swsdp_SiteCollaborator; /*permission*/ SiteCollaborator; ALLOWED
							permissionService.setPermission(node, accessPermission[1], accessPermission[3], "ALLOWED".equals(accessPermission[0]));
						}
					} else {
						System.out.println("WARN: should be 2 tokens 'PATH=authority list' in a row (permissions file)");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	private NodeRef recreateTmpDir() {
		NodeRef res = null;
		// remove app:company_home/cm:tmp directory
		ResultSet resultSet = searchService.query(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"), SearchService.LANGUAGE_LUCENE,
				"PATH:\"/app:company_home/*\" AND TYPE:\"cm:folder\" AND @cm\\:name:\"tmp\"");
		for (ResultSetRow row : resultSet) {
			nodeService.deleteNode(row.getNodeRef());
			break;
		}
		resultSet.close();
		// create app:company_home/cm:tmp directory
		res = fileFolderService.create(repository.getCompanyHome(), "tmp", ContentModel.TYPE_FOLDER).getNodeRef();
		return res;
	}

	private NodeRef getNodeRefByPath(String path) {
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		String lucenePath = "PATH:\"/app:company_home/cm:tmp";
		boolean isFolder = path.endsWith("/");
		if (isFolder) {
			path = path.substring(0, path.length() - 1);
		}
		for (String token : path.split("/")) {
			lucenePath += "/cm:" + token;
		}
		lucenePath = lucenePath.replaceAll(" ", "_x0020_").replaceAll("~", "_x007E_").concat("\"");
		ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, lucenePath);
		NodeRef res = null;
		try {
			if (rs.length() == 0) {
				throw new AlfrescoRuntimeException("Could not find nodeRef for path: " + lucenePath);
			} else if (rs.length() > 1) {
				throw new AlfrescoRuntimeException("More than 1 nodeRef found for path: " + lucenePath);
			}
			res = rs.getNodeRef(0);
		} finally {
			rs.close();
		}
		return res;
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg) {
		// arg.add(new ParameterDefinitionImpl(_UPLOAD_NODE, DataTypeDefinition.NODE_REF, true, null));
	}
}
