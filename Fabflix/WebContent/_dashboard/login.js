let loginForm = $("#loginForm");

function handleSuccessLogin(resData) {
    const resultData = JSON.parse(resData);

    if (resultData["status"] === "success") {
        window.location.replace("dashboard.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        $("#login_error_message").text(resultData["message"]);
    }

}

function handleLoginSubmit(logInEvent) {

    logInEvent.preventDefault();  // prevent redirect of page
    let recaptchaResponse = grecaptcha.getResponse();
    if (!recaptchaResponse) {
        alert("Please complete the reCAPTCHA.");
        return;
    }
    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: loginForm.serialize() + "&g-recaptcha-response=" + recaptchaResponse,
            success: handleSuccessLogin
        }
    );
}

loginForm.submit(handleLoginSubmit)