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
            const sortBy = this.value;
            sortBoardListClient(sortBy);
        });
    }
});

// 클라이언트 사이드 정렬 함수
function sortBoardListClient(sortBy) {
    const tbody = document.querySelector('.board-table tbody');
    if (!tbody || !tbody.originalRows) return;

    // 원본 데이터를 복사하여 정렬
    const rows = Array.from(tbody.originalRows).map(row => row.cloneNode(true));
    
    rows.sort((a, b) => {
        switch(sortBy) {
            case 'latest':
                const dateA = new Date(a.children[2].textContent);
                const dateB = new Date(b.children[2].textContent);
                return dateB - dateA;
            case 'views':
                const viewsA = parseInt(a.children[3].textContent);
                const viewsB = parseInt(b.children[3].textContent);
                return viewsB - viewsA;
            default:
                return 0;
        }
    });

    // tbody 내용을 비우고 정렬된 행들을 추가
    tbody.innerHTML = '';
    rows.forEach(row => tbody.appendChild(row));
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



