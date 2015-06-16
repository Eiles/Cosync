<?php

    if(!isset($_POST['username']) || !isset($_POST['password']))
        return false;
    switch($_POST['action']){
        case "retrieve":
            echo retrieveSystems($_POST['username'],$_POST['password']);
            break;
        case "register":
            echo registerSystem($_POST['username'],$_POST['password'],$_POST['master']);
            break;
    }
                      
function retrieveSystems($username,$password){
    $dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'strtoupper');
    $sql =  'SELECT systems.last_ip,systems.key,systems.is_master FROM `systems` JOIN user on user_id=user.id WHERE user.name="'.$username.'" AND user.password="'.$password.'"';
    $systems=$dbh->query($sql)->fetchAll(PDO::FETCH_ASSOC);
    return json_encode($systems);
}

function registerSystem($username,$password,$is_master){
    $dbh = new PDO('mysql:host=127.0.0.1;dbname=cosync', 'root', 'strtoupper');
    $user=$dbh->query('SELECT id from user WHERE username="'.$username.'" and password="'.$password.'"')->fetchColumn();
    if(!user){
        return false;
    }
    $key=uniqid();
    $sql =  'INSERT INTO systems (last_ip,is_master,key,user_id) VALUES ("'.$_SERVER['REMOTE_ADDR'].'",'.($is_master+0).',"'.$key.'",'.$user.')';
    $dbh->query($sql);
    return json_encode(array('key'=>uniqid));
}
?>