<?php
	require 'tool.php';
	displayHTMLHeader("Page utilisateur");
	debutSession();
	deconnection("http://localhost/coSync/compte.php");
	
	echo "Hello".$_SESSION["id"]." tu t es connecte a ".$_SESSION["arriver"];
	echo "<br><br>";
?>

<form action="http://localhost/coSync/compte.php" method="post">
<input name="register" value="Enregistrer un equipement" type="submit">
<input name="retrieve" value="Afficher vos equipements" type="submit">
</form>

<?php
	
?>


</body>
</html>

