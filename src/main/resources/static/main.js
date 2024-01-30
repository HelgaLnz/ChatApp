'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const btnOpenModalWindow = document.querySelector('#createGroupRoom');
const logout = document.querySelector('#logout');
const chatUsername = document.querySelector('#chat-username');
const checkBoxPassword = document.querySelector('#checkBoxShowPassword');
const modalWindow = document.querySelector('#create-group-room-modal');
const btnCreateGroupChatRoomModal = document.querySelector('#btn-create-group-chat-modal');
const btnCloseModalWindow = document.querySelector('#btn-close-modal-window');

let stompClient = null;
let nickname = null;
let password = null;
let selectedUserId = null;
let selectedGroupRoomId = null;
let isActiveUsersChat = null;
let isActiveGroupChat = null;
let modalWindowActive = null;


function online(event) {
    nickname = localStorage.getItem('nickname');
    password = localStorage.getItem('password');
    if (nickname && password) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        connectStomp(event);
    }
}

function connect(event) {
    if (!nickname && !password) {
        nickname = document.querySelector('#nickname').value.trim();
        password = document.querySelector('#password').value.trim();

        localStorage.setItem('nickname', nickname);
        localStorage.setItem('password', password);

        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
    }
    connectStomp(event);
}

function connectStomp(event) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);

    event.preventDefault();
}


function onConnected(options) {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/${nickname}/queue/groupMessages`, onMessageReceived);

    stompClient.subscribe(`/user/public`, onMessageReceived);


    // register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, fullName: password, status: 'ONLINE'})
    );
    document.querySelector('#connected-user-fullname').textContent = password;
    findAndDisplayConnectedUsers().then();
    findAndDisplayGroupChats().then();
}

async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/users');
    let connectedUsers = await connectedUsersResponse.json();

    connectedUsers = connectedUsers.filter(user => user.nickName !== nickname);
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
    });
}

async function findAndDisplayGroupChats() {
    const groupChatsUsersResponse = await fetch(`/groups/${nickname}`);
    let groupChats = await groupChatsUsersResponse.json();
    const groupChatsList = document.getElementById('groupChats');
    groupChatsList.innerHTML = '';

    groupChats.forEach(groupRoom => {
        appendGroupRoomElement(groupRoom, groupChatsList);
    });
}

async function openCreateGroupChatModalWindow() {
    console.log("Create group room");

    const connectedUsersResponse = await fetch('/users');
    let listUsers = await connectedUsersResponse.json();

    const usersList = document.getElementById('user-list');
    usersList.innerHTML = '';

    listUsers.forEach(user => {
        loadUserElement(user, usersList);
    });

    modalWindowActive = new bootstrap.Modal(modalWindow);
    modalWindowActive.show();
}

function closeModalWindow() {
    modalWindowActive.hide();
}

function loadUserElement(user, userList) {
    const listItem = document.createElement('li');
    listItem.classList.add('list-group-item');
    listItem.id = user.nickName;

    const userInput = document.createElement('input');
    userInput.type = "checkbox";
    userInput.classList.add('user-item-checkbox')
    userInput.id = user.nickName;
    userInput.style.marginRight = '10px';

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullName;

    listItem.appendChild(userInput);
    listItem.appendChild(usernameSpan);
    userList.appendChild(listItem);
}

async function sendDataForCreateGroupRoom() {
    const inputNameGroupChat = document.getElementById('inputNameGroupChat').value;
    const users = document.querySelectorAll('input[class="user-item-checkbox"]:checked');
    let usersAddInGroupRoom = [];

    users.forEach(user => {
        usersAddInGroupRoom.push(user.id);
    });

    const createdGroupRoomId = await fetch(`/groups/create/${nickname}`,
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                chatName: inputNameGroupChat,
                groupUsersId: usersAddInGroupRoom
            })
        });
    findAndDisplayGroupChats().then();
    closeModalWindow();
}

async function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.nickName;

    const userImage = document.createElement('a');
    userImage.textContent = user.status;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullName;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.classList.add('nbr-msg', 'hidden');
    receivedMsgs.textContent = '0';
    const chatId = await getChatId(user.nickName);
    await showNotificationUnreadMessage(receivedMsgs, chatId);

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}

async function appendGroupRoomElement(groupRoom, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('group-item');
    listItem.id = groupRoom.groupChatId;

    const groupImage = document.createElement('img');
    groupImage.alt = groupRoom.fullName;

    const groupSpan = document.createElement('span');
    groupSpan.textContent = groupRoom.groupChatName;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.classList.add('nbr-msg', 'hidden');
    receivedMsgs.textContent = '0';
    await showNotificationUnreadMessage(receivedMsgs, groupRoom.groupChatId);

    // listItem.appendChild(userImage);
    listItem.appendChild(groupSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', groupRoomItemClick);

    connectedUsersList.appendChild(listItem);
}

function groupRoomItemClick(event) {
    document.querySelectorAll('.group-item').forEach(item => {
        item.classList.remove('active');
    });
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    })
    messageForm.classList.remove('hidden');

    const clickedGroupRoom = event.currentTarget;
    clickedGroupRoom.classList.add('active');

    selectedGroupRoomId = clickedGroupRoom.getAttribute('id');
    fetchAndDisplayChat(null, selectedGroupRoomId).then();

    const nbrMsg = clickedGroupRoom.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    if (nbrMsg.textContent !== '0')
        setMessagesAsRead(null, selectedGroupRoomId).then(value => nbrMsg.textContent = value);

    chatUsername.textContent = document.createElement('span').textContent;
    isActiveGroupChat = true;
    isActiveUsersChat = false;
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });

    document.querySelectorAll('.group-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayChat(selectedUserId, null).then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');

    if (nbrMsg.textContent !== '0')
        setMessagesAsRead(selectedUserId, null).then(value => nbrMsg.textContent = value);

    chatUsername.textContent = document.createElement('span').textContent;
    isActiveUsersChat = true;
    isActiveGroupChat = false;
}

async function setMessagesAsRead(selectedUserId, selectedGroupId) {
    let chatId;
    if (selectedUserId)
        chatId = await getChatId(selectedUserId);
    if (selectedGroupId)
        chatId = selectedGroupId;
    const countMessagesResponse = await fetch(`/unread/messages/set/read`,
        {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                chatId: chatId,
                recipientId: nickname
            })
        });
    return await countMessagesResponse.json();
}

async function getChatId(selectedUserId) {
    return await (await fetch(`/chatRoom/${nickname}/${selectedUserId}`)).text();
}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayChat(selectedUserChatId, selectedGroupChatId) {
    let chatResponse;
    if (selectedGroupChatId != null) {
        chatResponse = await fetch(`/groupMessages/${selectedGroupChatId}`);
    } else
        chatResponse = await fetch(`/messages/${nickname}/${selectedUserChatId}`);
    const userChat = await chatResponse.json();
    chatArea.innerHTML = '';

    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        let chatMessage;
        let path;

        if (isActiveGroupChat && !isActiveUsersChat) {
            chatMessage = {
                chatId: selectedGroupRoomId,
                senderId: nickname,
                content: messageInput.value.trim(),
                timestamp: new Date()
            }
            path = `/app/groupChat`;

        } else {
            chatMessage = {
                senderId: nickname,
                recipientId: selectedUserId,
                content: messageInput.value.trim(),
                timestamp: new Date()
            };
            path = "/app/chat";

        }

        stompClient.send(path, {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    await findAndDisplayGroupChats();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);

    if (selectedUserId && selectedUserId === message.senderId && isActiveUsersChat && message.senderId !== nickname) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    } else {
        if (selectedGroupRoomId && selectedGroupRoomId === message.groupChatId
            && isActiveGroupChat && message.senderId !== nickname) {
            displayMessage(message.senderId, message.content)
            chatArea.scrollTop = chatArea.scrollHeight;
        } else {
            let notifiedIdRoom;
            let chatId;

            if (message.groupChatId) {
                notifiedIdRoom = document.getElementById(`${message.groupChatId}`);
                chatId = message.groupChatId;
            } else {
                notifiedIdRoom = document.querySelector(`#${message.senderId}`);
                chatId = message.chatId;
            }
            await showNotificationUnreadMessage(notifiedIdRoom, chatId);
        }
    }
}

