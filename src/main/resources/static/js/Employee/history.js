// pdf-preview.js
document.addEventListener("DOMContentLoaded", function() {
    const pdfIds = window.pdfIds; // window 객체에 pdfIds를 할당

    if (pdfIds) {
        pdfIds.forEach(id => {
            const url = '/education/preview/' + id;
            const canvas = document.getElementById('pdf-canvas-' + id);
            const context = canvas.getContext('2d');

            pdfjsLib.getDocument(url).promise.then(pdf => {
                pdf.getPage(1).then(page => {
                    const viewport = page.getViewport({ scale: 1 });
                    canvas.width = viewport.width;
                    canvas.height = viewport.height;

                    const renderContext = {
                        canvasContext: context,
                        viewport: viewport
                    };
                    page.render(renderContext);
                });
            });
        });
    }
});