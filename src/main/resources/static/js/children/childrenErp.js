// ERP 에서 학부모와 유아에 관련된 JavaScript 코드

function addChildForm() {
    const formsDiv = document.getElementById('childrenForms');
    const formCount = formsDiv.children.length;

    // 기존 폼을 복제하여 새로운 폼 생성
    const newForm = formsDiv.children[0].cloneNode(true);
    newForm.querySelector('.card-title').textContent = `자녀 정보 #${formCount + 1}`;

    // 입력 필드 초기화 및 인덱스 업데이트
    const inputs = newForm.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        const name = input.getAttribute('name').replace(/\[\d+\]/, `[${formCount}]`);
        input.setAttribute('name', name);
        input.value = '';
    });

    formsDiv.appendChild(newForm);
}