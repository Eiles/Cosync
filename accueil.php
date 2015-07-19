<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Accueil CoSync");
	session_start();
?>
	<div class="container">
		<h1>Bienvenue sur le site CoSync, le logiciel de peer to peer entre amis</h1>
		<br>
		<h2>Vous avez déjà un compte ?<br><br>Connectez vous :</h2>
		<!-- Sign in form, all input are require -->
		<form class="form-signin" method="post" action="accueil.php">
			
			<label for="inputEmail" class="sr-only">Username</label>
			<input name="username" type="text" id="inputEmail" class="form-control" placeholder="Nom d'utilisateur" required autofocus>
			<label for="inputPassword" class="sr-only">Password</label>
			<input name="password" type="password" id="inputPassword" class="form-control" placeholder="Mot de passer" required>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Connection</button>
		</form>
		<br>
		<br>
		<!-- Acces to the account creation page -->
		<h2>Sinon créez-vous un compte !</h2>
		<form class="form-signin" method="post" action="creation.php">
			<button class="btn btn-lg btn-primary btn-block" type="submit">Créer un compte</button>
		</form>
	</div>
	<?php
		//If the user sign in correctly he will have acces to the account page, else a pop-up will display an error message
		if (isset($_POST["username"]) && isset($_POST["password"])){
			$username=$_POST["username"];
			$password=$_POST["password"];
			
			if(connection($username,$password, "nokey"))
				header("Location: http://localhost/coSync/account.php");
			else alert("Mauvais username ou mauvais mot de passe");
		}	
	?>
	</body>
</html>