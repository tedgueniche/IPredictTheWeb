<?php
//Show all errors - debug only
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

$folder = 'datasets/uploaded';
 
if (!empty($_FILES)) {
     
    $tempFile = $_FILES['file']['tmp_name'];          
      
    $targetPath = dirname( __FILE__ ) . "/" . $folder . "/";
    $targetFile =  $targetPath . $_FILES['file']['name'];
 
    echo $targetFile;

    if(move_uploaded_file($tempFile,$targetFile)) {

    }
    else {
    	fail("Could move uploaded file, check file/folder permission");
    }
     
}
else {
	fail("No files were pushed.");
}


function fail($msg) {
	echo $msg;
	exit(1);
}

?>   