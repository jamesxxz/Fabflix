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
function processStars(starsString, starIdsString) {
    const starsArr = starsString.split(', ');

    // 如果 starIdsString 是 undefined 或 null，进行处理
    if (!starIdsString) {
        console.error("starIdsString is undefined or null.");
        return starsArr.join(', ');  // 如果没有 star_ids，则只显示明星名字，不生成超链接
    }

    const starIdsArr = starIdsString.split(', ');

    // 确保明星数量和ID数量匹配
    if (starsArr.length !== starIdsArr.length) {
        console.error("Stars and Star IDs count do not match.");
        return starsArr.join(', ');
    }

    const anchorTags = starsArr.map((star, index) => {
        return `<a href="single-star.html?id=${starIdsArr[index]}">${star}</a>`;
    });

    return anchorTags.join(', ');
}



function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData!!");
    console.log(resultData[0]);
    //console.log(resultData[0]["star_ids"]);
    console.log("Star IDs: ", resultData[0]["star_ids"]);

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");

    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_yr"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>All Genres: " + resultData[0]["genres"] + "</p>" +
        "<p>All Stars: " + processStars(resultData[0]["stars"], resultData[0]["star_ids"]) + "</p>" +
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