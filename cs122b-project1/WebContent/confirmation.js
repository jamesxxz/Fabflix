// Function to handle the result from the /api/confirmation endpoint
function handleResult(resultData) {
    console.log("handleResult: confirmation page");
    console.log(resultData);

    let allMoviesPurchased = JSON.parse(sessionStorage.getItem("moviesInCart"));

    // Select the element where order details will be displayed
    let orderDetailsElement = jQuery("#orderDetails");

    // Build the order details string with Sale ID and table headers
    let orderDetails = "";
    orderDetails += `<table>
                        <tr>
                            <th>Sales ID</th>
                            <th>Movie Title</th>
                            <th>Quantity</th>
                            <th>Price</th>
                        </tr>`;

    // Initialize total price
    let totalPrice = 0;

    // Iterate over the result data to build the table rows
    for (let i = 0; i < resultData.length; i++) {
        let movieTitle = resultData[i].movieTitle;
        let quantity = allMoviesPurchased[movieTitle];
        let pricePerMovie = 20; // Fixed price per movie (adjust if needed)
        let price = quantity * pricePerMovie;
        totalPrice += price;

        // Append each movie's details as a new row in the table
        orderDetails += `<tr>
                            <td>${resultData[i].salesId}</td>
                            <td>${movieTitle}</td>
                            <td>${quantity}</td>
                            <td>$${price}</td>
                         </tr>`;
    }

    // Close the table and add the total price
    orderDetails += `</table>`;
    orderDetails += `<h4>Total Price: $${totalPrice}</h4>`;

    // Set the built HTML content inside the order details element
    orderDetailsElement.html(orderDetails);

    sessionStorage.clear();
}

// Perform an AJAX GET request to fetch the confirmation data
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation", // URL of the endpoint
    success: (resultData) => handleResult(resultData), // Callback to handle the response
    error: (xhr, status, error) => {
        console.error("Failed to fetch confirmation data:", error);
        alert("Unable to retrieve order confirmation. Please try again.");
    }
});