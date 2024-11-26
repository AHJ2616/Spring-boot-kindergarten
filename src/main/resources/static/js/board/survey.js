document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('#surveyForm form');
    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const answers = [];
            const processedQuestions = new Set(); // 중복 답변 방지를 위한 Set

            document.querySelectorAll('.question-block').forEach(questionBlock => {
                const questionId = questionBlock.querySelector('input[name$="].questionId"]').value;

                // 이미 처리된 질문은 건너뛰기
                if (processedQuestions.has(questionId)) {
                    return;
                }

                // 라디오 버튼 처리
                const radioButton = questionBlock.querySelector('input[type="radio"]:checked');
                if (radioButton) {
                    answers.push({
                        questionId: questionId,
                        text: radioButton.value
                    });
                    processedQuestions.add(questionId);
                }

                // 체크박스 처리
                const checkboxes = questionBlock.querySelectorAll('input[type="checkbox"]:checked');
                if (checkboxes.length > 0) {
                    const selectedValues = Array.from(checkboxes).map(cb => cb.value);
                    answers.push({
                        questionId: questionId,
                        text: selectedValues.join(', ')
                    });
                    processedQuestions.add(questionId);
                }

                // 텍스트 영역 처리
                const textarea = questionBlock.querySelector('textarea');
                if (textarea && textarea.value.trim()) {
                    answers.push({
                        questionId: questionId,
                        text: textarea.value.trim()
                    });
                    processedQuestions.add(questionId);
                }
            });

            if (answers.length === 0) {
                alert('최소 하나 이상의 답변을 입력해주세요.');
                return;
            }

            // CSRF 토큰 가져오기
            const token = document.querySelector("meta[name='_csrf']").getAttribute("content");
            const header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
            
            // API 호출
            fetch('/survey/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify({
                    surveyId: document.querySelector('input[name="surveyId"]').value,
                    answers: answers
                })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(result => {
                if (result.status === 'success') {
                    alert(result.message);
                    window.location.href = result.redirectUrl;
                } else {
                    throw new Error(result.message || '설문 제출에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('설문 제출 중 오류가 발생했습니다. 다시 시도해주세요.');
            });
        });
    }
});


 