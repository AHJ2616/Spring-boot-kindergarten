let questionCount = 0;

function addQuestion() {
    const questionHtml = `
        <div class="question-item mb-3">
            <div class="card">
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label">질문 ${questionCount + 1}</label>
                        <input type="text" class="form-control mb-2" placeholder="질문을 입력하세요">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">질문 유형</label>
                        <select class="form-control mb-2" onchange="handleQuestionTypeChange(this)">
                            <option value="SINGLE_CHOICE">단일 선택</option>
                            <option value="MULTIPLE_CHOICE">다중 선택</option>
                            <option value="TEXT">주관식</option>
                        </select>
                    </div>
                    <div class="choices">
                        <div class="choice-item d-flex align-items-center mb-2">
                            <div class="flex-grow-1 me-2">
                                <input type="text" class="form-control" placeholder="선택지를 입력하세요">
                            </div>
                            <button type="button" class="btn btn-danger btn-sm" onclick="removeChoice(this)">삭제</button>
                        </div>
                    </div>
                    <div class="btn-group mt-2">
                        <button type="button" class="btn btn-secondary btn-sm" onclick="addChoice(this)">선택지 추가</button>
                        <button type="button" class="btn btn-danger btn-sm" onclick="removeQuestion(this)">질문 삭제</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    $('#questionList').append(questionHtml);
    questionCount++;
}

function addChoice(btn) {
    const choiceHtml = `
        <div class="choice-item d-flex align-items-center mb-2">
            <div class="flex-grow-1 me-2">
                <input type="text" class="form-control" placeholder="선택지를 입력하세요">
            </div>
            <button type="button" class="btn btn-danger btn-sm" onclick="removeChoice(this)">삭제</button>
        </div>
    `;
    $(btn).closest('.card-body').find('.choices').append(choiceHtml);
}

function removeQuestion(btn) {
    $(btn).closest('.question-item').remove();
    questionCount--;
}

function removeChoice(btn) {
    const choicesCount = $(btn).closest('.choices').find('.choice-item').length;
    if (choicesCount > 1) {
        $(btn).closest('.choice-item').remove();
    } else {
        alert('최소 하나의 선택지는 있어야 합니다.');
    }
}

function handleQuestionTypeChange(select) {
    const choicesDiv = $(select).closest('.card-body').find('.choices');
    const addChoiceBtn = $(select).closest('.card-body').find('button[onclick="addChoice(this)"]');
    
    if (select.value === 'TEXT') {
        choicesDiv.hide();
        addChoiceBtn.hide();
    } else {
        choicesDiv.show();
        addChoiceBtn.show();
        if (choicesDiv.find('.choice-item').length === 0) {
            addChoice(addChoiceBtn[0]);
        }
    }
}

function saveSurvey() {
    const surveyData = {
        title: $('#surveyTitle').val(),
        description: $('#surveyDescription').val(),
        questions: []
    };

    // 유효성 검사
    if (!surveyData.title.trim()) {
        alert('설문 제목을 입력해주세요.');
        return;
    }

    $('.question-item').each(function(index) {
        const questionText = $(this).find('input:first').val().trim();
        const questionType = $(this).find('select').val();

        if (!questionText) {
            alert('모든 질문을 입력해주세요.');
            return false;
        }

        const questionData = {
            text: questionText,
            type: questionType,
            orderNumber: index,
            answers: []
        };

        // 주관식이 아닌 경우에만 선택지 추가
        if (questionType !== 'TEXT') {
            let hasEmptyChoice = false;
            $(this).find('.choice-item input').each(function(choiceIndex) {
                const choiceText = $(this).val().trim();
                if (!choiceText) {
                    hasEmptyChoice = true;
                    return false;
                }
                const answerData = {
                    text: choiceText,
                    orderNumber: choiceIndex,
                    selected: false,
                    respondentId: null
                };
                questionData.answers.push(answerData);
            });

            if (hasEmptyChoice) {
                alert('모든 선택지를 입력해주세요.');
                return false;
            }
        }

        surveyData.questions.push(questionData);
    });

    if (surveyData.questions.length === 0) {
        alert('최소 하나의 질문이 필요합니다.');
        return;
    }

    // CSRF 토큰 가져오기
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    console.log('전송할 데이터:', JSON.stringify(surveyData, null, 2));

    // AJAX로 서버에 전송
    $.ajax({
        url: '/api/survey',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(surveyData),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response) {
            console.log('서버 응답:', response);
            alert('설문조사가 저장되었습니다.');
            $('#surveyModal').modal('hide');
            
            const surveyHtml = generateSurveyHtml(response);
            $('#summernote').summernote('pasteHTML', surveyHtml);
        },
        error: function(xhr, status, error) {
            console.error('에러 상태:', status);
            console.error('에러 내용:', error);
            console.error('서버 응답:', xhr.responseText);
            alert('설문조사 저장에 실패했습니다: ' + (xhr.responseText || error));
        }
    });
}

function loadSurvey(surveyId) {
    $.ajax({
        url: `/api/survey/${surveyId}`,
        type: 'GET',
        success: function(survey) {
            $('#surveyTitle').val(survey.title);
            $('#surveyDescription').val(survey.description);
            
            $('#questionList').empty();
            survey.questions.forEach(question => {
                addQuestion();
                const $lastQuestion = $('.question-item:last');
                $lastQuestion.find('input:first').val(question.text);
                $lastQuestion.find('select').val(question.type);
                
                question.answers.forEach(answer => {
                    if (question.type !== 'TEXT') {
                        addChoice($lastQuestion.find('.btn-secondary')[0]);
                        $lastQuestion.find('.choice-item:last input').val(answer.text);
                    }
                });
            });
        },
        error: function(xhr, status, error) {
            alert('설문조사를 불러오는데 실패했습니다.');
            console.error('Error:', error);
        }
    });
}

function updateSurvey(surveyId) {
    const surveyData = {
        surveyId: surveyId,
        title: $('#surveyTitle').val(),
        description: $('#surveyDescription').val(),
        questions: []
    };

    // 질문 데이터 수집 로직은 saveSurvey()와 동일
    $('.question-item').each(function(index) {
        const question = {
            text: $(this).find('input:first').val(),
            type: $(this).find('select').val(),
            orderNumber: index,
            answers: []
        };

        $(this).find('.choice-item input').each(function(choiceIndex) {
            const answer = {
                text: $(this).val(),
                orderNumber: choiceIndex,
                selected: false
            };
            question.answers.push(answer);
        });

        surveyData.questions.push(question);
    });

    $.ajax({
        url: `/api/survey/${surveyId}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(surveyData),
        success: function(response) {
            alert('설문조사가 수정되었습니다.');
            $('#surveyModal').modal('hide');
        },
        error: function(xhr, status, error) {
            alert('설문조사 수정에 실패했습니다.');
            console.error('Error:', error);
        }
    });
}

