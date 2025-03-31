document.addEventListener('DOMContentLoaded', function() {
    const translateForm = document.getElementById('translateForm');
    const resultDiv = document.getElementById('translationResult');
    const errorDiv = document.getElementById('errorResult');

    translateForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const sourceText = document.getElementById('sourceText').value;
        const sourceLang = document.getElementById('sourceLang').value;
        const targetLang = document.getElementById('targetLang').value;

        // Очистка предыдущих результатов
        resultDiv.innerHTML = '';
        errorDiv.textContent = '';

        // Валидация
        if (!sourceText.trim()) {
            showError('Please enter text to translate');
            return;
        }

        // Показать индикатор загрузки
        resultDiv.innerHTML = '<div class="loading">Translating...</div>';

        try {
            const response = await fetch('/api/v1/translate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    sourceLang: sourceLang,
                    targetLang: targetLang,
                    text: sourceText
                })
            });

            const data = await handleResponse(response);
            showTranslation(data.translatedText);
        } catch (error) {
            showError(error.message || 'Translation service unavailable');
        }
    });

    async function handleResponse(response) {
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Translation failed');
        }
        return response.json();
    }

    function showTranslation(text) {
        resultDiv.innerHTML = text;
        resultDiv.style.color = 'var(--tbank-gold-light)';
        resultDiv.style.borderColor = 'var(--tbank-gold)';
    }

    function showError(message) {
        errorDiv.textContent = message;
        resultDiv.innerHTML = '';
    }
});