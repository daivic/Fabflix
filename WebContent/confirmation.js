
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
    // change it to html list
    let res = "";
    let tot = 0;
    for (let i = 0; i < resultArray.length; i++) {
        const values = resultArray[i].split("%$%");
        let cost = parseInt(values[1]) * 5
        tot += cost;
        res += "<tr><td>" + values[0] + "</td>";
        res += "<td>" + values[1] + "</td>";
        res += "<td>"+ cost +"</td></tr>";
    }

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);

    total_cost.html("");
    total_cost.append(tot);

}



$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});
