<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


$pathToRequest = "requests";
$action = $_GET['action'];

if(isset($action) && $action == 'push') {
	push($pathToRequest);
}
elseif(isset($action) && $action == 'get')  {
	get($pathToRequest);
}
else {
	fail("Unknown action");
}


function push($pathToRequest) {
	$req = [];

	$req['model'] 			= $_POST['model'];
	$req['dataset'] 		= $_POST['dataset'];
	$req['sequenceCount'] 	= $_POST['sequenceCount'];

	$req['minS'] 			= $_POST['minS'];
	$req['prefixSize'] 		= $_POST['prefixSize'];
	$req['suffixSize'] 		= $_POST['suffixSize'];
	$req['kFold'] 			= $_POST['kFold'];


	$reqJson = json_encode($req);
	$id = rand(1, 10000);
	$requestPath = $pathToRequest . "/" . $id . ".experimentRequest";

	//writing the request
	$written = file_put_contents($requestPath, $reqJson);

	//if write failed
	if($written === false) {
		fail("Could not write request to file (". $requestPath . ")");
	}
	//else
	else {
		echo $id;
	}
}


function get($pathToRequest) {

	$id = $_GET['id'];
	$requestPath = $pathToRequest . "/" . $id . ".experimentResponse";

	if(file_exists($requestPath) == false) {
		echo 'false';
	}
	else {
		$results = file_get_contents($requestPath);
		echo $results;
		unlink($requestPath);
	}
}


function fail($msg) {
	echo $msg;
	exit(1);
}


?>
