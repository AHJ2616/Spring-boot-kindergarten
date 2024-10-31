document.addEventListener('DOMContentLoaded', function() {
    // 정렬 select 요소의 변경 이벤트 처리
    const sortSelect = document.querySelector('.sort-select');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const sortBy = this.value;
            const currentUrl = window.location.pathname;
            const urlParams = new URLSearchParams(window.location.search);
            const keyword = urlParams.get('keyword');

            // 검색 결과 페이지인 경우
            if (currentUrl === '/board/search' && keyword) {
                fetch(`/rest/board/search?keyword=${encodeURIComponent(keyword)}&sortBy=${sortBy}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        updateBoardList(data);
                    })
                    .catch(error => console.error('Error:', error));
            }
            // 일반 게시판 페이지인 경우
            else {
                fetch(`/rest/board/sort?sortBy=${sortBy}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        updateBoardList(data);
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    }

    // 검색 버튼 클릭 이벤트 처리
    const searchButton = document.getElementById('search-button');
    const searchInput = document.getElementById('searchInput');

    if (searchButton && searchInput) {
        searchButton.addEventListener('click', function() {
            const keyword = searchInput.value.trim();
            if (keyword) {
                window.location.href = `/board/search?keyword=${encodeURIComponent(keyword)}`;
            }
        });

        // 엔터 키 이벤트 처리
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const keyword = this.value.trim();
                if (keyword) {
                    window.location.href = `/board/search?keyword=${encodeURIComponent(keyword)}`;
                }
            }
        });
    }
});

// 게시판 목록 업데이트 함수
function updateBoardList(data) {
    const tbody = document.querySelector('.board-table tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    data.content.forEach(board => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="title">
                <a href="/board/${board.boardId}">${board.boardTitle}</a>
            </td>
            <td>${board.boardWriter}</td>
            <td>${formatDate(board.regiDate)}</td>
            <td>${board.views}</td>
        `;
        tbody.appendChild(tr);
    });

    // 검색 결과가 없는 경우 메시지 표시
    const noResult = document.querySelector('.no-result');
    if (noResult) {
        noResult.style.display = data.content.length === 0 ? 'block' : 'none';
    }
}

// 날짜 포맷 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
}
