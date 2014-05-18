package alfresco.extension.de;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.commons.io.IOUtils;


public class GrantPermissionsAction extends ActionExecuterAbstractBase {

	private NodeService nodeService;
	private PermissionService permissionService;
	private ContentService contentService;

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected void executeImpl(Action arg0, NodeRef tmpDir) {
		NodeRef permissFile = nodeService.getChildByName(tmpDir, ContentModel.ASSOC_CONTAINS, "permissions.txt");
		ContentReader reader = contentService.getReader(permissFile, ContentModel.PROP_CONTENT);
		if (reader != null) {
			InputStream is = reader.getContentInputStream();
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(is, writer, "UTF-8");
				String content = writer.toString();
				for (String row : content.split("\n")) {
					String arr[] = row.trim().split("%=%");
					if (arr.length == 2) {
						
					} else {
						System.out.println("WARN: should be 2 tokens in a row (permissions file)");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//
	}
}
