<?php
	require_once 'tool.php';
	require_once 'user.php';
	require_once 'systems.php';
	//Display of the header of the HTML file
	
	$path = "http://localhost/";
	
	displayHTMLHeader("Page utilisateur");
	startSession();
	deconnection($path."coSync/account.php","Se deconnecter");
	
	//Display of the username of the user and when did he connect
	echo "<h2>Bienvenue <strong>".$_SESSION["username"]."</strong> vous êtes identifié avec l'adresse ".$_SERVER['REMOTE_ADDR'].", le ".date('d/m/Y',$_SESSION["arriver"])." à ".date('H:i:s',$_SESSION["arriver"]);
	echo "</h2><br><br>";
?>

<!-- Download button which download the client in the root folder -->
<form class="form-signin" action=<?php echo $path."coSync/account.php" ?> method="post">
	<button name="download" class="btn btn-lg btn-success btn-block" type="submit">Télécharger le client</button>
</form>

<?php
		//Display all the system of the user by decoding the json of the request sql
		echo "<h3>Voici la liste de vos equipement :<br>";
		$systems = json_decode(retrieveSystems($_SESSION["username"], $_SESSION["password"]));
		
		//Creation of a dynamic table in which every system will be displayed
		echo "<table class =\"table table-bordered\">";
		echo "<tr><td class=\"element\"><strong>Numéro de l'équipement</strong><td class=\"element\"><strong>Dernière adresse IP connue</strong></td><td class=\"element\"><strong>Visibilité de l'équipement (cliquez pour changer)</strong></tr>";
		for($i = 0; $i < count($systems); $i++){
			
			echo "<td class=\"element\"># ".$i."</td><td class=\"element\">".$systems[$i]->last_ip."</td><td class=\"button\" align=\"center\"><div style=\"width:100px\">";
			
			//If the system is visible, display a button to set the system to unvisible
			if($systems[$i]->is_register){
				systems_button("Visible", $i);
				if(isset($_POST["Visible".$i])){
					updateSystemRegistration($systems[$i]->id, 0);
					//Reset the state of the button in order to avoid other modification and reload the page
					unset($_POST["Invisible".$i]);
					unset($_POST["Visible".$i]);
					header("Location:".$path."coSync/account.php");	
				}
			}else{
				//If the system is unvisible display a button to set the system to visible
				systems_button("Invisible", $i);
				if(isset($_POST["Invisible".$i])){
					updateSystemRegistration($systems[$i]->id, 1);
					unset($_POST["Visible".$i]);
					unset($_POST["Invisible".$i]);
					header("Location:".$path."coSync/account.php");
				}
			}
			echo "</div></td></tr>";
		}
		echo "</table>";

	
	//If the button download if pressed, the .jar will be donwloaded
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
</div>
</body>
</html>
