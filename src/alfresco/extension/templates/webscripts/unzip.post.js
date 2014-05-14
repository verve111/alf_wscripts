var filename = null;
var content = null;
var title = "";
var description = "";

// locate attributes
for each (field in formdata.fields) {
	if (field.name == "file" && field.isFile) {
		filename = field.filename;
		content = field.content;
	}
}

if (filename == undefined || content == undefined || filename == "") {
	status.code = 400;
	status.message = "Uploaded archive cannot be located in request. Please specify the archive path.";
	status.redirect = true;
  
} else {
	var tmpFolder = companyhome.childByNamePath("tmp");	
	if (tmpFolder == null) {
		var tmpFolder = companyhome.createFolder("tmp");
	}
	upload = tmpFolder.createFile(filename) ;
	  
	upload.properties.content.write(content);
	upload.properties.content.setEncoding("UTF-8");
	upload.properties.content.guessMimetype(filename);
	  
	upload.properties.title = "title";
	upload.properties.description = "description";
	upload.save();

	var importAction = actions.create("import");
	importAction.parameters.destination = tmpFolder;
	importAction.execute(upload); 
	
	// setup model for response template
	model.upload = upload;
	
	//upload.remove();
}