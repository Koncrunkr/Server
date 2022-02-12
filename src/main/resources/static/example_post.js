
function postCreateTable(){
    fetch(
        "http://localhost:8080/table/create",
        {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                "name": $("#name").val(),
                "avatar": $("#avatar").val(),
                "width": parseInt($("#width").val()),
                "height": parseInt($("#height").val())
            })
        }
    ).then(
        response => response.text()
    ).then(
        html => $("#greetings").append("<tr><td>" + html + "</td></tr>")
    )
}

$(function (){
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#send").click(function() { postCreateTable(); });
})