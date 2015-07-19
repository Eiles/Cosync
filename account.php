<?php
	require_once 'tool.php';
	require_once 'user.php';
	require_once 'systems.php';
	//Display of the header of the HTML file
	
	$path = "http://localhost/";
	
	displayHTMLHeader("Page utilisateur");
	startSession();
	deconnection($path."coSync/account.php");
	
	//Display of the username of the user and when did he connect
	echo "<h2>Hello ".$_SESSION["username"]." tu t'es connecté à ".date('D, d M Y H:i:s',$_SESSION["arriver"])." avec cette adresse IP : ".$_SERVER['REMOTE_ADDR'];
	echo "</h2><br><br>";
?>

<!-- Download button which download the client in the root folder -->
<form action=<?php echo $path."coSync/account.php" ?> method="post">
	<input class="btn btn-primary" name="download" value="Telecharger le client" type="submit">
</form>

<?php
		//Display all the system of the user by decoding the json of the request sql
		echo "<h3>Voici la liste de vos equipement :<br>";
		$systems = json_decode(retrieveSystems($_SESSION["username"], $_SESSION["password"]));
		
		//Creation of a dynamic table in which every system will be displayed
		echo "<table class =\"table table-bordered\">";
		for($i = 0; $i < count($systems); $i++){
			echo "<tr><td><strong>Numéro de l'équipement</strong><td><strong>Dernière adresse IP connue</strong></td><td><strong>Visibilité de l'équipement (cliquez pour changer)</strong></tr>";
			echo "<td># ".$i."</td><td>".$systems[$i]->last_ip."</td><td class=\"text-justify\">";
			
			//If the system is register, display a button to unregister the system for the client
			if($systems[$i]->is_register){
				systems_button("Visible", $i);
				if(isset($_POST["Visible".$i])){
					updateSystemRegistration($systems[$i]->id, 0);
					//Reset the state of the button in order to avoid other modification and reload the page
					unset($_POST["Unvisible".$i]);
					unset($_POST["Visible".$i]);
					header("Location:".$path."coSync/account.php");	
				}
			}else{
				//Same here but if the system is not register
				systems_button("Unvisible", $i);
				if(isset($_POST["Unvisible".$i])){
					updateSystemRegistration($systems[$i]->id, 1);
					unset($_POST["Visible".$i]);
					unset($_POST["Unvisible".$i]);
					header("Location:".$path."coSync/account.php");
				}
			}
			echo "</td></tr>";
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
</body>
</html>
