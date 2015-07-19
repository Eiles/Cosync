<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Creation de compte");
	deconnection("http://localhost/coSync/creation.php","Retour à l'accueil");
	echo "<div class=\"container\">";
		
		if(isset($_POST["deconnection"])){
			header("Location: http://localhost/coSync/accueil.php");
		}
?>
	<h1>Bienvenue et merci de créér un compte chez nous.</h1>
	<br><br>
	<form class="form-signin" method="post" action="creation.php">
		<label for="inputEmail" class="sr-only">Username</label>
			<input name="username" type="text" id="inputEmail" class="form-control" placeholder="Votre nom d'utilisateur" required autofocus>
			<label for="inputPassword" class="sr-only">Password</label>
			<input name="password" type="password" id="inputPassword" class="form-control" placeholder="Votre mot de passe" required>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Créer votre compte</button>
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
</div>
</body>
</html>