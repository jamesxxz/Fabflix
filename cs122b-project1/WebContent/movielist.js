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
        return '<a href="single-star.html?id=' + starIdsArr[index] + '">' + star + '</a>';
    });

    return anchorTags.join(', ');
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

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
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>" + processStars(resultData[i]["stars"], resultData[i]["starIds"]) + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// 通过 AJAX 请求获取电影列表数据
function fetchMovieList(params = "") {
    const url = params ? `api/movies?${params}` : "api/movies";
    console.log(url);

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: url,
        success: handleMoviesResult,
    });
}

function handleNumMoviesChange() {
    $('#numMovies').change(function () {
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
    $('#prev')
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
    const page = getParameterByName('page') || 1;
    const sort = getParameterByName('sort') || 'r0t1';
    const input = getParameterByName('input') || '';

    $('#numMovies').val(num);

    fetchMovieList(`num=${num}&page=${page}&sort=${sort}&input=${input}`);

    handleNumMoviesChange();
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

