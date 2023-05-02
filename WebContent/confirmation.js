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
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    handleCartArray(resultDataJson["previousMovies"]);
    clearCartArray(resultDataJson["previousMovies"]);

}


function clearCartArray(resultArray){
    for (let i = 0; i < resultArray.length; i++) {
        const values = resultArray[i].split("%$%");

        $.ajax("api/index", {
            method: "POST",
            data: {
                title: values[0],
                add: "REMOVE"
            },

        });

    }
}
function handleCartArray(resultArray) {
    let item_list = $("#item_list");
    let total_cost = $("#total_cost");
    let sale = $("#sale");
    let id = getParameterByName("saleID")
    // change it to html list
    let res = "";
    let tot = 0;
    for (let i = 0; i < resultArray.length; i++) {
        const values = resultArray[i].split("%$%");
        let cost = parseInt(values[1]) * 5
        tot += cost;
        res += "<tr><td>" + values[0] + "</td>";
        res += "<td>" + values[1] + "</td>";
        res += "<td style='text-align: right'>$"+ cost +"</td></tr>";
    }

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);

    tot = "Total Cost: $" + tot.toString();
    total_cost.html("");
    total_cost.append(tot);

    id = "SaleID: " + id.toString();
    sale.html("");
    sale.append(id);


}

$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});
