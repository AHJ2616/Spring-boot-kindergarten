// ERP에서 부모 등록할 때 필요한 JavaScript (jQuery 버전)
console.log('스크립트 로드 ::');

// DOM이 완전히 로드된 후 실행되는 초기화 코드
$(document).ready(function() {

    // 폼 제출 이벤트 처리
    $('form').on('submit', function(e) {
        e.preventDefault();


        const phone1 = $('input[maxlength="3"][required]').val();
        const phone2 = $('input[maxlength="4"][required]').first().val();
        const phone3 = $('input[maxlength="4"][required]').last().val();
        // 3칸으로 나누어진 연락처들을 조합한다.


        if (phone1 && phone2 && phone3) {
            $('[name="parentPhone"]').val(`${phone1}-${phone2}-${phone3}`);
        }// 연락처가 모두 입력된 경우에만 조합하여 hidden 필드에 설정

        const emergency1 = $('input[placeholder="입력"]').val();
        const emergency2 = $('input[maxlength="4"]:not([required])').first().val();
        const emergency3 = $('input[maxlength="4"]:not([required])').last().val();
        // 3칸으로 나누어진 비상연락처들을 조합한다.


        if (emergency1 && emergency2 && emergency3) {
            $('[name="emergencyContact"]').val(`${emergency1}-${emergency2}-${emergency3}`);
        }// 비상연락처가 모두 입력된 경우에만 조합하여 hidden 필드에 설정


        $.ajax({
            url: $(this).attr('action'),
            type: 'POST',
            data: $(this).serialize(),
            success: function(response) {// AJAX 요청이 성공이 되면?
                console.log('서버 응답:', response);

                if (response.success) {
                    // 모달에 임시 비밀번호 표시
                    $('#tempPasswordDisplay').text(response.tempPassword);

                    var modal = $('#passwordModal');
                    modal.modal({
                        backdrop: 'static',
                        keyboard: false
                    });
                    modal.modal('show');
                    // 모달 표시


                    $('#confirmPassword').one('click', function() {
                        modal.modal('hide');
                        window.location.href = '/erp/children/register?parentId=' + response.parentId;
                    });// 확인 버튼 클릭 시 원아 등록페이지로 이동하면서 부모ID도 넘어간다.

                } else {
                    alert(response.error || '등록에 실패했습니다.');
                    // 요청이 실패되면 알럿으로 알려주기
                }
            },// success end
            error: function(xhr, status, error) {
                console.error('에러 상세:', {
                    status: status,
                    error: error,
                    response: xhr.responseText
                });
                alert('서버 오류가 발생했습니다.');
            }// error end

        });// AJAX END
    });// $('form').on('submit', function(e) END

    // 전화번호 입력 필드에 숫자만 입력되도록 제한
    $('input[maxlength]').on('input', function() {
        // 입력된 값에서 숫자가 아닌 문자를 모두 제거
        $(this).val(function(_, value) {
            return value.replace(/[^0-9]/g, '');
        });
    });//  $('input[maxlength]').on('input', function() END


});// $(document).ready(function() END