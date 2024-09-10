<?php
// Database connection settings
$host = '127.0.0.1';   // Replace with your database host
$db   = 'thirsttap'; // Replace with your database name
$user = 'root';         // Replace with your database username
$pass = '';             // Replace with your database password (if applicable)
$charset = 'utf8mb4';

// Data Source Name (DSN)
$dsn = "mysql:host=$host;dbname=$db;charset=$charset";

try {
    // Create a PDO instance (connect to the database)
    $pdo = new PDO($dsn, $user, $pass);
    
    // Set PDO error mode to exception
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    // Handle connection error
    echo 'Connection failed: ' . $e->getMessage();
    exit;
}
?>
