document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var isAdmin = /*document.getElementById('isAdmin').value ===*/ 'true';
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        editable: isAdmin,
        selectable: isAdmin,
        events: '/events',
        eventClick: function(info) {
            showViewModal(info.event);
        },
        select: function(info) {
            if (isAdmin) {
                showScheduleModal(info);
            }
        },
        eventDrop: function(info) {
            if (isAdmin) {
                updateEventDates(info.event);
            }
        }
    });

    calendar.render();

    // 모달 드래그 설정
    $(document).ready(function() {
        $(".modal-content").draggable({
            handle: ".modal-header",
            containment: "window"
        });
    });

    // 모달 닫기 함수
    window.closeModal = function(modalId) {
        var modal = document.getElementById(modalId);
        modal.style.display = 'none';
        // 모달 위치 초기화
        var modalContent = modal.querySelector('.modal-content');
        modalContent.style.top = '50%';
        modalContent.style.left = '50%';
        modalContent.style.transform = 'translate(-50%, -50%)';
    }

    // 모달 닫기 버튼 이벤트
    document.querySelectorAll('.close').forEach(function(el) {
        el.addEventListener('click', function() {
            var modalId = this.closest('.modal').id;
            closeModal(modalId);
        });
    });

    // 모달 외부 클릭시 닫기
    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            closeModal(event.target.id);
        }
    });

    // 모달 열기 함수
    window.openModal = function(modalId) {
        var modal = document.getElementById(modalId);
        modal.style.display = 'block';
    }

    // showScheduleModal 함수 수정
    window.showScheduleModal = function(info) {
        document.getElementById('start').value = info.startStr;
        document.getElementById('end').value = info.endStr;
        openModal('scheduleModal');
    }

    // showViewModal 함수 수정
    window.showViewModal = function(event) {
        document.getElementById('viewTitle').textContent = event.title;
        document.getElementById('viewContent').textContent = event.extendedProps.scheduleContent;
        document.getElementById('viewLocation').textContent = event.extendedProps.location;
        document.getElementById('viewStart').textContent = event.start.toLocaleString();
        document.getElementById('viewEnd').textContent = event.end ? event.end.toLocaleString() : '';
        document.getElementById('currentEventId').value = event.id;
        openModal('viewModal');
    }

    // 일정 저장
    document.getElementById('scheduleForm').addEventListener('submit', function(e) {
        e.preventDefault();
        var scheduleData = {
            scheduleId: document.getElementById('scheduleId').value,
            scheduleTitle: document.getElementById('scheduleTitle').value,
            scheduleContent: document.getElementById('scheduleContent').value,
            location: document.getElementById('location').value,
            start: document.getElementById('start').value,
            end: document.getElementById('end').value
        };

        $.ajax({
            url: '/events/add',
            type: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken // CSRF 토큰 추가
            },
            data: JSON.stringify(scheduleData),
            success: function(response) {
                calendar.addEvent({
                    scheduleId: response.scheduleId,
                    scheduleTitle: response.scheduleTitle,
                    start: response.start,
                    end: response.end,
                    extendedProps: {
                        scheduleContent: response.scheduleContent,
                        location: response.location
                    }
                });
                closeModal('scheduleModal');
                document.getElementById('scheduleForm').reset();
            },
            error: function(xhr, status, error) {
                alert('일정 저장에 실패했습니다.');
                console.error(error);
            }
        });
    });

    // 이벤트 수정
    document.getElementById('editEvent').addEventListener('click', function() {
        var eventId = document.getElementById('currentEventId').value;
        var event = calendar.getEventById(eventId);

        document.getElementById('scheduleId').value = eventId;
        document.getElementById('scheduleTitle').value = event.title;
        document.getElementById('scheduleContent').value = event.extendedProps.scheduleContent;
        document.getElementById('location').value = event.extendedProps.location;
        document.getElementById('start').value = event.start.toISOString().slice(0, 16);
        if (event.end) {
            document.getElementById('end').value = event.end.toISOString().slice(0, 16);
        }

        closeModal('viewModal');
        document.getElementById('scheduleModal').style.display = 'block';
    });

    // 이벤트 삭제
    document.getElementById('deleteEvent').addEventListener('click', function() {
        var eventId = document.getElementById('currentEventId').value;
        if (confirm('정말로 이 일정을 삭제하시겠습니까?')) {
            $.ajax({
                url: '/events/' + eventId,
                type: 'DELETE',
                success: function() {
                    calendar.getEventById(eventId).remove();
                    closeModal('viewModal');
                }
            });
        }
    });

    // 이벤트 업데이트 (드래그 앤 드롭)
    function updateEventDates(event) {
        $.ajax({
            url: '/events/' + event.id,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                scheduleId: event.id,
                start: event.start.toISOString(),
                end: event.end ? event.end.toISOString() : null
            })
        });
    }
}); 