async function showNotificationUnreadMessage(notifiedIdRoom, chatId) {
    if (notifiedIdRoom && !notifiedIdRoom.classList.contains('active')) {
        notifiedIdRoom.classList.remove('hidden');
        const countUnreadMessage = await (await fetch(`/unread/messages/count/${chatId}/${nickname}`)).json();
        if (!(countUnreadMessage === 0)) {
            notifiedIdRoom.textContent = countUnreadMessage;
        } else {
            notifiedIdRoom.textContent = '0';
            notifiedIdRoom.classList.add('hidden');
        }
    }
}

function onLogout() {
    stompClient.send('/app/user.disconnectUser',
        {},
        JSON.stringify({nickName: nickname, fullName: password, status: 'OFFLINE'})
    );
    localStorage.clear();
    window.location.reload();
}

function onCheckBoxPassword() {
    const elementPassword = document.getElementById('password');
    console.log(elementPassword);
    if (elementPassword.type === "password")
        elementPassword.type = "text";
    else
        elementPassword.type = "password";
}


usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
btnCloseModalWindow.addEventListener('click', closeModalWindow);
btnOpenModalWindow.addEventListener('click', openCreateGroupChatModalWindow);
btnCreateGroupChatRoomModal.addEventListener('click', sendDataForCreateGroupRoom, true);
logout.addEventListener('click', onLogout, true);
checkBoxPassword.addEventListener('change', onCheckBoxPassword);
window.addEventListener('DOMContentLoaded', online, true);
