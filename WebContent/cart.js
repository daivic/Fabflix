
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
    let checkout = $("#checkout");
    // change it to html list
    let res = "";
    let tot = 0;
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        const values = resultArray[i].split("%$%");
        let cost = parseInt(values[1]) * 5
        tot += cost;
        res += "<tr style='text-align: left'><td>" + values[0] + "</td>";
        res += "<td>";
        res += "<a onclick='changeAmount(\""+values[0]+"\")'><i class=\"fa fa-arrow-up\"></i></a>";
        res += values[1];
        res += "<a onclick='changeAmount(\""+values[0]+"\", \"DELETE\")'><i class=\"fa fa-arrow-down\"></i></a>";

        res += "<td style='text-align: right'>$"+ cost +"</td>";
        res += "<td><a style='text-align: right' onclick='changeAmount(\""+values[0]+"\", \"REMOVE\")'><i class=\"fa fa-trash\"></i></a></td></tr>";



    }

    console.log(res);
    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);

    checkout.html("");
    checkout.append("<button style='color: green' onclick=\"window.location.href = 'checkout.html?p="+tot.toString()+"';\">Checkout: $"+tot+"</button>")
}

$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
