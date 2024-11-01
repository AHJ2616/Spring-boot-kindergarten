document.addEventListener('DOMContentLoaded', function() {
    const sortSelect = document.querySelector('.sort-select');

    sortSelect.addEventListener('change', function() {
        loadSortedBoards(this.value);
    });
});

function loadSortedBoards(sortBy, page = 0) {
    fetch(`/rest/board/sort?sortBy=${sortBy}&page=${page}`)
        .then(response => response.json())
        .then(data => {
            updateBoardList(data);
            updatePagination(data);
        })
        .catch(error => console.error('Error:', error));
}

function updateBoardList(data) {
    const boardContent = document.querySelector('.board-content');
    if (!data.content || data.content.length === 0) {
        boardContent.innerHTML = '<div class="no-result">검색과 일치하는 결과가 없습니다.</div>';
        return;
    }

    let html = '';
    data.content.forEach(board => {
        html += `
            <div class="board-item">
            <a href="/board/${board.boardId}">
                <h3>${board.boardTitle}</h3>
                </a>
                <span>작성자: ${board.boardWriter}</span>
                <span>조회수: ${board.views}</span>
                <span>좋아요: ${board.likes}</span>
                <span>작성일: ${board.regiDate}</span>
            </div>
        `;
    });
    boardContent.innerHTML = html;
}

function updatePagination(data) {
    const pagination = document.querySelector('.pagination');
    let html = '';

    if (data.hasPrevious) {
        html += `<a href="javascript:void(0)" onclick="loadSortedBoards('${document.querySelector('.sort-select').value}', ${data.number - 1})">&laquo; 이전</a>`;
    }

    for (let i = 0; i < data.totalPages; i++) {
        html += `
            <a href="javascript:void(0)" 
               onclick="loadSortedBoards('${document.querySelector('.sort-select').value}', ${i})"
               class="${data.number === i ? 'active' : ''}">${i + 1}</a>
        `;
    }

    if (data.hasNext) {
        html += `<a href="javascript:void(0)" onclick="loadSortedBoards('${document.querySelector('.sort-select').value}', ${data.number + 1})">다음 &raquo;</a>`;
    }

    pagination.innerHTML = html;
}