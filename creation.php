<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Creation de compte");
	deconnection("http://localhost/coSync/creation.php");
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
		
		if($username == "")
			echo "Le login est obligatoire !";
		else if($password == "")
			echo "Le mot de passe est obligatoire !";
		else creationUser($username, $password);
	}
?>

</body>
</html>