let stompClient = null;
let username = null;
let userId = null;
let csrfToken = null;
let csrfHeader = null;

// CSRF 토큰 초기화
function initializeCsrf() {
    csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
}

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // CSRF 헤더 추가
    const headers = {};
    headers[csrfHeader] = csrfToken;

    stompClient.connect(headers, onConnected, onError);
}

function onConnected() {
    // 개인 메시지를 구독
    stompClient.subscribe('/user/' + userId + '/queue/messages', onMessageReceived);

    // 공개 토픽 구독
    stompClient.subscribe('/topic/public', onMessageReceived);

    // 사용자가 참여했음을 알림
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({
            sender: username,
            senderId: userId,
            messageType: 'JOIN'
        })
    );
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = document.querySelector('#message').value.trim();
    const receiverId = document.querySelector('#receiverId').value;

    if (messageContent && stompClient) {
        const headers = {};
        headers[csrfHeader] = csrfToken;

        const chatMessage = {
            senderId: userId,
            senderName: username,
            receiverId: receiverId,
            content: messageContent,
            messageType: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", headers, JSON.stringify(chatMessage));
        document.querySelector('#message').value = '';
        document.querySelector('#currentLength').textContent = '0';
    }
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    displayMessage(message);
}

function onError(error) {
    console.log('WebSocket 연결 오류:', error);
}

function loadChatHistory(otherUserId) {
    fetch(`/api/chat/history/${otherUserId}`)
        .then(response => response.json())
        .then(messages => {
            const messageArea = document.querySelector('#messageArea');
            messageArea.innerHTML = ''; // 기존 메시지 클리어
            messages.forEach(message => {
                displayMessage(message);
            });
        })
        .catch(error => console.error('채팅 기록을 불러오는데 실패했습니다:', error));
}

function displayMessage(message) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');

    if (message.senderId === userId) {
        messageElement.classList.add('sent');
    } else {
        messageElement.classList.add('received');
    }

    const time = new Date(message.sendTime).toLocaleTimeString();
    messageElement.innerHTML = `
        <div class="message-content">${message.content}</div>
        <div class="message-time">${time}</div>
    `;

    document.querySelector('#messageArea').appendChild(messageElement);
    scrollToBottom();
}

// 사용자 목록 로드
function loadUserList() {
    const headers = new Headers();
    headers.append(csrfHeader, csrfToken);
    headers.append('Content-Type', 'application/json');

    fetch('/api/chat/users', {
        method: 'GET',
        headers: headers,
        credentials: 'same-origin'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(users => {
            const userList = document.querySelector('#userList');
            if (!userList) {
                console.error('User list element not found');
                return;
            }

            userList.innerHTML = '';
            users.forEach(user => {
                const li = document.createElement('li');
                li.innerHTML = `
                <div class="user-item" onclick="selectUser(${user.id}, '${user.name}')">
                    <div class="user-avatar">
                        ${user.profileImage ?
                    `<img src="/upload/${user.profileImage}" alt="${user.name}" />` :
                    `<span>${user.name.charAt(0)}</span>`}
                    </div>
                    <div class="user-info">
                        <span class="user-name">${user.name}</span>
                        <span class="user-position">${user.message || ''}</span>
                    </div>
                </div>
            `;
                userList.appendChild(li);
            });
            setupUserSearch();
        })
        .catch(error => {
            console.error('사용자 목록을 불러오는데 실패했습니다:', error);
        });
}

// 사용자 선택
function selectUser(selectedUserId, selectedUserName) {
    document.querySelector('#receiverId').value = selectedUserId;
    // 선택된 사용자와의 채팅 기록 로드
    loadChatHistory(selectedUserId);
    // UI 업데이트
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    event.currentTarget.classList.add('active');
    // 채팅방의 메시지를 읽음으로 표시
    const chatRoom = getCurrentChatRoom();
    if (chatRoom) {
        markMessagesAsRead(chatRoom.id);
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    initializeCsrf();

    // userId와 username이 정의되어 있는지 확인
    if (typeof userId === 'undefined' || typeof username === 'undefined') {
        console.error('사용자 정보가 없습니다.');
        return;
    }

    loadUserList();
    connect();

    // 메시지 입력 폼 이벤트 리스너 추가
    const messageForm = document.getElementById('messageForm');
    if (messageForm) {
        messageForm.addEventListener('submit', function(e) {
            e.preventDefault();
            sendMessage(e);
        });
    }

    // 메시지 길이 카운터 설정
    const messageInput = document.getElementById('message');
    const lengthCounter = document.getElementById('currentLength');
    if (messageInput && lengthCounter) {
        messageInput.addEventListener('input', function() {
            lengthCounter.textContent = this.value.length;
        });
    }
});

// 자동 스크롤 함수 추가
function scrollToBottom() {
    const messageArea = document.querySelector('#messageArea');
    messageArea.scrollTop = messageArea.scrollHeight;
}

function markMessagesAsRead(chatRoomId) {
    fetch(`/api/chat/read/${chatRoomId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    }).catch(error => console.error('메시지 읽음 처리 실패:', error));
}

function getCurrentChatRoom() {
    // 현재 채팅방을 가져오는 로직을 구현해야 합니다.
    // 예를 들어, 선택된 사용자와의 채팅방 ID를 가져올 수 있습니다.
    // 여기서는 간단하게 배열을 사용하여 예시를 보여드립니다.
    const chatRooms = [
        { id: 1, userId: 1 },
        { id: 2, userId: 2 },
        { id: 3, userId: 3 }
    ];
    const currentUserId = userId;
    return chatRooms.find(chatRoom => chatRoom.userId === currentUserId);
}
// 사용자 검색 기능
function setupUserSearch() {
    const searchInput = document.getElementById('userSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const userItems = document.querySelectorAll('.user-item');

            userItems.forEach(item => {
                const userName = item.querySelector('.user-name').textContent.toLowerCase();
                const userPosition = item.querySelector('.user-position')?.textContent.toLowerCase() || '';

                if (userName.includes(searchTerm) || userPosition.includes(searchTerm)) {
                    item.parentElement.style.display = ''; // li 요소를 보이게
                } else {
                    item.parentElement.style.display = 'none'; // li 요소를 숨김
                }
            });
        });

        // 검색창 클리어 기능 추가
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Escape') {
                this.value = '';
                const userItems = document.querySelectorAll('.user-item');
                userItems.forEach(item => {
                    item.parentElement.style.display = '';
                });
            }
        });
    } else {
        console.error('Search input element not found');
    }
}