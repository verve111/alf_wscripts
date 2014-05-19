var filename = null;
var content = null;
var title = "";
var description = "";

for each (field in formdata.fields) {
	if (field.name == "file" && field.isFile) {
		filename = field.filename;
		content = field.content;
	}
}

if (filename == undefined || content == undefined || filename == "") {
	status.code = 400;
	status.message = "Please select the archive (Uploaded archive cannot be located in request).";
	status.redirect = true;
  
} else {
	/*var tmpFolder = companyhome.childByNamePath("tmp");	
	if (tmpFolder != null) {
		tmpFolder.remove();
	}
	tmpFolder = companyhome.createFolder("tmp");*/
	upload = companyhome.createFile(filename);
	  
	upload.properties.content.write(content);
	upload.properties.content.setEncoding("UTF-8");
	upload.properties.content.setMimetype("application/zip");
	  
	upload.properties.title = "title";
	upload.properties.description = "description";
	upload.save();

	/*var importAction = actions.create("import");
	importAction.parameters.destination = tmpFolder;
	importAction.execute(upload); 
	
	upload.remove();*/
	
	var grantPermissionsAction = actions.create("grantpermissions");
	grantPermissionsAction.execute(upload);
}