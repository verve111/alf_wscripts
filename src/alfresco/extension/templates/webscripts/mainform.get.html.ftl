<html>
 <head> 
   <title>Zip/Unzip</title> 
   <link rel="stylesheet" href="${url.context}/css/main.css" TYPE="text/css">
 </head>
 <body>
   <p>
   <table>
     <form action="zip">
       <tr><td></td></tr>
       <tr><td><input type="submit" name="submit" value="Zip entire repo"></td></tr>
     </form>
   </table>
   <p>
   <table>
     <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="utf-8">
       <tr><td></td></tr>
       <tr><td>File: <input type="file" name="file"></td></tr>
       <tr><td><input type="submit" name="submit" value="Unzip (to /companyName/tmp/)"></td></tr>
     </form>
   </table>
 </body>
</html>