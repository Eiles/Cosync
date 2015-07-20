<?php
	//Function to deconnect user of the website after X min of inactivity
	function startSession(){
		session_start();
		if(time()-$_SESSION["arriver"]>25*60){
			session_destroy();
			unset($_SESSION);
			header("Location: http://localhost/coSync/accueil.php");
		}
	}
	
	//Function to allways display the same HTML Header to every page
	function displayHTMLHeader($title){
		$str="<!DOCTYPE html>
		<html lang=\"en\">
			<head>
				<link rel=\"icon\" type=\"image/png\" href=\"logo.png\" style=\"width:16px;height:16px;\"\>
				<meta charset=\"utf-8\">
				<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">
				<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
				<title>$title</title>
				<link href=\"css/bootstrap.min.css\" rel=\"stylesheet\">
				<link href=\"css/signin.css\" rel=\"stylesheet\">
				
			</head>
			<body>
			<div align=\"center\"><img src=\"logo.png\" style=\"width:100px;height:100px;\"></div>
			<div class=\"container\">";
		echo $str;
	}
	
	//Function create in order to quickly create an means to return at the main page. It take the path of where the button is create and also take the string which will be display inside the button.
	function deconnection($wherefrom,$value){
		echo "<form class=\"form-signin\"  method=\"post\" action=".$wherefrom.">
			<button class=\"btn btn-lg btn-danger btn-block\" type=\"submit\" name=\"deconnection\">".$value."</button>
		</form>";
		
		if(isset($_POST["deconnection"])){
			$_SESSION=array();
			session_destroy();
			unset($_SESSION);
			header("Location: http://localhost/coSync/accueil.php");
		}
	}
	
	//Function to create the button display in the account page in order to set the visibility of your system. Change the class and the string of the button.
	function systems_button($value, $number){
		echo 
		"<form action=\"account.php\"  method=\"post\">
			<input class=\"btn btn-block btn-" .
			(substr($value, 0, 1) == 'V' ? "info" : "warning")
			."\" name=\"".$value.$number."\" type=\"submit\" value=\"".$value."\">
		</form>";
	}
	
	//Quick javascript pop-up function
	function alert($message){
		echo '<script type="text/javascript">window.alert("'.$message.'");</script>';
	}
?>