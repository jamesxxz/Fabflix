// 绑定搜索表单
let search = $("#search");

// 处理类别和字母导航的结果
function handleGenreResult(resultData) {
    console.log("Populating genre and alpha lists");

    populateList("#genre_body", resultData, "genre");
    populateAlphaList("#alpha_body");
}

// 填充类别列表
function populateList(containerId, data, key) {
    let container = $(containerId);
    data.forEach((item) => {
        container.append(`<a href="movielist.html?num=10&page=1&sort=r0t1&input=${key}:${item[key]}">${item[key]}</a>`);
    });
}

// 填充字母导航列表
function populateAlphaList(containerId) {
    let container = $(containerId);
    for (let i = 0; i <= 9; i++) {
        container.append(`<a href="movielist.html?num=10&page=1&sort=r0t1&input=alpha:${i}">${i}</a>`);
    }
    for (let i = 65; i <= 90; i++) {
        let letter = String.fromCharCode(i);
        container.append(`<a href="movielist.html?num=10&page=1&sort=r0t1&input=alpha:${letter}">${letter}</a>`);
    }
    container.append('<a href="movielist.html?num=10&page=1&sort=r0t1&input=alpha:*">*</a>');
}

function handleSearch(event) {
    console.log("Search form submitted.");
    event.preventDefault();
    console.log("Submitting search form");

    $.ajax({
        method: "POST",
        url: "api/index",
        data: search.serialize(),
        success: handleSearchResult,
    });
}

function handleSearchResult(resultDataString) {
    let resultData = JSON.parse(resultDataString);
    let newURL = "input=";

    if (resultData["sort_title"]) {
        newURL += "title:" + resultData["sort_title"] + ":";
    }
    if (resultData["sort_year"]) {
        newURL += "year:" + resultData["sort_year"] + ":";
    }
    if (resultData["sort_director"]) {
        newURL += "director:" + resultData["sort_director"] + ":";
    }
    if (resultData["sort_name"]) {
        newURL += "name:" + resultData["sort_name"] + ":";
    }
    console.log(newURL);
    console.log("Redirecting to:", `movielist.html?num=10&page=1&sort=r0t1&${newURL}`);
    window.location.replace(`movielist.html?num=10&page=1&sort=r0t1&${newURL}`);
}


// 获取初始数据并填充导航
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/index",
    success: handleGenreResult,
});

// 绑定搜索表单提交事件
console.log("Binding search form submit event.");
search.submit(handleSearch);

//
