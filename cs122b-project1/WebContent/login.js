let loginForm = jQuery("#loginForm")

function handleSuccessLogin(resData) {
    const resultData = JSON.parse(resData);

    if (resultData.redirect) {
        console.log("success");
        window.location.replace("index.html");
    }
}

function handleLoginSubmit(logInEvent) {

    logInEvent.preventDefault();  // prevent redirect of page

    jQuery.ajax({
        method: "POST",
        url: "api/login",
        data: loginForm.serialize(),
        success: (resData) => handleSuccessLogin(resData)
    })
}

loginForm.submit(handleLoginSubmit)