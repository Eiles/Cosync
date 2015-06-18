<?php
function debutSession(){
	session_start();
	if(time()-$_SESSION["arriver"]>25*60){
		session_destroy();
		header("Location: http://localhost/coSync/accueil.php");
	}
}

function verifSession(){
	if($_SESSION["job"]=="Eleve"||$_SESSION["job"]=="President"){
		echo "Vous n'avez pas le droits d'être ici. Vous aller être redirigé sans voir ce message";
		header("Location: http://localhost/projet_annuel/accueil/accueil.php");
	}
}

function displayHTMLHeader($title){
	$str="<html><head><title>$title</title></head><body>";
	echo $str;
}

function deconnection($wherefrom){
	echo "<form method=\"post\" action=".$wherefrom.">
		<input type=\"submit\" name=\"deconnection\" value=\"Se deconnecter\">
	</form>";
	
	if(isset($_POST["deconnection"])){
		$_SESSION=array();
		session_destroy();
		unset($_SESSION);
		header("Location: http://localhost/coSync/accueil.php");
	}
}

function systems_button($value){
	echo 
	"<form action=\"account.php\"  method=\"post\">
		<input name=\"".$value."\" type=\"submit\" value=\"".$value."\">
	</form>";
}
?>

