/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


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

function handleResult(resultData) {
    // 在控制台打印日志，用于调试，显示正在从 resultData 填充明星信息
    console.log("handleResult: populating star info from resultData!");

    console.log(resultData);
    // 获取 HTML 页面中 id 为 "star_info" 的元素，用于填充明星信息
    let starInfoElement = jQuery("#star_info");

    // 向 "star_info" 元素中追加两段 HTML <p> 标签，分别显示明星的姓名和出生日期
    // resultData[0] 表示从服务器获取的第一个数据对象
    // "star_name" 是明星的名字，"star_dob" 是明星的出生日期（如果没有，则为 N/A）
    if (resultData.length > 0) {
        starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
            "<p>Year Of Birth: " + resultData[0]["star_dob"] + "</p>");
    } else {
        starInfoElement.append("<p>No data available for this star.</p>");
    }

    // 在控制台打印日志，用于调试，显示正在填充电影表格
    console.log("handleResult: populating movie table from resultData");

    // 获取 HTML 页面中 id 为 "movie_table_body" 的表格元素，用于填充电影信息
    let movieTableBodyElement = jQuery("#movie_table_body");

    // 从 resultData[0] 获取所有电影的标题，使用逗号加空格（", "）分隔，并将其拆分为数组
    let movies = resultData[0]["movie_title"].split(", ");

    // 从 resultData[0] 获取所有电影的 ID，同样使用逗号加空格分隔，拆分为数组
    let movieIds = resultData[0]["movie_ids"].split(", ");
    //let movies = resultData[0]["movies"] ? resultData[0]["movies"].split(", ") : [];
    //let movieIds = resultData[0]["movie_ids"] ? resultData[0]["movie_ids"].split(", ") : [];

    // 循环遍历 movies 数组中的每一个电影
    for (let i = 0; i < movies.length; i++) {
        // 创建一个新的 HTML 表格行 <tr>，每个电影将作为一个新行加入到表格中
        let rowHTML = "<tr>";

        // 第一个单元格显示明星的名字
        //rowHTML += "<td>" + resultData[0]["star_name"] + "</td>";

        // 第二个单元格显示明星的出生年份（或 N/A）
        //rowHTML += "<td>" + resultData[0]["star_dob"] + "</td>";

        // 第三个单元格显示电影的名字，并将电影名设为超链接，点击时跳转到对应的 Single Movie 页面
        // 使用 movieIds[i] 创建超链接，链接到 single-movie.html 页面，并附带电影 ID 作为参数
        rowHTML += "<td><a href='single-movie.html?id=" + movieIds[i] + "'>" + movies[i] + "</a></td>";

        // 结束当前表格行
        rowHTML += "</tr>";

        // 将生成的表格行追加到表格的主体部分 movieTableBodyElement 中，刷新页面内容
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});