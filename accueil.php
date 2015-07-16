<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Accueil CoSync");
	session_start();
?>
	<h1>Bienvenue sur le site CoSync, le logiciel de peer to peer entre amis</h1>
	<br>
	<h2>Vous avez déjà un compte ?<br><br>Connectez vous :</h2>
	<form method="post" action="accueil.php">
		<strong>username</strong> <input type="text" name="username">
		<strong>Mot de passe</strong> <input type="password" name="password">
		<input class="btn btn-primary" type="submit" value="Connection">
	</form>
	<br><br>
	<form method="post" action="creation.php">
		<h2>Sinon créez-vous un compte !</h2>
		<input class="btn btn-primary" type="submit" value="Créer un compte">
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