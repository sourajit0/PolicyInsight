document.addEventListener('DOMContentLoaded', function() {
    // Add file name display when selecting a file
    const fileInput = document.getElementById('pdfFile');
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const fileName = e.target.files[0]?.name || 'No file selected';
            let fileNameDisplay = document.querySelector('.file-name-display');

            if (!fileNameDisplay) {
                fileNameDisplay = document.createElement('div');
                fileNameDisplay.className = 'file-name-display';
                fileInput.parentNode.appendChild(fileNameDisplay);
            }

            fileNameDisplay.textContent = fileName;
        });
    }

    // Add loading indicator for form submission
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function() {
            // Create and show loading overlay
            const loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'loading-overlay';
            loadingOverlay.innerHTML = `
                <div class="loading-spinner"></div>
                <p>Processing your document...</p>
            `;
            document.body.appendChild(loadingOverlay);

            // Add styles for loading overlay
            const style = document.createElement('style');
            style.textContent = `
                .loading-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background-color: rgba(0, 0, 0, 0.7);
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    z-index: 1000;
                    color: white;
                }
                .loading-spinner {
                    border: 5px solid rgba(255, 255, 255, 0.3);
                    border-radius: 50%;
                    border-top: 5px solid white;
                    width: 50px;
                    height: 50px;
                    animation: spin 1s linear infinite;
                    margin-bottom: 20px;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(style);
        });
    }
});