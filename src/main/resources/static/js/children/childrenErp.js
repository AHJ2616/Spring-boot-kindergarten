function deleteChildren(childrenId) {

    if (confirm('정말 삭제하겠습니까?')) {
        fetch('/erp/children/delete/${childrenId}', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('삭제되었습니다.');
                    window.location.reload();
                } else {
                    response.text().then(text => alert(text));
                }
            })
            .catch(error => {
                console.error('Error', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
    }
}


// 원아 등록에서 초기 폼은 1개로 설정
let childCount = 1;

function addChildForm() {
    // 자녀 버튼 클릭 시 실행되는 함수!

    const newIndex = childCount++;


    const newChildForm = `
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">자녀 정보 #${childCount}</h5>
            </div>
            <div class="card-body">
                <div class="form-group">
                    <label class="required-label">이름</label>
                    <input type="text" class="form-control"
                           name="childrenArray[${newIndex}].childrenName"
                           required>
                </div>

                <div class="form-group">
                    <label class="required-label">생년월일</label>
                    <input type="date" class="form-control"
                           name="childrenArray[${newIndex}].childrenBirthDate"
                           required>
                </div>

                <div class="form-group">
                    <label class="required-label">성별</label>
                    <select class="form-control"
                            name="childrenArray[${newIndex}].childrenGender"
                            required>
                        <option value="">선택하세요</option>
                        <option value="MALE">남자</option>
                        <option value="FEMALE">여자</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>혈액형</label>
                    <select class="form-control"
                            name="childrenArray[${newIndex}].childrenBloodType">
                        <option value="">혈액형 선택</option>
                        <option value="A">A형</option>
                        <option value="B">B형</option>
                        <option value="AB">AB형</option>
                        <option value="O">O형</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>알레르기:</label>
                    <textarea class="form-control"
                              name="childrenArray[${newIndex}].childrenAllergies"
                              placeholder="알레르기가 있다면 입력해 주시길 바랍니다."></textarea>
                </div>

                <div class="form-group">
                    <label>병력 정보:</label>
                    <textarea class="form-control"
                              name="childrenArray[${newIndex}].childrenMedicalHistory"
                              placeholder="병력 정보가 있다면 입력해 주시길 바랍니다."></textarea>
                </div>

                <div class="form-group">
                    <label>특이사항</label>
                    <textarea class="form-control"
                              name="childrenArray[${newIndex}].childrenNotes"
                              rows="3"></textarea>
                </div>
            </div>
        </div>
    `;// 이름, 생년월일, 혈액형 등 원래 있던 폼 항목들을 복제 해온다.

    $('#childrenForms').append(newChildForm);
    // 복제한 폼을 원래 있던 폼 뒤에다가 배치한다.
}// function addChildForm() END


$(document).ready(function() {

    console.log('childrenForms 개수:', $('#childrenForms').children().length);

});// $(document).ready(function() END

function removeChildForm(button) {

    const formToRemove = button.parentElement;
    formToRemove.remove();

    const container = document.getElementById('childrenContainer');

    const forms = container.getElementsByClassName('chlid-form');

    Array.from(forms).forEach((form, index) => {
        form.querySelector('h4').textContent = `자녀 ${index + 1}`;
        updateFormInputNames(form, index);
    });
}

function updateFormInputNames(form, index) {

    const inputs = form.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {

        const name = input.getAttribute('name');

        if (name) {

            input.setAttribute('name', name.replace(/\[\d+\]/, `[${index}]`));
        }
    });
}