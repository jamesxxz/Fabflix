function handleResult() {
    const allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart"));
    console.log(allMoviesInCart);

    let cartTableElement = jQuery('#cart_table_body');
    let totalPriceElement = jQuery('#total_price');
    let totalMoviePrice = 0;
    for (const [movieTitle, movieQuantity] of Object.entries(allMoviesInCart)) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + movieTitle + "</th>";
        rowHTML += `<th style="display: flex; gap: 20px"><a style="background-color: white; color: black; border-radius: 5px" onclick="addMovies('${movieTitle}')">&nbsp;+&nbsp;</a>`
            + movieQuantity
            + `<a style="background-color: white; color: black; border-radius: 5px" onclick="minusMovies('${movieTitle}')">&nbsp;-&nbsp;</a></th>`;
        rowHTML += "<th>" + "$20" + "</th>";
        rowHTML += "<th>" + "$" + movieQuantity * 20 + "</th>";
        rowHTML += `<th><button onclick="deleteMovie('${movieTitle}')">Delete</button></th>`;
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        cartTableElement.append(rowHTML);
        totalMoviePrice += movieQuantity * 20
    }
    totalPriceElement.text("Total Price: $" + totalMoviePrice);

}

function deleteMovie(movieTitle) {
    let allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart"));
    const {[movieTitle]: _, ...newAllMovies} = allMoviesInCart;
    console.log(newAllMovies);
    sessionStorage.setItem("moviesInCart", JSON.stringify(newAllMovies));
    window.location.reload();
}

function addMovies(movieTitle) {
    let allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart"));
    allMoviesInCart[movieTitle] += 1;
    sessionStorage.setItem("moviesInCart", JSON.stringify(allMoviesInCart));
    window.location.reload();
}

function minusMovies(movieTitle) {
    let allMoviesInCart = JSON.parse(sessionStorage.getItem("moviesInCart"));
    if (allMoviesInCart[movieTitle] > 1) {
        allMoviesInCart[movieTitle] -= 1;
        sessionStorage.setItem("moviesInCart", JSON.stringify(allMoviesInCart));
        window.location.reload();
    }
}

function handleToPaymentPage() {
    $('#toPaymentBtn').click(function () {
        window.location.replace("payment.html")
    })
}

handleResult();
handleToPaymentPage();

// $.ajax({
//     dataType: "json",
//     method: "GET",
//     url: "api/shopping-cart",
//     success: (resultData) => handleResult(resultData)
// });