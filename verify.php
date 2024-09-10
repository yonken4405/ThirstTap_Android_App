<?php
require 'db.php';

// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Initialize response array
$response = ['success' => 0, 'message' => ''];

// Get the verification code from POST
$verification_code = $_POST['verification_code'] ?? '';

// Validate input
if (empty($verification_code)) {
    $response['message'] = 'Verification code is required.';
    echo json_encode($response);
    exit;
}

// Check if the verification code is valid in the unverified_users table
$stmt = $pdo->prepare("SELECT * FROM unverified_users WHERE verification_code = :verification_code");
$stmt->execute(['verification_code' => $verification_code]);

if ($stmt->rowCount() > 0) {
    // Get the user data
    $user = $stmt->fetch();

    // Now check if the verification code matches the email
    // Assuming you are sending the email in the POST request too
    $email = $_POST['email'] ?? '';

    if ($user['email'] === $email) {
        // Move the user data to the main user_data table
        $stmt = $pdo->prepare("INSERT INTO user_data (email, password, name, phone_num) VALUES (:email, :password, :name, :phone_num)");
        if ($stmt->execute([
            'email' => $user['email'],
            'password' => $user['password'],
            'name' => $user['name'],
            'phone_num' => $user['phone_num']
        ])) {
            // Delete from unverified_users table
            $stmt = $pdo->prepare("DELETE FROM unverified_users WHERE email = :email");
            $stmt->execute(['email' => $user['email']]);

            $response['success'] = 1;
            $response['message'] = 'Email verified successfully.';
        } else {
            $response['message'] = 'Verification failed. Please try again.';
        }
    } else {
        $response['message'] = 'Verification code does not match the email.';
    }
} else {
    $response['message'] = 'Invalid verification code.';
}

// Output response
echo json_encode($response);
?>
