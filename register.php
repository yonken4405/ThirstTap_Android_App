
<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\SMTP;
require 'vendor/autoload.php';
require 'db.php'; // Ensure this contains your PDO connection





// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Initialize response array
$response = ['success' => 0, 'message' => ''];

// Get POST data and sanitize inputs
$email = filter_var($_POST['email'] ?? '', FILTER_SANITIZE_EMAIL);
$email = strtolower($email); // Convert email to lowercase
$password = $_POST['password'] ?? '';
$name = htmlspecialchars($_POST['name'] ?? '');
$phone_num = htmlspecialchars($_POST['phone_num'] ?? '');

// Validate input
if (!filter_var($email, FILTER_VALIDATE_EMAIL) || empty($password) || empty($name) || empty($phone_num)) {
    $response['message'] = 'Invalid input. Please check your details.';
    echo json_encode($response);
    exit;
}

// Check if email already exists in user_data
$stmt = $pdo->prepare("SELECT email FROM user_data WHERE email = :email");
$stmt->execute(['email' => $email]);

if ($stmt->rowCount() > 0) {
    $response['message'] = 'Email already registered.';
    echo json_encode($response);
    exit;
}

// Check if email already exists in unverified_users
$stmt = $pdo->prepare("SELECT email FROM unverified_users WHERE email = :email");
$stmt->execute(['email' => $email]);

if ($stmt->rowCount() > 0) {
    $response['message'] = 'Email pending verification.';
    echo json_encode($response);
    exit;
}

try {
    // Generate a random verification code
    $verification_code = random_int(1000, 9999);

    // Start output buffering
    ob_start();

    // Send the verification email
    $mail = new PHPMailer(true);
    // Server settings
    $mail->SMTPDebug = 0;
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 'thirsttapmail@gmail.com';
    $mail->Password = 'ddfu ouyi imqu ksbm'; // This should be stored in an environment variable
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = 587;

    // Recipients
    $mail->setFrom('thirsttapmail@gmail.com', 'ThirstTap');
    $mail->addAddress($email);

    // Content
    $mail->isHTML(true);
    $mail->Subject = 'Email Verification Code';
    $mail->Body = "Your verification code is: <b>$verification_code</b>";

    $mail->send();

    // Clear output buffer to discard any output from PHPMailer
    ob_clean();

    // Hash the password
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

    // Insert the new user into the unverified_users table
    $stmt = $pdo->prepare("INSERT INTO unverified_users (email, password, name, phone_num, verification_code) VALUES (:email, :password, :name, :phone_num, :verification_code)");

    if ($stmt->execute(['email' => $email, 'password' => $hashedPassword, 'name' => $name, 'phone_num' => $phone_num, 'verification_code' => $verification_code])) {
        $response['success'] = 1;
        $response['message'] = 'Registration successful. Please verify your email.';
    } else {
        $response['message'] = 'Registration failed. Please try again.';
    }

} catch (Exception $e) {
    $response['message'] = 'An error occurred: ' . $e->getMessage();
}

// Output response
echo json_encode($response);
?>
