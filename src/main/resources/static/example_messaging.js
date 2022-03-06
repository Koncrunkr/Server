var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        $("#stomp").append("<tr><td>" + frame + "</td></tr>")
        const chatId = $("#chatId").val();
        stompClient.subscribe("/connection/table_message/" + chatId, function (greeting) {
            showMessage(greeting.body);
        });

        fetch(
            "/user/info",
            {
                method: "GET",
                credentials: "include",
                headers: {"Content-Types": "application/json"}
            }
        ).then(
            response => response.text()
        ).then(
            text => {
                console.log(JSON.parse(text));
                console.log(text);
                stompClient.subscribe("/connection/user/" + JSON.parse(text).id, function (greeting) {
                    showMessage(greeting.body);
                })
            }
        )

    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.send("/connection/table_message", {},
        JSON.stringify(
            {
                x: $("#xCoord").val(),
                y: $("#yCoord").val(),
                chatId: $("#chatId").val(),
                text: $("#text").val()
            }
        )
    );
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#send-message").click(() => sendMessage());
    $("#connect").click(function() { connect(); });
    $("#disconnect").click(function() { disconnect(); });
});