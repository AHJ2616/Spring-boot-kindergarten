var eventModal = $('#eventModal');
var modalTitle = $('.modal-title');
var editAllDay = $('#edit-allDay');
var editTitle = $('#edit-title');
var editStart = $('#edit-start');
var editEnd = $('#edit-end');
var editType = $('#edit-type');
var editColor = $('#edit-color');
var editDesc = $('#edit-desc');

var addBtnContainer = $('.modalBtnContainer-addEvent');
var modifyBtnContainer = $('.modalBtnContainer-modifyEvent');


/* ****************
 *  새로운 일정 생성
 * ************** */
var newEvent = function (start, end, eventType) {

    $("#contextMenu").hide(); //메뉴 숨김

    modalTitle.html('새로운 일정');
    editType.val(eventType).prop('selected', true);
    editTitle.val('');
    editStart.val(start);
    editEnd.val(end);
    editDesc.val('');

    addBtnContainer.show();
    modifyBtnContainer.hide();
    eventModal.modal('show');

    //새로운 일정 저장버튼 클릭
    $('#save-event').unbind();
    $('#save-event').on('click', function () {


        // 유효성 검사를 먼저 수행
        if (editTitle.val() === '') {
            alert('일정명은 필수입니다.');
            return false;
        }

        if (editStart.val() > editEnd.val()) {
            alert('끝나는 날짜가 앞설 수 없습니다.');
            return false;
        }

        let eventData = {
            title: editTitle.val(),
            start: moment(editStart.val()).format('YYYY-MM-DDTHH:mm:ss'),
            end: moment(editEnd.val()).format('YYYY-MM-DDTHH:mm:ss'),
            description: editDesc.val(),
            type: editType.val(),
            username: '사나',
            backgroundColor: editColor.val(),
            textColor: '#ffffff',
            allDay: editAllDay.is(':checked')
        };

        // allDay 체크 처리
        if (eventData.allDay) {
            eventData.start = moment(eventData.start).format('YYYY-MM-DD[T]00:00:00');
            eventData.end = moment(eventData.end).add(1, 'days').format('YYYY-MM-DD[T]00:00:00');
        }

        console.log('전송할 데이터:', eventData); // 데이터 확인용 로그

        // AJAX 요청
        $.ajax({
            url: '/events/add',
            type: 'POST',
            data: JSON.stringify(eventData),
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {
                console.log('저장 성공:', response);

                // 캘린더에 이벤트 추가
                $("#calendar").fullCalendar('renderEvent', eventData, true);

                // 모달 초기화 및 닫기
                eventModal.find('input, textarea').val('');
                editAllDay.prop('checked', false);
                eventModal.modal('hide');

                // 캘린더 새로고침
                $('#calendar').fullCalendar('refetchEvents');
            },
            error: function (xhr, status, error) {
                alert('일정 저장 중 오류가 발생했습니다.');
            }
        });
    });
};
