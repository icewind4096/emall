<html>
<body>
<h2>Hello World!</h2>
<form name="form1" action="/emall_war/manager/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="上传文件">
</form>

<form name="form2" action="/emall_war/manager/product/richUpload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="上传富文件">
</form>
</body>
</html>
