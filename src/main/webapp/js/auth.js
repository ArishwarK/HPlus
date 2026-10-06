/**
 * AUTH JAVASCRIPT - Demo Credentials Quick-Fill
 */

function fillCredentials(username, password) {
  const userInput = document.getElementById("username");
  const passInput = document.getElementById("password");
  if (userInput && passInput) {
    userInput.value = username;
    passInput.value = password;
    userInput.focus();
  }
}
