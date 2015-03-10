<?php

$model = $_POST['model'];
$dataset = $_POST['dataset'];
$kfold = $_POST['kfold'];
$sequenceCount = $_POST['sequenceCount'];

$args = $model .' '. $dataset .' '. $kfold .' '. $sequenceCount;
$cmd = "java -jar IPredict_0.01.jar ". $args;
exec($cmd, $rawOutput);

$output = "";
foreach($rawOutput as $line)
{
	$output .= $line . "\n";
}


echo $output;



?>
