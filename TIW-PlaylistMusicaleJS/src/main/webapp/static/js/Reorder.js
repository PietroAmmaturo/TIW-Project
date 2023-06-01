class ReorderManager {
    constructor() {
		this.songs = document.querySelectorAll("#playlistSong");
		
        this.handleFormSubmit = function(event, formData) {
            fetch(event.target.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            })
                .then(response => {
                    console.log('Request succeeded with response:', response);
                    this.show();
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
        };
    }
}

document.addEventListener('DOMContentLoaded', function() {const playlistManager = new PlaylistManager();})