'use strict';

var nameInput = $('#name');
var roomInput = $('#room-id');
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
var roomName = null;
var topic = null;
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


function enterRoom(newRoomId) {
    roomName = newRoomId;
    roomIdDisplay.textContent = roomName;
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${roomName}`, onMessageReceived);

    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
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
        var newRoomId = messageContent.substring('/join '.length);
        enterRoom(newRoomId);
        while (messageArea.firstChild) {
            messageArea.removeChild(messageArea.firstChild);
        }
    } else if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            roomName: roomName
        };
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
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
    $.ajax({
        url: 'chat/' + roomName + '/getMessages/' + localStorage.page,
        method: 'GET'
    }).done(({content}) => {content.forEach(
        function sendHistory(currentContent){
            var messageElement = addMessage(currentContent);
            messageArea.prepend(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }
    )})
    localStorage.page = ++localStorage.page;
}

function getRooms(){
    $.ajax({
        url: 'chat/getRooms',
        method: 'GET',
        success: function (data) {
            console.log(data);
        }
    }).done();
}

function addMessage(message){
    var messageElement = document.createElement('li');

    if (message.type == 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    }
    else if (message.type == 'ERROR') {
        connectingElement.classList.remove('hidden');
        connectingElement.textContent = 'The maximum length of the message is 1000 symbols!';
        connectingElement.style.color = 'red';
        message.content = '';
    } else if (message.type == 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
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