document.addEventListener('DOMContentLoaded', function() {
    // 모든 드롭다운 토글 버튼에 이벤트 리스너 추가
    const dropdowns = document.querySelectorAll('.dropdown-toggle');

    dropdowns.forEach(dropdown => {
        dropdown.addEventListener('click', function(e) {
            e.preventDefault();
            // 현재 클릭된 드롭다운의 메뉴를 찾음
            const menu = this.nextElementSibling;

            // 다른 열린 드롭다운 메뉴들을 닫음
            document.querySelectorAll('.dropdown-menu').forEach(item => {
                if (item !== menu) {
                    item.classList.remove('show');
                }
            });

            // 현재 메뉴의 표시 상태를 토글
            menu.classList.toggle('show');
        });
    });

    // 문서 클릭 시 열린 드롭다운 메뉴 닫기
    document.addEventListener('click', function(e) {
        if (!e.target.matches('.dropdown-toggle')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                if (menu.classList.contains('show')) {
                    menu.classList.remove('show');
                }
            });
        }
    });
});