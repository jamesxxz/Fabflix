/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let numMovies = $("#numMovies");

async function sortStarsByMoviesPlayed(starsArr, starIdsArr) {
    let res = [];
    const promises = starIdsArr.map((starId, index) => {
        return $.ajax({
            dataType: "json",
            method: "GET",
            url: `api/single-star?id=${starId}`,
        }).then((resultData) => {
            let movies = resultData[0]["movie_title"].split(", ");
            res.push({ "star": starsArr[index], "starId": starId, "moviePlayed": movies.length });
        }).catch((error) => {
            console.error(`Error fetching data for star ID ${starId}:`, error);
        });
    });

    await Promise.all(promises);

    // Sort stars: first by movie count (desc), then alphabetically
    res.sort((a, b) => {
        if (a.moviePlayed > b.moviePlayed) return -1;
        if (a.moviePlayed < b.moviePlayed) return 1;
        return a.star.localeCompare(b.star);
    });

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


/**
 * Processes genres and returns HTML with hyperlinks for genre-based browsing.
 */
function processGenres(genreString) {
    const genresArr = genreString.split(', ');

    const anchorTags = genresArr.map((genre) => {
        return `<a href="movielist.html?num=10&page=1&sort=r0t1&input=genre:${genre}">${genre}</a>`;
    });

    return anchorTags.join(', ');
}
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
async function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");
    console.log("Received resultData:", resultData);  // 新增的日志

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' +
            resultData[i]["movie_title"] +      // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_yr"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //rowHTML += `<th>${processGenres(resultData[i]["genres"])}</th>`;
        const genresHTML = processGenres(resultData[i]["genres"]) || 'N/A';
        rowHTML += `<th>${genresHTML}</th>`;
        const starAnchors = await processStars(resultData[i]["stars"], resultData[i]["starIds"])||'NA';
        rowHTML += `<th>${starAnchors}</th>`;
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    sessionStorage.setItem("prevMovieListURL", window.location.href);
}

// 通过 AJAX 请求获取电影列表数据
function fetchMovieList(params = "") {
    const url = params ? `api/movies?${params}` : "api/movies";
    console.log("Fetching movie list from:", url);


    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: url,
        success: (resultData) => {
            console.log("Successfully received data:", resultData);
            handleMoviesResult(resultData);
        },
        error: (jqXHR, textStatus, errorThrown) => {
            console.error("AJAX request failed:", textStatus, errorThrown);
            console.error("Response:", jqXHR.responseText);
        }
    });
}

function handleNumMoviesChange() {
    numMovies.change(function () {
        const selectedNums = $(this).val();
        const urlObj = new URL(window.location.href);
        const params = new URLSearchParams(urlObj.search);
        params.set('num', selectedNums);
        urlObj.search = params.toString();
        const updatedUrl = urlObj.toString();
        window.location.replace(updatedUrl);
    });
}

function handlePrevNextPagination() {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    const curPage = getParameterByName('page')
    $('#prev').click(function () {
        if (parseInt(curPage) - 1 === 0) {
            params.set('page', "1");
        } else {
            params.set('page', (parseInt(curPage) - 1).toString());
            url.search = params.toString();
            const updatedUrl = url.toString();
            window.location.replace(updatedUrl);
        }
    });
    $('#next').click(function () {
        params.set('page', (parseInt(curPage) + 1).toString());
        url.search = params.toString();
        const updatedUrl = url.toString();
        window.location.replace(updatedUrl);
    });
}

// 初始化搜索和导航逻辑

function initializePage() {

    // 渲染导航链接
    const homeElement = $("#home");
    // homeElement.append('<li><a href="index.html">Home</a></li>');
    // homeElement.append('<li><a href="shopping-cart.html">Check Out</a></li>');
    // homeElement.append('<li><a href="login.html">Log Out</a></li>');

    // 获取 URL 中的参数，并加载对应的电影列表
    const num = getParameterByName('num') || 10;
    let page = getParameterByName('page') || 1;
    const sort = getParameterByName('sort') || 'r0t1';
    const input = getParameterByName('input') || '';

    numMovies.val(num);

    fetchMovieList(`num=${num}&page=${page}&sort=${sort}&input=${input}`);

    handleNumMoviesChange();
    handlePrevNextPagination();
}

// 获取 URL 参数
function getParameterByName(name) {
    const url = new URL(window.location.href);
    return url.searchParams.get(name);
}

// 初始化页面
initializePage();

//
// /**
//  * Once this .js is loaded, following scripts will be executed by the browser
//  */
//
// // Makes the HTTP GET request and registers on success callback function handleMoviesResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "api/movies", // Setting request url, which is mapped by MoivesServlet in Movies.java
//     success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the MoivesServlet
// });

