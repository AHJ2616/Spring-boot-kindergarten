document.addEventListener('DOMContentLoaded', function() {
    const csrfToken = document.querySelector("meta[name='_csrf']").content;
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").content;

    const addQuestionBtn = document.getElementById('addQuestion');
    const saveSurveyBtn = document.getElementById('saveSurvey');
    const surveyForm = document.getElementById('surveyForm');

    if (addQuestionBtn) {
        let questionCount = 0;
        
        addQuestionBtn.addEventListener('click', function() {
            questionCount++;
            const questionHtml = createQuestionHtml(questionCount);
            document.getElementById('questionsContainer').insertAdjacentHTML('beforeend', questionHtml);
            
            const latestQuestion = document.querySelector(`.question-block[data-question="${questionCount}"]`);
            const addOptionBtn = latestQuestion.querySelector('.add-option-btn');
            if (addOptionBtn) {
                addOptionBtn.addEventListener('click', function() {
                    const optionsContainer = latestQuestion.querySelector('.options-container');
                    const optionCount = optionsContainer.children.length + 1;
                    const optionHtml = createOptionHtml(questionCount, optionCount);
                    optionsContainer.insertAdjacentHTML('beforeend', optionHtml);
                });
            }

            const questionType = latestQuestion.querySelector('.question-type');
            questionType.addEventListener('change', function() {
                const optionsSection = latestQuestion.querySelector('.options-section');
                optionsSection.style.display = this.value === 'TEXT' ? 'none' : 'block';
            });
        });
    }

    if (saveSurveyBtn) {
        saveSurveyBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleSaveSurvey(csrfHeader, csrfToken);
        });
    }

    if (surveyForm) {
        surveyForm.onsubmit = function(event) {
            submitSurvey(event, csrfHeader, csrfToken);
        };
    }
});

function handleSaveSurvey(csrfHeader, csrfToken) {
    const title = document.getElementById('surveyTitle').value;
    const description = document.getElementById('surveyDescription').value;
    const questions = [];

    if (!title.trim()) {
        alert('설문 제목을 입력해주세요.');
        return;
    }

    document.querySelectorAll('.question-block').forEach((questionBlock, index) => {
        const questionText = questionBlock.querySelector('.question-text').value;
        const questionType = questionBlock.querySelector('.question-type').value;
        const options = [];

        if (!questionText.trim()) {
            alert(`질문 ${index + 1}의 내용을 입력해주세요.`);
            return;
        }

        if (questionType !== 'TEXT') {
            const optionElements = questionBlock.querySelectorAll('.option-text');
            if (optionElements.length < 2) {
                alert(`질문 ${index + 1}은 최소 2개 이상의 옵션이 필요합니다.`);
                return;
            }

            optionElements.forEach(option => {
                const optionValue = option.value.trim();
                if (optionValue) {
                    options.push(optionValue);
                }
            });

            if (options.length < 2) {
                alert(`질문 ${index + 1}의 모든 옵션을 입력해주세요.`);
                return;
            }
        }

        questions.push({
            text: questionText,
            type: questionType,
            orderNumber: index + 1,
            options: options
        });
    });

    if (questions.length === 0) {
        alert('최소 1개 이상의 질문을 추가해주세요.');
        return;
    }

    const surveyData = {
        title: title,
        description: description,
        questions: questions
    };

    fetch('/survey/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(surveyData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('설문 저장에 실패했습니다.');
        }
        return response.json();
    })
    .then(result => {
        alert('설문이 성공적으로 저장되었습니다.');
        window.location.href = '/survey/list';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('설문 저장 중 오류가 발생했습니다: ' + error.message);
    });
}

function createQuestionHtml(questionNumber) {
    return `
        <div class="question-block" data-question="${questionNumber}">
            <div class="question-header">
                <h3>질문 ${questionNumber}</h3>
                <button type="button" class="remove-question-btn" onclick="removeQuestion(this)">삭제</button>
            </div>
            <div class="question-content">
                <input type="text" class="question-text" placeholder="질문을 입력하세요" required>
                <select class="question-type" required>
                    <option value="SINGLE_CHOICE">객관식 (단일 선택)</option>
                    <option value="MULTIPLE_CHOICE">객관식 (다중 선택)</option>
                    <option value="TEXT">주관식</option>
                </select>
                <div class="options-section">
                    <div class="options-container">
                        <div class="option">
                            <input type="text" class="option-text" name="option" placeholder="옵션 1" required>
                            <button type="button" class="remove-option-btn" onclick="removeOption(this)">삭제</button>
                        </div>
                    </div>
                    <button type="button" class="add-option-btn">옵션 추가</button>
                </div>
            </div>
        </div>
    `;
}

function createOptionHtml(questionNumber, optionNumber) {
    return `
        <div class="option">
            <input type="text" class="option-text" placeholder="옵션 ${optionNumber}" required>
            <button type="button" class="remove-option-btn" onclick="removeOption(this)">삭제</button>
        </div>
    `;
}

function removeQuestion(button) {
    button.closest('.question-block').remove();
}

function removeOption(button) {
    button.closest('.option').remove();
}

function submitSurvey(event, csrfHeader, csrfToken) {
    event.preventDefault();

    const surveyId = document.querySelector('input[name="surveyId"]').value;
    const answers = [];

    document.querySelectorAll('.question-block').forEach(questionBlock => {
        const questionId = questionBlock.querySelector('input, textarea').name.replace('question_', '');

        const radioButton = questionBlock.querySelector('input[type="radio"]:checked');
        if (radioButton) {
            answers.push({
                questionId: questionId,
                text: radioButton.nextElementSibling.textContent.trim() || radioButton.value,
                answerId: radioButton.value
            });
        }

        const checkboxes = questionBlock.querySelectorAll('input[type="checkbox"]:checked');
        checkboxes.forEach(checkbox => {
            answers.push({
                questionId: questionId,
                text: checkbox.nextElementSibling.textContent.trim() || checkbox.value,
                answerId: checkbox.value
            });
        });

        const textarea = questionBlock.querySelector('textarea');
        if (textarea && textarea.value.trim()) {
            answers.push({
                questionId: questionId,
                text: textarea.value.trim(),
                answerId: null
            });
        }
    });

    if (answers.length === 0) {
        alert('최소 하나 이상의 답변을 입력해주세요.');
        return;
    }

    fetch('/survey/submit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            surveyId: surveyId,
            answers: answers
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('설문 제출에 실패했습니다.');
        }
        return response.text();
    })
    .then(result => {
        alert('설문이 성공적으로 제출되었습니다.');
        window.location.href = '/survey/list';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('설문 제출 중 오류가 발생했습니다: ' + error.message);
    });
}


 