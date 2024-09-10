<?php
header('Content-Type: application/json');

// Enable error reporting (for debugging)
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Database credentials
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "thirsttap";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(array("success" => "0", "message" => "Connection failed: " . $conn->connect_error)));
}

// Get POST data
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Validate input
if (empty($email) || empty($password)) {
    echo json_encode(array("success" => "0", "message" => "Email and Password are required."));
    $conn->close();
    exit();
}

// Prepare and execute SQL query to get password hash for the email
$sql = $conn->prepare("SELECT userid, password FROM user_data WHERE email = ?");
$sql->bind_param("s", $email);
$sql->execute();
$sql->store_result();
$sql->bind_result($user_id, $password_hash);

// Check if user exists
if ($sql->num_rows === 0) {
    echo json_encode(array("success" => "0", "message" => "Invalid email or password."));
    $sql->close();
    $conn->close();
    exit();
}

// Fetch the result (fetch user ID and password hash)
$sql->fetch();

// Verify password
if (!password_verify($password, $password_hash)) {
    echo json_encode(array("success" => "0", "message" => "Incorrect password."));
    $sql->close();
    $conn->close();
    exit();
}

// Generate a token (this could be a JWT or a simple token for now)
$token = bin2hex(random_bytes(16));

// Respond with success and token
echo json_encode(array("success" => "1", "token" => $token));

// Close connection
$sql->close();
$conn->close();
?>
