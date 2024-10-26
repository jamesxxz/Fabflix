$(document).ready(function() {

    let cartItems = JSON.parse(sessionStorage.getItem("shoppingCart")) || [];
    let totalPrice = 0;


    cartItems.forEach(item => {
        totalPrice += item.price * item.quantity;
    });


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
    if (!expDate) {
        $("#payment_error_message").text("Expiration date is required.");
        return;
    }

    // 打印 expDate 以确保格式正确
    console.log("Expiration Date:", expDate);


    $.ajax({
        url: "api/payment",
        method: "POST",
        data: payment_form.serialize(),
        success: handlePaymentResult
    });
}

payment_form.submit(submitPaymentForm);
