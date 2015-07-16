<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Creation de compte");
	deconnection("http://localhost/coSync/creation.php");
?>
	<h1>Bienvenue et merci de créér un compte chez nous.</h1>
	<br><br><br><br>
	<form method="post" action="creation.php">
		<strong>Renseigner votre adresse mail</strong> <input type="text" name="username">
		<strong>Renseigner votre mot de passe</strong> <input type="password" name="password">
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