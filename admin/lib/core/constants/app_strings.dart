/// Centralized UI String Constants for Open Fashion Admin
abstract class AppStrings {
  // Brand & Console
  static const String appName = 'OPEN FASHION';
  static const String brandTagline = 'LUXURY ADMIN CONSOLE';
  static const String copyright = '© 2026 Open Fashion Ecosystem. All Rights Reserved.';

  // Auth Screen Titles & Subtitles
  static const String welcomeBack = 'Welcome Back';
  static const String loginSubtitle = 'Enter your executive credentials to access the console';
  static const String createAccount = 'Create Account';
  static const String registerSubtitle = 'Set up a new administrator or manager account';
  static const String resetPassword = 'Reset Password';
  static const String resetPasswordSubtitle = 'Enter your email address to receive recovery instructions';
  static const String setNewPassword = 'Set New Password';
  static const String setNewPasswordSubtitle = 'Enter the 6-digit OTP or token and choose your new password';

  // Form Fields & Labels
  static const String fullName = 'Full Name';
  static const String fullNameHint = 'Eleanor Vance';
  static const String emailAddress = 'Email Address';
  static const String emailHint = 'admin@openfashion.com';
  static const String password = 'Password';
  static const String passwordHint = '••••••••••••';
  static const String newPassword = 'New Password';
  static const String newPasswordHint = 'Minimum 8 characters with symbol';
  static const String confirmPassword = 'Confirm New Password';
  static const String confirmPasswordHint = 'Repeat new password';
  static const String phoneNumber = 'Phone Number (Optional)';
  static const String phoneHint = '+1234567890';
  static const String otpOrToken = 'Verification OTP or Token';
  static const String otpHint = 'Enter 6-digit OTP or reset token';

  // Buttons & Actions
  static const String signInToConsole = 'SIGN IN TO CONSOLE';
  static const String createAccountButton = 'CREATE ADMIN ACCOUNT';
  static const String sendRecoveryCode = 'SEND RECOVERY CODE';
  static const String updatePassword = 'UPDATE PASSWORD';
  static const String forgotPassword = 'Forgot Password?';
  static const String backToSignIn = 'Back to Sign In';
  static const String requestDifferentCode = 'Request a different code';
  static const String saveChanges = 'Save Changes';
  static const String cancel = 'Cancel';
  static const String signOut = 'Sign Out';

  // Validation Error Messages
  static const String fullNameRequired = 'Full name is required';
  static const String fullNameTooShort = 'Full name must be at least 2 characters';
  static const String emailRequired = 'Email address is required';
  static const String emailInvalid = 'Please enter a valid email address';
  static const String passwordRequired = 'Password is required';
  static const String passwordTooShort = 'Password must be at least 8 characters';
  static const String passwordMismatch = 'Passwords do not match';
  static const String tokenRequired = 'Reset token or OTP is required';

  // Profile & Navigation
  static const String profile = 'Admin Profile';
  static const String roleAdministrator = 'ADMIN';
  static const String roleManager = 'MANAGER';
  static const String editProfile = 'Edit Profile';
  static const String profileUpdatedSuccess = 'Profile updated successfully!';
}
