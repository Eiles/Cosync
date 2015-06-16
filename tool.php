<?php
function debutSession(){
	session_start();
	if(time()-$_SESSION["arriver"]>25*60){
		session_destroy();
		header("Location: http://localhost/projet_annuel/authentification/authentification.php");
	}
}

function verifSession(){
	if($_SESSION["job"]=="Eleve"||$_SESSION["job"]=="President"){
		echo "Vous n'avez pas le droits d'être ici. Vous aller être redirigé sans voir ce message";
		header("Location: http://localhost/projet_annuel/accueil/accueil.php");
	}
}

function displayHTMLHeader($title){
	$str="<html><head><title>$title</title></head><body>";
	echo $str;
}


function executeQuery($query) {
	$dbIp="localhost";
	$dbUser="root";
	$dbPwd="toor";
	$dbName="coSync";
	
	$connexionId=mysqli_connect($dbIp,$dbUser,$dbPwd) or die("Erreur de connexion");
	$dbId=mysqli_select_db($connexionId,$dbName) or die("Erreur de base de donnee");
	
	$result=mysqli_query($connexionId,$query) or die("Erreur de requete");
	
	mysqli_close($connexionId) or die("Erreur de fermeture");
	
	return $result;
}

function AfficherMenu(){
	$urlServeur="http://localhost/projet_annuel/";
	echo "<table class=\"tab1\" align=\"center\" >
			<tr>
				<td class=\"td1\" align=\"center\"><a href=\"".$urlServeur."promotion/promotion.php\"><b>PROMOTION</b></a></td>
				<td class=\"td2\" align=\"center\"><a href=\"".$urlServeur."pedagogie/pedagogie.php\"><b>PEDAGOGIE</b></a></td>
				<td class=\"td3\" align=\"center\"><a href=\"".$urlServeur."association/association.php\"><b>ASSOCIATION</b></a></td>
				<td class=\"td4\" align=\"center\"><a href=\"".$urlServeur."bon_app/bon_app.php\"><b>BON</br> APPETIT</b></a></td>
				<td class=\"td5\" align=\"center\"><a href=\"".$urlServeur."liens-utiles/liens-utiles.php\"><b>LIENS</br> UTILES</b></a></td>
			</tr>
		</table>";
}

function Association($asso,$idasso){
	$query="SELECT idstudent, mail FROM eleve WHERE idasso = $idasso";
	$result=executeQuery($query);
	$ligne=mysqli_fetch_array($result);
	echo 
	"<div align=\"center\"><br><br><br>
		<h3>Bienvenue à l'association $asso</h3>
		<p>Le pr&eacute;sident ".$ligne[0]." que vous pouvez contacter grâce au lignes ci-dessous</p>
		
	</div>";
	return $ligne[1];
}

function BlogAsso($idasso){
	echo "
		<div align=\"center\"><a href=\"http://localhost/projet_annuel/association/association.php?opt=".$idasso."\">Acceder au blog</a></div>
		<div align=\"center\"><a href=\"http://localhost/projet_annuel/forum/voirssforum.php\">Forum</a> </div>";
}

function MailAsso($mail,$msg,$sujet){
	$from=$_SESSION["mail"];
	$message=$msg;
	$subject=$sujet;
	$to=$_POST["mail"];
	$headers="from:$from";
	mail($to, $subject, $message, $headers);	
}

function displayFTP($mat, $perm){
	if($perm==1){
		$query="SELECT * FROM ftp WHERE mat = $mat AND perm= $perm";
	}else if($perm==0){
		$query="SELECT * FROM ftp WHERE mat = $mat";
	}
	$result=executeQuery($query);
	$ligne=mysqli_fetch_array($result);
	echo "<br><br><br><br><div align=\"center\"><table border=\"thin\">
	<tr><td>Le liens vers le fichier</td><td>La description du fichier</td><td>La date d'upload du fichier</td><td>Proprietaire du fichier</td>";
	while($ligne!=false){
		echo "<tr><td><a class=\"ftp\" href=\"http://localhost/projet_annuel/ftp/matiere".$mat."/".$ligne[0].$ligne[3]."\">"
		.$ligne[0].$ligne[3]."</a></td><td>".$ligne[1]."</td><td>".$ligne[5]."</td><td>".$ligne[4]."</td></tr>";
		$ligne=mysqli_fetch_array($result);
	}
	echo "</table></div>";
}

?>

