let addStarForm = $("#addStarForm");
let addMovieForm = $("#addMovieForm");

function handleAddStar(submitEvent) {
    submitEvent.preventDefault();

    $.ajax(
        "api/add", {
            method: "POST",
            data: addStarForm.serialize(),
            success: (statusJson) => handleAddStarResult(statusJson)
        }
    );
}

function handleAddStarResult(statusJson) {
    alert(statusJson["message"]);
    $("#starName").val("");
    $("#birthYear").val("");
}

function handleAddMovie(submitEvent) {
    submitEvent.preventDefault();
    console.log(addMovieForm.serialize());
    $.ajax(
        "api/add", {
            method: "POST",
            data: addMovieForm.serialize(),
            success: (statusJson) => handleAddMovieResult(statusJson)
        }
    );
}

function handleAddMovieResult(statusJson) {
    alert(statusJson["message"]);
    $("#movieTitle").val("");
    $("#movieYear").val("");
    $("#director").val("");
    $("#star").val("");
    $("#genreName").val("");
}


addStarForm.submit(handleAddStar);
addMovieForm.submit(handleAddMovie);
