/* ****************
 *  일정 편집
 * ************** */
function editEvent(event) {
    const eventModal = new bootstrap.Modal(document.getElementById('eventModal'));
    const modalTitle = document.querySelector('.modal-title');
    
    modalTitle.textContent = '일정 수정';
    
    // 폼 필드 설정
    document.getElementById('edit-title').value = event.title;
    document.getElementById('edit-start').value = moment(event.start).format('YYYY-MM-DDTHH:mm');
    document.getElementById('edit-end').value = moment(event.end || event.start).subtract(1, 'days').format('YYYY-MM-DDTHH:mm');
    document.getElementById('edit-type').value = event.extendedProps.type;
    document.getElementById('edit-desc').value = event.extendedProps.description;
    document.getElementById('edit-color').value = event.backgroundColor;
    document.getElementById('edit-allDay').checked = event.allDay;

    document.querySelector('.modalBtnContainer-addEvent').style.display = 'none';
    document.querySelector('.modalBtnContainer-modifyEvent').style.display = 'block';
    
    eventModal.show();

    $('#updateEvent').off('click').on('click', function() {
            
        if (userRole !== 'ROLE_ADMIN') {
            alert('이 기능은 관리자만 사용할 수 있습니다.');
            info.revert();
            return;
        }
        if (document.getElementById('edit-title').value === '') {
            alert('일정명은 필수입니다.');
            return;
        }

        if (document.getElementById('edit-start').value > document.getElementById('edit-end').value) {
            alert('끝나는 날짜가 앞설 수 없습니다.');
            return;
        }

        const eventData = {
            title: document.getElementById('edit-title').value,
            start: document.getElementById('edit-start').value,
            end: document.getElementById('edit-end').value,
            description: document.getElementById('edit-desc').value,
            type: document.getElementById('edit-type').value,
            backgroundColor: document.getElementById('edit-color').value,
            allDay: document.getElementById('edit-allDay').checked
        };

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: `/events/${event.id}`,
            type: 'PUT',
            data: JSON.stringify(eventData),
            contentType: 'application/json',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);  // CSRF 토큰 추가
            },
            success: function(response) {
                calendar.refetchEvents();
                eventModal.hide();
            },
            error: function() {
                alert('일정 수정 중 오류가 발생했습니다.');
            }
        });
    });

    $('#deleteEvent').off('click').on('click', function() {
        if (confirm('이 일정을 삭제하시겠습니까?')) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                url: `/events/delete/${event.id}`,
                type: 'DELETE',
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(header, token);  // CSRF 토큰 추가
                },
                success: function(response) {
                    calendar.refetchEvents();
                    eventModal.hide();
                },
                error: function() {
                    alert('일정 삭제 중 오류가 발생했습니다.');
                }
            });
        }
    });
}