$(document).ready(function() {
    initializeSummernote();
    initializeFileUpload();
    initializeFormSubmit();
});


function initializeSummernote() {
    $('#summernote').summernote({
        height: 300,
        lang: 'ko-KR',
        sanitize: true, // HTML sanitize 활성화
        disableDragAndDrop: true, // 드래그 앤 드롭 비활성화
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
        emoji: {
            dropdown: true,
            container: 'body',
            categories: [
                'people', 'nature', 'food', 'activity',
                'travel', 'objects', 'symbols', 'flags'
            ],
            showCategory: true,
            recentCount: 36,
            useLocalStorage: true
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
    document.querySelector('form').addEventListener('submit', function(e) {
        e.preventDefault();

        const formData = new FormData(this);

        $.ajax({
            url: this.action,
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                if (response.success) {
                    window.location.href = response.redirectUrl;
                } else {
                    window.location.href = '/board/list/common';
                }
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('파일 업로드 중 오류가 발생했습니다.');
                window.location.href = '/board/list/common';
            }
        });
    });
}
