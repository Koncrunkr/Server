
function postCreateTable(){
    fetch(
        "/table/create",
        {
            method: "POST",
            credentials: "include",
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
        html => $("#responses").append("<tr><td>" + html + "</td></tr>")
    )
}

function getUserInfo(){
    fetch(
        "/user/info?includeChats=" + ($("#includeChats").val() === "true"),
        {
            method: "GET",
            credentials: "include",
            headers: {"Content-Types": "application/json"}
        }
    ).then(
        response => response.text()
    ).then(
        html => $("#responses").append("<tr><td>" + html + "</td></tr>")
    )
}

function sendRequestAddPerson() {
    fetch(
        "/table/participant",
        {
            method: "POST",
            credentials: "include",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                "chatId": parseInt($("#chatId").val()),
                "userId": $("#userId").val(),
            })
        }
    ).then(
        respone => {
            $("#responses").append("<tr><td>" + "Status: " + respone.status + ", " + respone.text() + "</td></tr>");
        }
    )
}

$(function (){
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#send_table").click(function() { postCreateTable(); });
    $("#send_info").click(function() { getUserInfo(); });
    $("#send_request_add_person").click(function () {sendRequestAddPerson()})
})