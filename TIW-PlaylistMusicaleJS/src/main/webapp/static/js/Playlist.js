class PlaylistManager {
    constructor() {
		this.pageSize = 5;
		this.songManager = new SongManager();
        this.pageNumber = parseInt(urlParams.get('playlistPage')) || 1;
        this.previousButton = document.getElementById('previousPlaylistButton');
        this.nextButton = document.getElementById('nextPlaylistButton');
        this.addForm = document.getElementById('addSongsToPlaylistForm');
        this.removeForm = document.getElementById('removeSongsFromPlaylistForm');
        this.songs = document.getElementsByClassName('playlistSong');
		
        this.addForm.addEventListener('submit', this.handleAddFormSubmit);
        this.removeForm.addEventListener('submit', this.handleRemoveFormSubmit);

        this.nextButton.addEventListener('click', () => {
            if (this.pageNumber * this.pageSize < this.songs.length) {
                this.pageNumber++;
                this.hideAndShow();
                this.updateQueryParams();
            }
        });

        this.previousButton.addEventListener('click', () => {
            if (this.pageNumber > 1) {
                this.pageNumber--;
                this.hideAndShow();
                this.updateQueryParams();
            }
        });

        this.handleAddFormSubmit = function(event) {
            event.preventDefault();
            const selectedSongs = Array.from(document.querySelectorAll('#addSongsToPlaylistForm option:checked')).map(option => option.value);
            const formData = new FormData();
            selectedSongs.forEach(songId => {
                formData.append('songIds', songId);
            });

            this.handleFormSubmit(event, formData);
        };

        this.handleRemoveFormSubmit = function(event) {
            event.preventDefault();
            const selectedSongs = Array.from(document.querySelectorAll('#removeSongsFromPlaylistForm input[type="checkbox"]:checked')).map(checkbox => checkbox.value);
            const formData = new FormData();
            selectedSongs.forEach(songId => {
                formData.append('songIds', songId);
            });

            this.handleFormSubmit(event, formData);
        };

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

        this.updateQueryParams = function() {
            const updatedUrlParams = new URLSearchParams(window.location.search);
            updatedUrlParams.set('playlistPage', this.pageNumber.toString());

            const newUrl = window.location.pathname + '?' + updatedUrlParams.toString();
            window.history.pushState({ path: newUrl }, '', newUrl);
        };

        this.hideAndShow = function() {
            for (let i = 0; i < this.songs.length; i++) {
                if (i < (this.pageNumber - 1) * 5 || i >= this.pageNumber * 5) {
                    this.songs[i].hidden = true;
                } else {
                    this.songs[i].hidden = false;
                }
            }
        };
        
        this.show = function() {
            fetch("http://localhost:8080/TIW-PlaylistMusicale/GetPlaylist?playlistId=" + urlParams.get('playlistId'), {
                method: 'GET'
            })
                .then(response => response.json())
                .then(data => {
                    const playlistSongsWithAlbum = data.playlistSongsWithAlbum;
                    const userSongs = data.userSongs;

                    console.log(playlistSongsWithAlbum);
                    console.log(userSongs);

                    this.update({ playlistSongsWithAlbum, userSongs });
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
        };

        this.update = function(playlistData) {
            console.log("updating...", playlistData);
            const removeSongsForm = document.getElementById('removeSongsFromPlaylistForm');
            const addSongsForm = document.getElementById('addSongsToPlaylistForm');
            const playlistSongsRow = removeSongsForm.querySelector('table tr');
            playlistSongsRow.innerHTML = '';

            playlistData.playlistSongsWithAlbum.forEach((entry) => {
                const songId = entry[0].id;
                const songTitle = entry[0].title;
                const imageSrc = entry[1].image;
                const imageAlt = entry[1].title;

                const cell = document.createElement('td');
                cell.classList.add('playlistSong');
                cell.hidden = true;

                const wrapper = document.createElement('div');

                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = 'songIds';
                checkbox.value = songId;

                const label = document.createElement('label');
                label.setAttribute("data-id", songId);
                label.textContent = songTitle;
                label.addEventListener('click', this.handleSongClick.bind(this, songId));

                wrapper.appendChild(checkbox);
                wrapper.appendChild(label);

                const image = document.createElement('img');
                image.setAttribute('loading', 'lazy');
                console.log(imageSrc)
                image.src = `http://localhost:8080/TIW-PlaylistMusicale/FileHandler?fileName=${imageSrc}`;
                image.alt = imageAlt;

                cell.appendChild(wrapper);
                cell.appendChild(image);
                playlistSongsRow.appendChild(cell);
            });

            const songSelect = addSongsForm.querySelector('select#song');
            songSelect.innerHTML = '';

            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = '-- Select songs --';
            songSelect.appendChild(defaultOption);

            playlistData.userSongs.forEach((song) => {
                const option = document.createElement('option');
                option.value = song.id;
                option.textContent = song.title;
                songSelect.appendChild(option);
            });

			this.songManager.update(playlistData.playlistSongsWithAlbum)
            this.songs = document.getElementsByClassName('playlistSong');
            this.hideAndShow();
            this.updateQueryParams();
        };
        
        this.handleSongClick = function(songId) {
			console.log("clicked", songId)
			let songPageNumber = this.songManager.getSongPageNumber(songId);
			this.songManager.setPageNumber(songPageNumber);
			this.songManager.updateQueryParams();
			this.songManager.hideAndShow();
		    this.songManager.updateQueryParams();
		};
		
		this.show();
    }
}

document.addEventListener('DOMContentLoaded', function() {const playlistManager = new PlaylistManager();})
