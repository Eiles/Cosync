<?php
	require_once 'tool.php';
	
	//If the application try to perform any action, the server will ensure that the username and password have been well transmited
	if(!isset($_POST['username']) || !isset($_POST['password']))
		return false;
	//Here there is a verification about who is using the function, if it the website it will put the parameter key to nokey but if it is the application it will put the parameter key to the current key of the system
	if(isset($_POST['action'])){
		switch($_POST['action']){
			case "connection":
				echo connection($_POST['username'],$_POST['password'], $_POST['key']);
				break; 
			default:
				break;
		}
	}
	
	//This function take the username, the password and the key of the system. If it is the website that did call this function it will ensure that the username and password is the same as them in the database and authorize the user to get access to the website. If it is the application that did call the function it will either update the last_ip of the system or create the current system in the database if it does not exist.
	function connection($username,$password,$key){
		$password = md5($password);
		//create an access to the database
		$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
		
		//this verification is to know if it is a user on the website or the application who call the function. If the key = "nokey" then it is the user on the web site.
		if($key != "nokey"){
			
			//First of all we use the username and password to get the id of the user
			$params = array(":u" => $username, ":p" => $password);
			$statement = $dbh->prepare("SELECT user.id FROM user WHERE name = :u AND password = :p");
			
			if($statement && $statement->execute($params))
				$id = $statement->fetchColumn();
			if($id != null)
				echo 1;
			else echo 0;
			
			//Then we get the key of the current system if there is one
			$params = array(":id" => $id, ":k" => $key);
			$statement = $dbh->prepare("SELECT `key` FROM systems WHERE `user_id` = :id AND `key` = :k");
			if($statement && $statement->execute($params)){
				$row = $statement->fetchColumn();
				if(!$row)
					//Else we indicate there is none
					$isKeyExist = "nokey";
				else
					$isKeyExist = $row;
			}
			
			//After that, we either update the last_ip of the current system or insert it in the database
			$params = array(":id" => $id,":k" => $key, ":ip" => $_SERVER['REMOTE_ADDR']);
			
			if($isKeyExist == $row){
				$statement = $dbh->prepare("UPDATE  `systems` SET `last_ip` = :ip WHERE `user_id` = :id AND `key` = :k");
				$statement->execute($params);
			}else{
				$statement = $dbh->prepare("INSERT INTO `systems` (`last_ip`,`is_master`,`key`,`user_id`) VALUES (:ip,1,:k,:id)");
				$statement->execute($params);
			}
			//At the end we stop the function
			return;
		}
		
		//In this case the function is call by the website and we check if the username and the password are the same that in the database
		$params = array(":u" => $username, ":p" => $password);
		
		$statement = $dbh->prepare("SELECT `name`, `password`, `id` FROM user WHERE :u = `name` AND :p = `password`");
		
		//If the user is well logged, then we display all his system with the last_ip knowned and we save the time when he logged in order to deconnect him after X minutes
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
	
	//This function allow a new user to create an account in order to get access to the application.
	function creationUser($username, $password){
		$password = md5($password);
		$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
		$params = array(":u" => $username);
		
		//Verification if there is allready the same username in the database
		$statement = $dbh->prepare("SELECT `name` FROM `user` WHERE :u = `name`");
		
		if($statement && $statement->execute($params)){
			$row = $statement->fetchALL();
			if($row)
				//display a pop-up in which you inform the new user that this username is allready taken
				alert("Ce nom d'utulisateur est déjà utilisé. Merci d'en choisir un nouveau");
			else{
				//else we create the user in the database and display a confirmation message
				$params = array(":u" => $username, ":p" => $password);
				$statement = $dbh->prepare("INSERT INTO `user` (`name`, `password`) VALUES (:u, :p)");
				if($statement && $statement->execute($params)){
					alert("Création de compte réussi");
				}
				else alert("Erreur de requête");
			}
		}
	}
?>