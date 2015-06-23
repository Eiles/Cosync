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
		
		//var_dump($systems);
		echo "<table>";
		for($i = 0; $i < count($systems); $i++){
			echo "<tr>";
			echo "<td>Equipement numero : ".$i."</td><td>Dernier ip connu : ".$systems[$i]->last_ip."</td><td>";
		// foreach ($systems as $obj){
			//var_dump($obj);
			if($systems[$i]->is_register){
				systems_button("Unregister".$i);
				if(isset($_POST["Unregister".$i])){
					updateSystemRegistration($systems[$i]->id, 0);
					unset($_POST["Unregister".$i]);
					unset($_POST["Register".$i]);
					header("Location: http://localhost/coSync/account.php");
					//echo "<script language=\"javascript\" type=\"text/javascript\">window.location.reload();</script>";
					
					
				}
			}else{
				systems_button("Register".$i);
				if(isset($_POST["Register".$i])){
					updateSystemRegistration($systems[$i]->id, 1);
					unset($_POST["Register".$i]);
					unset($_POST["Unregister".$i]);
					header("Location: http://localhost/coSync/account.php");
					//echo "<script language=\"javascript\" type=\"text/javascript\">window.location.reload();</script>";
				}
			}
			echo "</td></tr>";
		// }
		}
		echo "</table>";
	//}
	
?>


</body>
</html>
