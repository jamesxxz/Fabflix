// $(document).ready(function() {
//
//     let cartItems = JSON.parse(sessionStorage.getItem("moviesInCart")) || [];
//     let totalPrice = 0;
//
//
//     cartItems.forEach(item => {
//         totalPrice += item.price * item.quantity;
//     });
//
//
//     $("#totalPrice").text(`$${totalPrice.toFixed(2)}`);
// });
let movieTitles = [];

$(document).ready(function () {
    const allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart")) || {};
    let totalPrice = 0;

    for (const [movieTitle, movieQuantity] of Object.entries(allMoviesInCart)) {
        totalPrice += movieQuantity * 20;
        movieTitles.push(movieTitle);
    }


    $("#totalPrice").text(`$${totalPrice.toFixed(2)}`);
});


let payment_form = $("#payment_form");

function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle payment response", resultDataJson);

    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {

        console.log("show error message", resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

function submitPaymentForm(formSubmitEvent) {
    console.log("submit Payment form");
    formSubmitEvent.preventDefault();
    let expDate = $("#expDate").val();
    console.log("Expiration Date from input:", expDate);
    if (!expDate) {
        $("#payment_error_message").text("Expiration date is required.");
        return;
    }

    // 打印 expDate 以确保格式正确
    console.log("Expiration Date:", expDate);


    $.ajax({
        url: "api/payment",
        method: "POST",
        data: payment_form.serialize() + `&movieTitles=${movieTitles}`,
        success: handlePaymentResult
    });
}

payment_form.submit(submitPaymentForm);
