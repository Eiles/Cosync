<?php
if(!isset($_POST['username']) || !isset($_POST['password']))
	return false;
if(isset($_POST['action'])){
	switch($_POST['action']){
		case "connection":
			echo connection($_POST['username'],$_POST['password']);
			break;
		default:
			break;
	}
}

function connection($username,$password){
	$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
	
	$params = array(":u" => $username, ":p" => $password);
	
	$statement = $dbh->prepare("SELECT `name`, `password`, `id` FROM user WHERE :u = `name` AND :p = `password`");
			
	if($statement && $statement->execute($params)){
		$row = $statement->fetchALL();
		
		if(isset($row[0][2])){
			$_SESSION["username"] = $row[0][0];
			$_SESSION["password"] = $row[0][1];
			$_SESSION["id"] = $row[0][2];
			$_SESSION["arriver"] = time();
			return 1;
		}
		
	}
	
	return 0;	 
}

function creationUser($username, $password){
	$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
	$params = array(":u" => $username);
	
	$statement = $dbh->prepare("SELECT `name` FROM `user` WHERE :u = `name`");
	
	if($statement && $statement->execute($params)){
		$row = $statement->fetchALL();
		if($row)
			echo "Ce nom d'utulisateur est déjà utilisé. Merc d'en choisir un nouveau.";
		else{
			$params = array(":u" => $username, ":p" => $password);
			$statement = $dbh->prepare("INSERT INTO `user` (`name`, `password`) VALUES (:u, :p)");
			if($statement && $statement->execute($params)){
				echo "Insertion réussi";
			}
			else echo "Erreur de requête";
		}
	}
	
}

?>