<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "test";
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$stmt_check = $conn->prepare("SELECT COUNT(*) FROM users WHERE login=?");
$stmt_check->bind_param("s", $login);
$login = $_POST['username'];
$password = $_POST['password'];
$stmt_check->execute();
$stmt_check->bind_result($result);
$stmt_check->fetch();
$stmt_check->close();
if($result==0){
	$stmt = $conn->prepare("INSERT INTO users (login, password) VALUES (?, ?)");
	$stmt->bind_param("ss", $login, $password);
    $stmt->execute();
	$stmt->close();
    echo "<html><body><h2 style='text-align:center'>Регистрация прошла успешно!</h2></body></html>";
}
else{
	echo "<html><body><h2 style='text-align:center'>Пользователь с таким именем уже существует! Попробовать еще <a href ='../index.html'>раз</a>.</h2></body></html>";
}
$conn->close();
?>