'use strict';

var nameInput = $('#name');
var roomInput = $('#room-name');
var changeRoomInput = $('#newRoomName');
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdDisplay = document.querySelector('#room-id-display');


var stompClient = null;
var currentSubscription;
var username = null;
var topic = null;
var roomId = null;
localStorage.page = 0;

function connect(event) {
    username = nameInput.val().trim();
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function enterRoom(newRoomName) {
    roomIdDisplay.textContent = newRoomName;

    topic = `/app/ws/chat/room/${roomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);

    stompClient.send(`${topic}/user/add`,
        {}, username
    );
}

function onConnected() {
    enterRoom(roomInput.val());
    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent.startsWith('/join ')) {
        var newRoomName = messageContent.substring('/join '.length);
        enterRoom(newRoomName);
        while (messageArea.firstChild) {
            messageArea.removeChild(messageArea.firstChild);
        }
    } else if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
        };
        stompClient.send(`${topic}/message/send`, {}, JSON.stringify(chatMessage));
    }
    messageInput.value = '';
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = addMessage(message);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getHistory(){
    var page = localStorage.page
    var pageSize = 15;

    $.ajax({
        url: 'api/chat/room/' + roomId + '/messages',
        method: 'GET',
        data: {
            page: page,
            pageSize: pageSize
        }
    }).done(({content}) => {content.forEach(
        function sendHistory(currentContent){
            var messageElement = addMessage(currentContent);
            messageArea.prepend(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }
    )})
    localStorage.page = ++localStorage.page;
}

function changeRoom(){
    let roomObj = {
        "roomName" : changeRoomInput.val()
    }

    $.ajax({
        url: 'chat/rooms',
        method: 'POST',
        data: roomObj,
    }).done(function (data){
        roomId = data.id;
        messageArea.textContent = '';
        localStorage.page = 0;
        enterRoom(changeRoomInput.val());
    });
}

(function getRooms(){
    $.ajax({
        url: 'chat/rooms',
        method: 'GET'

    }).done((data) => data.forEach(({roomName}) => $('#select').append('<option>' + roomName + '</option>')));
}());

function createRoom(){
    let roomObj = {
        "roomName": roomInput.val()
    }

    $.ajax({
        url: 'chat/rooms',
        method: 'POST',
        data: roomObj,
    }).done((data) => roomId = data.id);
}

function addMessage(message){
    var messageElement = document.createElement('li');

    if (message.type == 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.username + ' joined!';
    }
    else if (message.type == 'ERROR') {
        connectingElement.classList.remove('hidden');
        connectingElement.textContent = 'The maximum length of the message is 1000 symbols!';
        connectingElement.style.color = 'red';
        message.content = '';
    } else if (message.type == 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.username + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);


        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    return messageElement;
}

 $(document).ready(function() {
    usernameForm.addEventListener('submit', connect);
    messageForm.addEventListener('submit', sendMessage);
});