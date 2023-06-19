class PlaylistManager {
    constructor() {
		this.currentBlock = parseInt(urlParams.get('playlistBlock')) || 1;
		this.playlistId = parseInt(urlParams.get('playlistId')) || -1;
		this.pageSize = 5;
		this.songManager = new SongManager();
        this.pageNumber = parseInt(urlParams.get('playlistPage')) || 1;
        this.previousButton = document.getElementById('previousPlaylistButton');
        this.nextButton = document.getElementById('nextPlaylistButton');
        this.addForm = document.getElementById('addSongsToPlaylistForm');
        this.removeForm = document.getElementById('removeSongsFromPlaylistForm');
        this.mainElement = document.getElementById('playlistMain');
        this.titleElement = this.mainElement.querySelector('#playlistTitle');
        this.descriptionElement = this.mainElement.querySelector('#playlistDescription');

		this.setPageNumber = function(pageNumber) {
			this.pageNumber = pageNumber;
		};
		
        this.hideAndShow = function() {
			const playlistSongsRow = this.removeForm.querySelector('table tr');
			while(playlistSongsRow.firstChild) {
				playlistSongsRow.removeChild(playlistSongsRow.firstChild);
			}
			const removeButton = this.removeForm.querySelector("[type='submit']");
			if (this.maxBlock == 0) {
				removeButton.style.display = "none";
				const cell = document.createElement('td');
                cell.textContent = "La playlist non contiene brani.";
                cell.classList.add('playlistSong');
	            cell.classList.add("card");
				playlistSongsRow.appendChild(cell);
			} else {
				removeButton.style.display = "block";
			}
			for (let i = 0; i < this.songs.length; i++) {
	           if (!(i < (this.pageNumber - 1) * 5 || i >= this.pageNumber * 5)) {
					this.renderSong(playlistSongsRow, this.songs[i])
	           }
	        }
            let isLastPage = this.pageNumber * this.pageSize >= this.songs.length;
            let isLastBlock = this.currentBlock >= this.maxBlock;
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
            }).then(response => {
			    if (response.ok) {
			      return response;
			    } else {
			      response.text().then(errorMessage => {
					renderErrorMessage(errorMessage);
			        throw new Error(errorMessage || 'Unknown error');
			      });
			    }
			  })
			  .then(data => {
			    this.refresh();
			  })
        };
        
        this.handleAddFormSubmit = function(event) {
			if (!this.addForm.checkValidity()){
				    return;
			}
            event.preventDefault();
            const playlistId = this.addForm.querySelector('input[name="playlistId"]').value;
            const selectedSongs = Array.from(this.addForm.querySelectorAll('option:checked')).map(option => option.value);
            const formData = new FormData();
            selectedSongs.forEach(songId => {
                formData.append('songIds', songId);
            });
            formData.append('playlistId', playlistId);

            this.handleFormSubmit(event, formData);
        };

        this.handleRemoveFormSubmit = function(event) {
			if (!this.removeForm.checkValidity()){
				    return;
			}
            event.preventDefault();
            const playlistId = this.removeForm.querySelector('input[name="playlistId"]').value;
            const selectedSongs = Array.from(this.removeForm.querySelectorAll('input[type="checkbox"]:checked')).map(checkbox => checkbox.value);
            const formData = new FormData();
            selectedSongs.forEach(songId => {
                formData.append('songIds', songId);
            });
            formData.append('playlistId', playlistId);

            this.handleFormSubmit(event, formData);
        };

        this.addForm.addEventListener('submit', this.handleAddFormSubmit.bind(this));
        this.removeForm.addEventListener('submit', this.handleRemoveFormSubmit.bind(this));
		
        this.update = function(playlistData) {
			this.mainElement.style.display = "block";
			this.currentBlock = playlistData.currentBlock;
			this.maxBlock = playlistData.maxBlock;
			this.songsPerBlock = playlistData.songsPerBlock;
			this.songs = playlistData.playlistSongsWithAlbum;
			this.songManager.update(playlistData.playlistSongsWithAlbum)
            this.setPageNumber(1);
            this.updateQueryParams();
            
            this.titleElement.textContent = playlistData.playlist.title;
            this.descriptionElement.textContent = playlistData.playlist.description;

            const addSongsForm = document.getElementById('addSongsToPlaylistForm');
			
            const songSelect = addSongsForm.querySelector('select#song');
            songSelect.innerHTML = '';

            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = '-- Select songs --';
            defaultOption.setAttribute('disabled', '');
            songSelect.appendChild(defaultOption);

            playlistData.userSongs.forEach((song) => {
                const option = document.createElement('option');
                option.value = song.id;
                option.textContent = song.title;
                songSelect.appendChild(option);
            });
            
            this.hideAndShow();
        };
        
        this.show = function(nextBlock) {
			if (this.playlistId < 0) {
				this.mainElement.style.display = "none";
				return;
			}
            fetch(contextPath + "GetPlaylist?playlistId=" + this.playlistId + "&playlistBlock=" + nextBlock, {
                method: 'GET'
            })
                .then(response => response.json())
                .then(data => {
					const playlist = data.playlist;
                    const playlistSongsWithAlbum = data.playlistSongsWithAlbum;
                    const userSongs = data.userSongs;
                    const currentBlock = data.currentBlock;
                    const maxBlock = data.maxBlock;
                    const songsPerBlock = data.songsPerBlock;

                    this.update({ playlist, playlistSongsWithAlbum, userSongs, currentBlock, maxBlock, songsPerBlock });
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
            this.removeForm.querySelector('input[name="playlistId"]').setAttribute("value", this.playlistId);
            this.addForm.querySelector('input[name="playlistId"]').setAttribute("value", this.playlistId);
			this.updateQueryParams();
			this.currentBlock = 1;
			this.pageNumber = 1;
			this.show(this.currentBlock);
		}
		
		this.refresh = function() {
			this.show(this.currentBlock);
			return;
		}
		
		this.renderSong = function(parent, entry) {
				    const songId = entry[0].id;
	                const songTitle = entry[0].title;
	                const imageSrc = encodeURIComponent(entry[1].image);
	                const imageAlt = entry[1].title;
	
	                const cell = document.createElement('td');
	                cell.classList.add('playlistSong');
	                cell.classList.add("card");

	                const wrapper = document.createElement('div');
	
	                const checkbox = document.createElement('input');
	                checkbox.type = 'checkbox';
	                checkbox.name = 'songIds';
	                checkbox.value = songId;
	
	                const label = document.createElement('a');
	                label.setAttribute("data-id", songId);
	                label.textContent = songTitle;
	                label.addEventListener('click', this.songManager.goToSong.bind(this.songManager, songId));
	                label.setAttribute("href", "#playerReference");
	                label.classList.add("interactable");
	
	                wrapper.appendChild(checkbox);
	                wrapper.appendChild(label);
	
	                const image = document.createElement('img');
	                image.setAttribute('loading', 'lazy');
	                image.src = contextPath + `FileHandler?fileName=${imageSrc}`;
	                image.alt = imageAlt;
	
	                cell.appendChild(wrapper);
	                cell.appendChild(image);
	                parent.appendChild(cell);
		}

        this.removeForm.querySelector('input[name="playlistId"]').setAttribute("value", this.playlistId);
        this.addForm.querySelector('input[name="playlistId"]').setAttribute("value", this.playlistId);
		this.show(this.currentBlock);
    }
}