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

async function sortStarsByMoviesPlayed(starsArr, starIdsArr) {
    let res = [];
    const promises = starIdsArr.map((starId, index) => {
        return $.ajax({
            dataType: "json",
            method: "GET",
            url: `api/single-star?id=${starId}`,
        }).then((resultData) => {
            let movies = resultData[0]["movie_title"].split(", ");
            res.push({"star": starsArr[index], "starId": starId, "moviePlayed": movies.length});
        }).catch((error) => {
            console.error(`Error fetching data for star ID ${starId}:`, error);
        });
    });

    // Wait for all AJAX calls to complete
    await Promise.all(promises);

    res.sort((a, b) => {
        if (a.moviePlayed > b.moviePlayed) {
            return -1;
        } else if (a.moviePlayed < b.moviePlayed) {
            return 1;
        } else {
            return a.star > b.star;
        }
    })

    return res;
}

async function processStars(starsString, starIdsString) {
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

    const sortedStarsInfo = await sortStarsByMoviesPlayed(starsArr, starIdsArr);
    const anchorTags = sortedStarsInfo.map((item) => {
        return `<a href="single-star.html?id=${item.starId}">${item.star}</a>`;
    });

    return anchorTags.join(', ');
}

function processGenres(genreString) {
    const genresArr = genreString.split(', ');

    const anchorTags = genresArr.map((genre, index) => {
        return `<a href="movielist.html?num=10&page=1&sort=r0t1&input=genre:${genre}">${genre}</a>`;
    });
    return anchorTags.join(', ');
}

function handleReturnToPrevMovieList() {
    $("#return-to-movies-btn").click(function () {
            window.location.href = sessionStorage.getItem("prevMovieListURL");
        }
    );
}


async function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData!!");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"
    let movieInfoElement = jQuery("#movie_info");

    const starAnchors = await processStars(resultData[0]["stars"], resultData[0]["star_ids"])

    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_yr"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>All Genres: " + processGenres(resultData[0]["genres"]) + "</p>" +
        "<p>All Stars: " + starAnchors + "</p>" +
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

handleReturnToPrevMovieList();