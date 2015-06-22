<?php
	require_once 'tool.php';
	require_once 'user.php';
	require_once 'systems.php';
	displayHTMLHeader("Page utilisateur");
	startSession();
	deconnection("http://localhost/coSync/account.php");
	
	echo "Hello ".$_SESSION["username"]." tu t'es connecté à ".date('D, d M Y H:i:s',$_SESSION["arriver"]);
	echo "<br><br>";
?>

<form action="http://localhost/coSync/account.php" method="post">
<input name="register" value="Enregistrer un equipement" type="submit">
<input name="retrieve" value="Afficher vos equipements" type="submit">
</form>

<?php
	//if(isset($_POST["retrieve"])){
		echo "<br>Voici la liste de vos équipement :<br>";
		$systems = json_decode(retrieveSystems($_SESSION["username"], $_SESSION["password"]));
		var_dump($systems);
		
		foreach ($systems as $obj){
			//var_dump($obj);
			if($obj->is_register){
				systems_button("Unregister");
				if(isset($_POST["Unregister"])){
					updateSystemRegistration($obj->id, 0);
					unset($_POST["Unregister"]);
					unset($_POST["Register"]);
					header("Location: http://localhost/coSync/account.php");
					//echo "<script language=\"javascript\" type=\"text/javascript\">window.location.reload();</script>";
					
					
				}
			}else{
				systems_button("Register");
				if(isset($_POST["Register"])){
					updateSystemRegistration($obj->id, 1);
					unset($_POST["Register"]);
					unset($_POST["Unregister"]);
					header("Location: http://localhost/coSync/account.php");
					//echo "<script language=\"javascript\" type=\"text/javascript\">window.location.reload();</script>";
				}
			}
		}
	//}
	
?>


</body>
</html>
