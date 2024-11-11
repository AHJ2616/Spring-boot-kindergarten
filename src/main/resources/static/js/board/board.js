document.addEventListener('DOMContentLoaded', function() {
    const sortSelect = document.querySelector('.sort-select');
    if (sortSelect) {
        // 초기 데이터 저장
        const tbody = document.querySelector('.board-table tbody');
        if (tbody) {
            // 원본 데이터를 보존하기 위해 깊은 복사 수행
            tbody.originalRows = Array.from(tbody.querySelectorAll('tr')).map(row => row.cloneNode(true));
        }

        sortSelect.addEventListener('change', function() {
            const sortType = this.value;
            const currentUrl = new URL(window.location.href);
            const boardType = currentUrl.pathname.split('/').pop();
            const keyword = currentUrl.searchParams.get('keyword');
            
            // 현재 페이지의 게시글 목록을 가져옴
            const boards = Array.from(document.querySelectorAll('.board-table tbody tr'));
            
            // ABSOLUTE 타입과 일반 게시글을 분리
            const absoluteBoards = boards.filter(tr => tr.classList.contains('absolute-post'));
            const normalBoards = boards.filter(tr => !tr.classList.contains('absolute-post'));
            
            // 일반 게시글만 정렬
            normalBoards.sort((a, b) => {
                if (sortType === 'regiDate') {
                    const dateA = new Date(a.children[2].textContent);
                    const dateB = new Date(b.children[2].textContent);
                    return dateB - dateA;
                } else if (sortType === 'views') {
                    const viewsA = parseInt(a.children[3].textContent);
                    const viewsB = parseInt(b.children[3].textContent);
                    return viewsB - viewsA;
                }
            });
            
            // 테이블 내용 초기화
            const tbody = document.querySelector('.board-table tbody');
            tbody.innerHTML = '';
            
            // ABSOLUTE 게시글을 먼저 추가
            absoluteBoards.forEach(tr => tbody.appendChild(tr));
            // 정렬된 일반 게시글 추가
            normalBoards.forEach(tr => tbody.appendChild(tr));
        });
    }
});

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



