<?php
	//If the application try to perform any action, the server will ensure that the username and password have been well transmited
    if(!isset($_POST['username']) || !isset($_POST['password']))
        return false;
	
	//We choose wich action will be perform on the call
    switch($_POST['action']){
        case "retrieve":
            echo retrieveSystems($_POST['username'],$_POST['password']);
            break;
        case "register":
            echo registerSystem($_POST['username'],$_POST['password'],$_POST['master']);
            break;
    }

//This function will be call by the application and will return all the systems of a user in a json encoding.
function retrieveSystems($username,$password){
    $dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
    $sql =  'SELECT systems.id,systems.last_ip,systems.key,systems.is_master,systems.is_register FROM `systems` JOIN user on user_id=user.id WHERE user.name="'.$username.'" AND user.password="'.$password.'"';
    $systems=$dbh->query($sql)->fetchAll(PDO::FETCH_ASSOC);
    return json_encode($systems);
}

//This fucntion will be call by the application and will register in the database the current system.
function registerSystem($username,$password,$is_master){
    $dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
    $user=$dbh->query('SELECT id from user WHERE username="'.$username.'" and password="'.$password.'"')->fetchColumn();
    if(!user){
        return false;
    }
    $key=uniqid();
    $sql =  'INSERT INTO systems (last_ip,is_master,key,user_id) VALUES ("'.$_SERVER['REMOTE_ADDR'].'",'.($is_master+0).',"'.$key.'",'.$user.')';
    $dbh->query($sql);
    return json_encode(array('key'=>uniqid));
}

//This function will update the database and inform the application of which system is visible or not. The function take the system id and the value of the visibility (0 for unvisible, 1 for visible) 
function updateSystemRegistration($id, $value){
	$dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'toor');
	$sql = "UPDATE `systems` SET `is_register` = ".$value." WHERE `id` = ".$id;
	$dbh->query($sql);
}
?>