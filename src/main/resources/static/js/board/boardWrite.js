$(document).ready(function() {
    initializeSummernote();
    initializeFileUpload();
    initializeFormSubmit();
});

function initializeSummernote() {
    $('#summernote').summernote({
        height: 300,
        lang: 'ko-KR',
        sanitize: true,
        disableDragAndDrop: true,
        toolbar: [
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['style', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],
            ['color', ['forecolor','backcolor']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']],
            ['insert', ['picture', 'link', 'video']],
            ['view', ['fullscreen','help']]
        ],
        fontNames: ['Arial', '맑은 고딕', '궁서', '굴림', '굴림체', '돋움체', 'sans-serif'],
        fontSizes: ['8', '9', '10', '11', '12', '14', '16', '18', '20', '22', '24', '28', '30', '36'],
        callbacks: {
            onImageUpload: function(files) {
                for(let i = 0; i < files.length; i++) {
                    uploadSummernoteImage(files[i], this);
                }
            }
        },
    });
}

function uploadSummernoteImage(file, editor) {
    const data = new FormData();
    data.append("file", file);

    $.ajax({
        data: data,
        type: "POST",
        url: "/rest/board/uploadImage",
        contentType: false,
        processData: false,
        success: function(data) {
            $(editor).summernote('insertImage', data.url);
        },
        error: function(data) {
            console.log("이미지 업로드 실패");
        }
    });
}

function initializeFileUpload() {
    document.getElementById('boardFile').addEventListener('change', function(e) {
        const files = e.target.files;
        const fileCount = files.length;
        const fileCountDiv = document.getElementById('fileCount');
        fileCountDiv.textContent = `선택된 파일 수: ${fileCount}개`;
    });
}

function initializeFormSubmit() {
    document.querySelector('form').addEventListener('submit', async function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        try {
            const response = await $.ajax({
                url: this.action,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false
            });

            if (response.success) {
                window.location.href = response.redirectUrl;
            } else {
                window.location.href = '/board/list/common';
            }
        } catch (error) {
            console.error('Error:', error);
            alert('파일 업로드 중 오류가 발생했습니다.');
            window.location.href = '/board/list/common';
        }
    });
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    const boardTypeSelect = document.getElementById('board_type');
    const userRoleValue = userRole; // write.html에서 전달받은 userRole 값

    // ROLE_ADMIN이 아닌 경우, 공지와 필독 옵션 선택 시 경고 메시지 표시
    boardTypeSelect.addEventListener('change', function() {
        if (userRoleValue !== 'ROLE_ADMIN') {
            if (this.value === 'NOTIFICATION' || this.value === 'ABSOLUTE') {
                alert('관리자만 공지/필독 게시글을 작성할 수 있습니다.');
                this.value = 'COMMON'; // 일반 게시글로 되돌림
            }
        }
    });
});