
function changeAmount(title, add){
    console.log(add)
    $.ajax("api/index", {
        method: "POST",
        data: {
            title: title,
            add: add
        },

    });
    window.location.reload();
}
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    handleCartArray(resultDataJson["previousMovies"]);
}

function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    let total_cost = $("#total_cost");
    let checkout = $("#checkout");
    // change it to html list
    let res = "";
    let tot = 0;
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        const values = resultArray[i].split("%$%");
        let cost = parseInt(values[1]) * 5
        tot += cost;
        res += "<tr><td>" + values[0] + "</td>";
        res += "<td>" + values[1];
        res += "<button onclick='changeAmount(\""+values[0]+"\")'>ADD</button>";
        res += "<button onclick='changeAmount(\""+values[0]+"\", \"DELETE\")'>REM</button>";
        res += "<button onclick='changeAmount(\""+values[0]+"\", \"REMOVE\")'>CLEAR</button></td>";

        res += "<td>"+ cost +"</td></tr>";


    }

    console.log(res);
    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);

    total_cost.html("");
    total_cost.append(tot);

    checkout.html("");
    checkout.append("<button onclick=\"window.location.href = 'checkout.html?p="+tot.toString()+"';\">Checkout</button>")
}

$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
