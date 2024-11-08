let addStarForm = $("#addStarForm");


function handleAddStar(submitEvent) {
    submitEvent.preventDefault();

    $.ajax(
        "api/add", {
            method: "POST",
            data: addStarForm.serialize(),
            success: handleAddResult
        }
    );
}

function handleAddResult(statusJson) {
    alert(statusJson["message"]);
    $("#starName").val("");
    $("#birthYear").val("");
}

addStarForm.submit(handleAddStar)