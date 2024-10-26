function handleResult() {
    const allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart"));
    console.log(allMoviesInCart);

    let cartTableElement = jQuery('#cart_table_body');

    for (const [movieTitle, movieQuantity] of Object.entries(allMoviesInCart)) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + movieTitle + "</th>";
        rowHTML += "<th>" + movieQuantity + "</th>";
        rowHTML += "<th>" + "$20" + "</th>";
        rowHTML += "<th>" + "$" + movieQuantity * 20 + "</th>";
        rowHTML += `<th><button>Delete</button></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableElement.append(rowHTML);
    }
}

handleResult();

// $.ajax({
//     dataType: "json",
//     method: "GET",
//     url: "api/shopping-cart",
//     success: (resultData) => handleResult(resultData)
// });
