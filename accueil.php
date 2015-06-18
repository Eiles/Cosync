<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Accueil CoSync");
	
	session_start();
?>
	Bienvenue !!
	<br>
	Connectez vous :
	<form method="post" action="accueil.php">
		username <input type="text" name="username">
		Mot de passe <input type="password" name="password">
		<input type="submit" value="Entrer">
	</form>
	<br><br>
	<form method="post" action="creation.php">
		Vous êtes nouveau ?
		<input type="submit" value="Créér un compte">
	</form>
	<?php
		if (isset($_POST["username"]) && isset($_POST["password"])){
			$username=$_POST["username"];
			$password=$_POST["password"];
			
			if(connection($username,$password))
				header("Location: http://localhost/coSync/account.php");
			else echo "Mauvais username ou mauvais mot de passe";
		}	
	?>
	
	</body>
</html>





































