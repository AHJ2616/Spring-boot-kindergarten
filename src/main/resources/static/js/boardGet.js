// DOM이 로드되면 실행될 초기화 함수
$(document).ready(function() {
    // 이미지 프리뷰 초기화
    const thumbnails = document.querySelectorAll('.thumbnail-item img');
    const previewImage = document.getElementById('preview-image');

    if (thumbnails.length > 0 && previewImage) {
        thumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('mouseover', function() {
                previewImage.src = this.src;
            });
        });
    }

    // 댓글 토글 초기화
    const toggleButton = document.getElementById('toggleComments');
    const commentsSection = document.getElementById('comments-section');

    if (toggleButton && commentsSection) {
        // 초기 상태 설정
        commentsSection.style.display = 'none';
        toggleButton.textContent = '댓글보기';

        toggleButton.addEventListener('click', function() {
            if (commentsSection.style.display === 'none') {
                commentsSection.style.display = 'block';
                toggleButton.textContent = '댓글닫기';
            } else {
                commentsSection.style.display = 'none';
                toggleButton.textContent = '댓글보기';
            }
        });
    }
});

// 게시글 삭제 함수
function deleteBoard(boardId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        fetch(`/board/delete/${boardId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('삭제되었습니다.');
                    window.location.href = '/board/basic';
                } else {
                    alert('삭제 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
    }
}

// 댓글 제출 함수
function submitComment() {
    const contents = document.getElementById('contents').value;
    if (!contents.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    fetch('/comments/write', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify({
            boardId: boardId,
            contents: contents
        })
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('댓글 등록에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('댓글 등록 중 오류가 발생했습니다.');
        });
}

// 댓글 삭제 함수
function deleteComment(commentId) {
    if (!confirm('댓글을 삭제하시겠습니까?')) return;

    fetch(`/comments/delete/${commentId}`, {
        method: 'DELETE',
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('댓글 삭제 중 오류가 발생했습니다.');
        });
}

// 댓글 수정 함수
function editComment(commentId) {
    const commentDiv = document.querySelector(`[data-comment-id="${commentId}"]`);
    if (!commentDiv) return;

    const content = commentDiv.querySelector('.comment-content').textContent;
    const textarea = document.createElement('textarea');
    textarea.value = content;
    textarea.className = 'edit-textarea';

    const saveButton = document.createElement('button');
    saveButton.textContent = '저장';
    saveButton.className = 'btn btn-sm btn-primary';
    saveButton.onclick = () => saveEdit(commentId, textarea.value);

    const cancelButton = document.createElement('button');
    cancelButton.textContent = '취소';
    cancelButton.className = 'btn btn-sm btn-secondary';
    cancelButton.onclick = () => location.reload();

    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'edit-buttons';
    buttonContainer.appendChild(saveButton);
    buttonContainer.appendChild(cancelButton);

    commentDiv.querySelector('.comment-content').replaceWith(textarea);
    commentDiv.querySelector('.comment-actions').replaceWith(buttonContainer);
}

// 댓글 수정 저장 함수
function saveEdit(commentId, content) {
    if (!content.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    fetch(`/comments/update/${commentId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        },
        body: JSON.stringify({
            commentContent: content
        })
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('댓글 수정에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        });
}
