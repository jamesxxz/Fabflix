/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function processStars(starsString, resultData, i) {
    const starsArr = starsString.split(', ');
    const anchorTags = starsArr.map(star => {
        return `<a href="single-star.html?id=${resultData[i]['starId']}">${star}</a>`;
    });
    return anchorTags.join(', ');
}

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
                            "<p>Release Year: " + resultData[0]["movie_yr"] + "</p>" +
                            "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
                            "<p>All Genres: " + resultData[0]["genres"] + "</p>" +
                            "<p>All Stars: " + processStars(resultData[0]["stars"]) + "</p>" +
                            "<p>Rating: " + resultData[0]["rating"] + "</p>"
    );

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});