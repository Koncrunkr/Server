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
    const socket = new SockJS('/messaging');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        $("#stomp").append("<tr><td>" + frame + "</td></tr>")
        const chatId = $("#chatId").val();
        stompClient.subscribe("/app/table/queue/" + chatId, function (greeting) {
            showMessage(greeting.body);
        });
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
    stompClient.send("/app/table", {},
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