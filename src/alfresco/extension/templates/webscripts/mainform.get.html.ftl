<html>
 <head> 
   <title>Zip/Unzip</title> 
   <link rel="stylesheet" href="${url.context}/css/main.css" TYPE="text/css">
 </head>
 <body>
   <p>
     <form action="zip">
       <fieldset style="width:400px;">
         <legend>Zip Entire Repository</legend>
         <input type="submit" name="submit" value="Zip">
       </fieldset>
     </form>
   <p>
     <form action="unzip" method="post" enctype="multipart/form-data" accept-charset="utf-8">
       <fieldset style="width:400px;">
         <legend>Unzip archive to "/companyName/tmp/")</legend>
       	 Choose archive: <input type="file" name="file"><br>
         <input type="submit" name="submit" value="Unzip">
       </fieldset>     
 </body>
</html>