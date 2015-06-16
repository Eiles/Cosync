<?php

function connection($username,$password){
	$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
	
	$params = array(":u" => $username, ":p" => $password);
	
	$statement = $dbh->prepare("SELECT `name`, `password`, `id` FROM user WHERE :u = `name` AND :p = `password`");
			
	if($statement && $statement->execute($params))
		$row = $statement->fetchALL();
	/*
	$sql = "SELECT name, password, id
			FROM user
			WHERE ".$username." = name AND ".$password." = password";
	$connection = $dbh->query($sql)->fetchALL();
	
	*/
	 var_dump($row);
}

?>