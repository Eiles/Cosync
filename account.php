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
<input class="btn btn-primary" name="download" value="Telecharger le client" type="submit">
<!-- <input name="retrieve" value="Afficher vos equipements" type="submit"> -->
</form>

<?php
	//if(isset($_POST["retrieve"])){
		echo "<br>Voici la liste de vos equipement :<br>";
		$systems = json_decode(retrieveSystems($_SESSION["username"], $_SESSION["password"]));
		
		//var_dump($systems);
		echo "<table class =\"table table-bordered\">";
		for($i = 0; $i < count($systems); $i++){
			echo "<tr>";
			echo "<td>Equipement numero : ".$i."</td><td>Dernier ip connu : ".$systems[$i]->last_ip."</td><td class=\"text-justify\">";
		// foreach ($systems as $obj){
			//var_dump($obj);
			if($systems[$i]->is_register){
				systems_button("Unregister", $i);
				if(isset($_POST["Unregister".$i])){
					updateSystemRegistration($systems[$i]->id, 0);
					unset($_POST["Unregister".$i]);
					unset($_POST["Register".$i]);
					header("Location: http://localhost/coSync/account.php");
					//echo "<script language=\"javascript\" type=\"text/javascript\">window.location.reload();</script>";
					
					
				}
			}else{
				systems_button("Register", $i);
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
	if(isset($_POST["download"])){
		$clientPath = 'C:\wamp\www\CoSync';
		$clientName = 'client.jar';
		if(file_exists($clientName)){
			 header('Content-Description: File Transfer');
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename='.basename($clientName));
			header('Expires: 0');
			header('Cache-Control: must-revalidate');
			header('Pragma: public');
			header('Content-Length: ' . filesize($clientName));
			readfile($clientName);
		}
	}
?>


</body>
</html>
