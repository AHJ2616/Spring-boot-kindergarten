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

    const eventModal = new bootstrap.Modal(document.getElementById('eventModal'));
    const modalTitle = document.querySelector('.modal-title');
    
    modalTitle.textContent = '새로운 일정';
    document.getElementById('edit-type').value = eventType;
    document.getElementById('edit-title').value = '';
    document.getElementById('edit-start').value = moment(start).format('YYYY-MM-DDTHH:mm');
    document.getElementById('edit-end').value = moment(end).format('YYYY-MM-DDTHH:mm');
    document.getElementById('edit-desc').value = '';

    document.querySelector('.modalBtnContainer-addEvent').style.display = 'block';
    document.querySelector('.modalBtnContainer-modifyEvent').style.display = 'none';
    
    eventModal.show();

    $('#save-event').off('click').on('click', function() {
        const editTitle = document.getElementById('edit-title');
        const editStart = document.getElementById('edit-start');
        const editEnd = document.getElementById('edit-end');
        const editDesc = document.getElementById('edit-desc');
        
        if (editTitle.value.trim() === '') {
            alert('일정명은 필수입니다.');
            return;
        }

        if (editStart.value > editEnd.value) {
            alert('끝나는 날짜가 앞설 수 없습니다.');
            return;
        }

        const eventData = {
            title: editTitle.value.trim(),
            start: editStart.value,
            end: editEnd.value,
            description: editDesc.value.trim() || '', // 빈 문자열이면 빈 문자열로 설정
            type: document.getElementById('edit-type').value,
            username: '사나',
            backgroundColor: document.getElementById('edit-color').value,
            textColor: '#ffffff',
            allDay: document.getElementById('edit-allDay').checked
        };

        console.log('전송할 일정 데이터:', eventData);


        $.ajax({
            url: '/events/add',
            type: 'POST',
            data: JSON.stringify(eventData),
            contentType: 'application/json',
            success: function(response) {
                console.log('저장된 일정:', response);
                calendar.refetchEvents();
                eventModal.hide();
                alert('일정이 저장되었습니다.');
            },
            error: function(xhr, status, error) {
                console.error('저장 실패:', error);
                console.error('상태:', status);
                alert('일정 저장 중 오류가 발생했습니다.');
            }
        });
    });
};