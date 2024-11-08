function processMetaData(metaData) {
    let newMetaData = {};
    for (let i = 0; i < metaData.length; i++) {
        let colInfo = metaData[i];
        let colAndTypePair = [];
        if (!newMetaData.hasOwnProperty(colInfo["tableName"])) {
            newMetaData[colInfo.tableName] = [];
        }
        colAndTypePair[0] = colInfo.columnName;
        colAndTypePair[1] = colInfo.dataType;
        newMetaData[colInfo.tableName].push(colAndTypePair);
    }

    return newMetaData;
}

function handleMetadataResult(resultData) {
    let metaDataElement = jQuery("#metadata");
    let processedData = processMetaData(resultData);
    console.log(processedData);
    for (let tableName in processedData) {
        let rowHTML = "";
        let tableColumns = processedData[tableName];
        rowHTML += "<p>" + tableName + "</p>";
        rowHTML += "<table>";
        rowHTML += "<tr><th>Attribute</th><th>Type</th></tr>";
        console.log(tableColumns);
        for (let i = 0; i < tableColumns.length; i++) {
            let column = tableColumns[i];
            rowHTML += "<tr>";
            rowHTML += "<td>" + column[0] + "</td>";
            rowHTML += "<td>" + column[1] + "</td>";
            rowHTML += "</tr>";
        }
        rowHTML += "</table>";

        metaDataElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/dashboard",
    success: (resultData) => handleMetadataResult(resultData)
})
