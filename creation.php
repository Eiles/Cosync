<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Creation de compte");
	//deconnection("http://localhost/coSync/creation.php");
	echo "<form method=\"post\" action=\"http://localhost/coSync/creation.php\">
			<input class=\"btn btn-danger\" type=\"submit\" name=\"deconnection\" value=\"Retour à l'accueil\">
		</form>";
		
		if(isset($_POST["deconnection"])){
			header("Location: http://localhost/coSync/accueil.php");
		}
?>
	<h1>Bienvenue et merci de créér un compte chez nous.</h1>
	<br><br>
	<form method="post" action="creation.php">
		<h3>Renseignez votre adresse mail</h3> <input type="text" name="username"><br>
		<h3>Renseignez votre mot de passe</h3> <input type="password" name="password"><br><br>
		<input class="btn btn-primary" type="submit" value="Créer votre compte">
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