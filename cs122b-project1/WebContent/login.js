

function handleSuccessLogIn(response) {
    if (response.success) {
        console.log("success")
        window.location.href = "index.html";
    }
}