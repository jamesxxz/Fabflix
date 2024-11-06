let loginForm = $("#loginForm");

function handleSuccessLogin(resData) {
    const resultData = JSON.parse(resData);

    if (resultData["status"] === "success") {
        window.location.replace("index.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        $("#login_error_message").text(resultData["message"]);
    }

}

function handleLoginSubmit(logInEvent) {

    logInEvent.preventDefault();  // prevent redirect of page

    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: loginForm.serialize(),
            success: handleSuccessLogin
        }
    );
}

loginForm.submit(handleLoginSubmit)