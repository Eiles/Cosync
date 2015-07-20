<?php
	require_once 'tool.php';
	require_once 'user.php';
	displayHTMLHeader("Creation de compte");
	deconnection("http://localhost/coSync/creation.php","Retour à l'accueil");
		
	if(isset($_POST["deconnection"])){
		header("Location: http://localhost/coSync/accueil.php");
	}
?>
	<h1>Bienvenue dans la page de création de compte.<br><br>
	Merci de bien vouloir remplir ce formulaire.</h1><br>
	
	
	<!-- This is the account creation form, all input are require -->
	<form class="form-signin" method="post" action="creation.php">
		<label for="inputEmail" class="sr-only">Username</label>
			<input name="username" type="email" id="inputEmail" class="form-control" placeholder="Votre nom d'utilisateur" required autofocus>
			<label for="inputPassword" class="sr-only">Password</label>
			<input name="password" type="password" id="inputPassword" class="form-control" placeholder="Votre mot de passe" required>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Créer votre compte</button>
	</form>
<?php
	
	//Verification of the existence of both input then the function for create a new user is called.  
	if (isset($_POST["username"]) && isset($_POST["password"])){
		$username=$_POST["username"];
		$password=$_POST["password"];
		
		$lowercase = preg_match('@[a-z]@', $password);
		$uppercase = preg_match('@[A-Z]@', $password);
		$number = preg_match('@[0-9]@', $password);
		
		if(strlen($password) < 6)
			alert("Le mot de passe doit faire minimum 6 caractères");
		else if(!$lowercase && !$uppercase && !$number)
			alert("Le mot de passe doit contenir au minimum un charactère alphanuméric");
		else creationUser($username, $password);
	}
?>
<div align="center"><strong>Votre identifiant doit être une adresse-mail.<br>
	Votre mot de passe doit être composé d'au moins une minuscule ou d'une majuscule ou d'un chiffre et doit être composé d'au moins 6 charactères.</strong>
</div>
</div>
</body>
</html>