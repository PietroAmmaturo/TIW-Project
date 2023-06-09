class PlaylistManager {
    constructor() {
		this.currentBlock = 1;
		this.playlistId = -1;
		this.pageSize = 5;
		this.songManager = new SongManager();
        this.pageNumber = parseInt(urlParams.get('playlistPage')) || 1;
        this.previousButton = document.getElementById('previousPlaylistButton');
        this.nextButton = document.getElementById('nextPlaylistButton');
        this.addForm = document.getElementById('addSongsToPlaylistForm');
        this.removeForm = document.getElementById('removeSongsFromPlaylistForm');
		
		this.setPageNumber = function(pageNumber) {
			this.pageNumber = pageNumber;
		};
		
        this.hideAndShow = function() {
            for (let i = 0; i < this.songs.length; i++) {
                if (i < (this.pageNumber - 1) * 5 || i >= this.pageNumber * 5) {
                    this.songs[i].hidden = true;
                } else {
                    this.songs[i].hidden = false;
                }
            }
            let isLastPage = this.pageNumber * this.pageSize >= this.songs.length;
            let isLastBlock = this.currentBlock == this.maxBlock;
            let isFirstPage = this.pageNumber == 1;
            let isFirstBlock = this.currentBlock == 1;
            if (!(isLastPage && isLastBlock) && !(isFirstPage && isFirstBlock)) {
				this.nextButton.style.display = "block";
				this.previousButton.style.display = "block";
				return;
			}
            if ((isLastPage && isLastBlock) && !(isFirstPage && isFirstBlock)) {
				this.nextButton.style.display = "none";
				this.previousButton.style.display = "block";
				return;
			}
			if (!(isLastPage && isLastBlock) && (isFirstPage && isFirstBlock)) {
				this.nextButton.style.display = "block";
				this.previousButton.style.display = "none";
				return;
			}
			if ((isLastPage && isLastBlock) && (isFirstPage && isFirstBlock)) {
				this.nextButton.style.display = "none";
				this.previousButton.style.display = "none";
				return;
			}
			return;
        };
        
        this.updateQueryParams = function() {
            const updatedUrlParams = new URLSearchParams(window.location.search);
            updatedUrlParams.set('playlistId', this.playlistId.toString());
            updatedUrlParams.set('playlistPage', this.pageNumber.toString());
            updatedUrlParams.set('playlistBlock', this.currentBlock.toString());

            const newUrl = window.location.pathname + '?' + updatedUrlParams.toString();
            window.history.pushState({ path: newUrl }, '', newUrl);
        };
        
        this.nextButton.addEventListener('click', () => {
			let isLastPage = this.pageNumber * this.pageSize >= this.songs.length;
			if (isLastPage) {
				this.show(this.currentBlock + 1);
				return;
			}
            if (this.pageNumber * this.pageSize < this.songs.length) {
                this.pageNumber++;
                this.updateQueryParams();
                this.hideAndShow();
            }
        });
        //todo non andrebbe aggiunto false dopo la funzione?

        this.previousButton.addEventListener('click', () => {
			let isFirstPage = this.pageNumber == 1;
			if (isFirstPage) {
				this.show(this.currentBlock - 1);
				return;
			}
            if (this.pageNumber > 1) {
                this.pageNumber--;
                this.updateQueryParams();
                this.hideAndShow();
            }
        });

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

        this.addForm.addEventListener('submit', this.handleAddFormSubmit.bind(this));
        this.removeForm.addEventListener('submit', this.handleRemoveFormSubmit.bind(this));
        
        this.handleSongClick = function(songId) {
			let songPageNumber = this.songManager.getSongPageNumber(songId);
			this.songManager.setPageNumber(songPageNumber);
			this.songManager.updateQueryParams();
			this.songManager.hideAndShow();
		};
		
        this.update = function(playlistData) {
			this.currentBlock = playlistData.currentBlock;
			this.maxBlock = playlistData.maxBlock;
			this.songsPerBlock = playlistData.songsPerBlock;
            console.log("updating...", playlistData);
            const removeSongsForm = document.getElementById('removeSongsFromPlaylistForm');
            const addSongsForm = document.getElementById('addSongsToPlaylistForm');
            const playlistSongsRow = removeSongsForm.querySelector('table tr');
            playlistSongsRow.innerHTML = '';
			if (this.maxBlock == 0) {
				const cell = document.createElement('td');
                cell.textContent = "La playlist non contiene brani.";
				playlistSongsRow.appendChild(cell);
			} else {
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
	                image.src = contextPath + `FileHandler?fileName=${imageSrc}`;
	                image.alt = imageAlt;
	
	                cell.appendChild(wrapper);
	                cell.appendChild(image);
	                playlistSongsRow.appendChild(cell);
            	});
			}

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
            this.setPageNumber(1);
            this.updateQueryParams();
            this.hideAndShow();
        };
        
        this.show = function(nextBlock) {
			if (this.playlistId < 0) {
				return;
			}
            fetch(contextPath + "GetPlaylist?playlistId=" + this.playlistId + "&playlistBlock=" + nextBlock, {
                method: 'GET'
            })
                .then(response => response.json())
                .then(data => {
                    const playlistSongsWithAlbum = data.playlistSongsWithAlbum;
                    const userSongs = data.userSongs;
                    const currentBlock = data.currentBlock;
                    const maxBlock = data.maxBlock;
                    const songsPerBlock = data.songsPerBlock;

                    this.update({ playlistSongsWithAlbum, userSongs, currentBlock, maxBlock, songsPerBlock });
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
        };
		
		this.goToPlaylist = function(playlistId) {
			if (playlistId == this.playlistId) {
				this.show(this.currentBlock);
				return;
			}
			this.playlistId = playlistId;
			const removeSongsForm = document.getElementById('removeSongsFromPlaylistForm');
			removeSongsForm.querySelector("input").setAttribute("value", urlParams.get('playlistId'));
			this.updateQueryParams();
			this.currentBlock = 1;
			this.pageNumber = 1;
			this.show(this.currentBlock);
		}
    }
}