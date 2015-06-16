<?php
	require 'tool.php';
	displayHTMLHeader("Creation de compte");
?>
	Bienvenue et merci de creer un compte chez nous.
	<form method="post" action="creation.php">
		Renseigner votre adresse mail <input type="text" name="username">
		Renseigner votre mot de passe <input type="password" name="password">
		<input type="submit" value="Entrer">
	</form>
<?php
	if (isset($_POST["username"]) && isset($_POST["password"])){
		$username=$_POST["username"];
		$password=$_POST["password"];
		
		if($name == ""){
			echo "Le login est obligatoire !";
		}
		else if($password == ""){
			echo "Le mot de passe est obligatoire !";
		}
		else {
			$query="SELECT name
			FROM user
			WHERE '$username' = name";
			$result=executeQuery($query);
			$ligne=mysqli_fetch_array($result);
			
			if($ligne[0]==$username) echo "Ce nom d'utilisateur et deja utilise. Merci d'en choisir un nouveau";
			
			if ($ligne == false){
				$query="INSERT INTO `user` (`name`, `password`) 
				VALUES ('$username', '$password')";
				executeQuery($query);
				echo "Creation de compte reussi : '$username'";
			}
		}
	}
?>

</body>
</html>