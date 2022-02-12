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
        console.log('Connected: ' + frame);
        const chatId = 1;
        stompClient.subscribe('/app/table/queue/' + chatId, function (greeting) {
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
                xCoord: parseInt($("#xCoord").val()),
                yCoord: parseInt($("#yCoord").val()),
                chatId: 1,
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