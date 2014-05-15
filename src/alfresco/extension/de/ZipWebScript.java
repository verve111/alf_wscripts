package alfresco.extension.de;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class ZipWebScript extends AbstractWebScript {

	private NodeService nodeService;
	private SearchService searchService;
	private PermissionService permissionService;
	private DictionaryService dictionaryService;
	private ContentService contentService;
	
	private List<String> permissionsList = null;

	private StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");

	private static final int BUFFER_SIZE = 1024;

	private static final String MIMETYPE_ZIP = "application/zip";
	private static final String TEMP_FILE_PREFIX = "alf";
	private static final String ZIP_EXTENSION = ".zip";

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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

	public ZipWebScript() {
		super();
	}

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();
		permissionsList = new ArrayList<String>();
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "PATH:\"/app:company_home/*\" AND TYPE:\"cm:folder\"");
		List<NodeRef> list = new ArrayList<NodeRef>();
		for (ResultSetRow row : resultSet) {
			list.add(row.getNodeRef());
		}
		resultSet.close();
		String filename = "res2";
		try {
			res.setContentType(MIMETYPE_ZIP);
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.addHeader("Content-Disposition", "attachment;filename=\"" + filename + ZIP_EXTENSION + "\"");
			res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			res.setHeader("Pragma", "public");
			res.setHeader("Expires", "0");
			createZipFile(list, res.getOutputStream());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public void createZipFile(List<NodeRef> nodeRefs, OutputStream os) throws IOException {
		File zip = null;
		try {
			if (nodeRefs.size() > 0) {
				zip = TempFileProvider.createTempFile(TEMP_FILE_PREFIX, ZIP_EXTENSION);
				FileOutputStream stream = new FileOutputStream(zip);
				BufferedOutputStream buff = new BufferedOutputStream(stream);
				ZipArchiveOutputStream out = new ZipArchiveOutputStream(buff);
				// out.setEncoding(encoding);
				out.setMethod(ZipArchiveOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_SPEED);
				try {
					for (NodeRef nr : nodeRefs) {
						addToZip(nr, out, "");
					}
					createPermissionsFile(out);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					out.close();
					buff.close();
					stream.close();
					if (nodeRefs.size() > 0) {
						InputStream in = new FileInputStream(zip);
						try {
							byte[] buffer = new byte[BUFFER_SIZE];
							int len;
							while ((len = in.read(buffer)) > 0) {
								os.write(buffer, 0, len);
							}
						} finally {
							in.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zip != null) {
				zip.delete();
			}
		}
	}
	
	private void storePermission(String path, NodeRef node) {
		String s = "";
		for (AccessPermission ap : permissionService.getAllSetPermissions(node)) {
			s += ap.getAccessStatus() + "," + ap.getAuthority()
					+ "," + ap.getAuthorityType() + "," + ap.getPermission()
					+ "," + ap.getPosition() + ";";
		}
		if (s.isEmpty()) {
			System.out.println("WARN: no permits found for node : " + node);
		}
		s = path + "=" + s;
		permissionsList.add(s.endsWith(";") ? s.substring(0, s.length() - 1) : s);		
	}
	
	private void createPermissionsFile(ZipArchiveOutputStream out) throws IOException {
		ZipArchiveEntry entry = new ZipArchiveEntry("permissions.txt");
		entry.setTime((new Date()).getTime());
		// entry.setSize(reader.getSize());
		out.putArchiveEntry(entry);
		for (String perm : permissionsList) {
			out.write(perm.concat(System.getProperty("line.separator")).getBytes());
		}
		out.closeArchiveEntry();
	}

	public void addToZip(NodeRef node, ZipArchiveOutputStream out, String path) throws IOException {
		QName nodeQnameType = this.nodeService.getType(node);
		String nodeName = (String) nodeService.getProperty(node, ContentModel.PROP_NAME);
		if (dictionaryService.isSubClass(nodeQnameType, ContentModel.TYPE_CONTENT)) {
			ContentReader reader = contentService.getReader(node, ContentModel.PROP_CONTENT);
			if (reader != null) {
				InputStream is = reader.getContentInputStream();
				String filename = path.isEmpty() ? nodeName : path + '/' + nodeName;
				storePermission(filename, node);
				ZipArchiveEntry entry = new ZipArchiveEntry(filename);
				entry.setTime(((Date) nodeService.getProperty(node, ContentModel.PROP_MODIFIED)).getTime());
				entry.setSize(reader.getSize());
				out.putArchiveEntry(entry);
				byte buffer[] = new byte[BUFFER_SIZE];
				while (true) {
					int nRead = is.read(buffer, 0, buffer.length);
					if (nRead <= 0) {
						break;
					}
					out.write(buffer, 0, nRead);
				}
				is.close();
				out.closeArchiveEntry();
			} else {
				// ignore datalists: issue, todoList
				// System.out.println("Unmanaged type, node = " + node + ", type = " +
				// nodeService.getType(node));
			}
		} else if (this.dictionaryService.isSubClass(nodeQnameType, ContentModel.TYPE_FOLDER)
				&& !this.dictionaryService.isSubClass(nodeQnameType, ContentModel.TYPE_SYSTEM_FOLDER)) {
			List<ChildAssociationRef> children = nodeService.getChildAssocs(node);
			if (children.isEmpty()) {
				String folderPath = path.isEmpty() ? nodeName + '/' : path + '/' + nodeName + '/';
				storePermission(folderPath, node);
				out.putArchiveEntry(new ZipArchiveEntry(folderPath));
				out.closeArchiveEntry();
			} else {
				for (ChildAssociationRef childAssoc : children) {
					NodeRef childNodeRef = childAssoc.getChildRef();
					addToZip(childNodeRef, out, path.isEmpty() ? nodeName : path + '/' + nodeName);
				}
			}
		} else {
			// ignore system folders, actions
			// System.out.println("Unmanaged type, node = " + node + ", type = " +
			// nodeService.getType(node));
		}
	}

}