function deleteSurvey(surveyId) {
    if (confirm('정말로 이 설문조사를 삭제하시겠습니까?')) {
        $.ajax({
            url: `/api/survey/${surveyId}`,
            type: 'DELETE',
            success: function() {
                alert('설문조사가 삭제되었습니다.');
                // 필요한 경우 페이지 새로고침 또는 다른 처리
            },
            error: function(xhr, status, error) {
                alert('설문조사 삭제에 실패했습니다.');
                console.error('Error:', error);
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('addQuestionBtn').addEventListener('click', addQuestion);
    
    $('#surveyModal').on('show.bs.modal', function () {
        $('#surveyTitle').val('');
        $('#surveyDescription').val('');
        $('#questionList').empty();
        questionCount = 0;
        
        addQuestion();
    });
});

// Tooltip 초기화 문제 해결을 위한 코드 추가
document.addEventListener('DOMContentLoaded', function() {
    // 기존 이벤트 리스너 유지
    document.getElementById('addQuestionBtn').addEventListener('click', addQuestion);
    
    // 모달 관련 이벤트
    $('#surveyModal').on('show.bs.modal', function () {
        $('#surveyTitle').val('');
        $('#surveyDescription').val('');
        $('#questionList').empty();
        questionCount = 0;
        addQuestion();
    });

    // Bootstrap tooltip 초기화 제거 (필요한 경우에만 특정 요소에 적용)
    // $('[data-bs-toggle="tooltip"]').tooltip();
});

// 설문조사 HTML 생성 함수
function generateSurveyHtml(survey) {
    let html = `
        <div class="survey-container" data-survey-id="${survey.surveyId}">
            <h3>${survey.title}</h3>
            <p>${survey.description}</p>
            <form class="survey-form">
                <div class="survey-questions">
    `;

    survey.questions.forEach((question, qIndex) => {
        html += `
            <div class="survey-question" data-question-id="${question.questionId}">
                <p><strong>${qIndex + 1}. ${question.text}</strong></p>
        `;

        if (question.type === 'TEXT') {
            html += `<textarea class="form-control" rows="3" name="question_${question.questionId}"></textarea>`;
        } else {
            question.answers.forEach((answer, aIndex) => {
                const inputType = question.type === 'SINGLE_CHOICE' ? 'radio' : 'checkbox';
                html += `
                    <div class="form-check">
                        <input class="form-check-input" type="${inputType}" 
                               name="question_${question.questionId}" 
                               id="answer_${answer.answerId}" 
                               value="${answer.answerId}">
                        <label class="form-check-label" for="answer_${answer.answerId}">
                            ${answer.text}
                        </label>
                    </div>
                `;
            });
        }

        html += `</div>`;
    });

    html += `
                </div>
                <button type="submit" class="survey-submit-btn">제출하기</button>
            </form>
        </div>
    `;

    return html;
}