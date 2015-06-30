<?php
function startSession(){
	session_start();
	if(time()-$_SESSION["arriver"]>25*60){
		session_destroy();
		unset($_SESSION);
		header("Location: http://localhost/coSync/accueil.php");
	}
}

function displayHTMLHeader($title){
	$str="<!DOCTYPE html>
	<html lang=\"en\">
		<head>
			<meta charset=\"utf-8\">
			<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">
			<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
			<title>$title</title>
			<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\">
		</head>
		<body>";
	echo $str;
}

function deconnection($wherefrom){
	echo "<form method=\"post\" action=".$wherefrom.">
		<input class=\"btn btn-danger\" type=\"submit\" name=\"deconnection\" value=\"Se deconnecter\">
	</form>";
	
	if(isset($_POST["deconnection"])){
		$_SESSION=array();
		session_destroy();
		unset($_SESSION);
		header("Location: http://localhost/coSync/accueil.php");
	}
}

function systems_button($value, $number){
	echo 
	"<form action=\"account.php\"  method=\"post\">
		<input class=\"btn btn-block btn-".
		(substr($value, 0, 1) == 'R' ? "info" : "warning")
		."\" name=\"".$value.$number."\" type=\"submit\" value=\"".$value."\">
	</form>";
}
?>

