console.log("homejs started");

class HomeManager{
	constructor (){
		this.playlistManager = new PlaylistManager();
		this.mainElement = document.querySelector("#homeMain");
		this.newSongNewALbumForm = this.mainElement.querySelector("#newSongNewAlbum");
		this.newSongExistingALbumForm = this.mainElement.querySelector("#newSongExistingAlbum");
		this.newPlaylistForm = this.mainElement.querySelector("#newPlaylist");
		
		
		
		this.handleFormSubmit = function(event, formData) {
            fetch(event.target.action, {
                method: 'POST',
                
                body: formData
            })
                .then(response => {
                    console.log('Request succeeded with response:', response);
                    this.show();
                    this.playlistManager.refresh();
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
        };
        
        this.handleUrlFormSubmit = function(event, formData) {
            fetch(event.target.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams(formData).toString()
            })
			  .then(response => {
			    if (response.ok) {
			      return response.json();
			    } else {
			      throw new Error(response.statusText);
			    }
			  })
			  .then(data => {
			    console.log('Request succeeded with response:', data);
			    // Handle successful response here
			    this.show();
			  })
			  .catch(error => {
			    console.error('Error:', error.message);
			    window.alert('Error: ' + error.message);
			  });
        };
        
        this.handleNewSongExistinAlbum = function(event){
			event.preventDefault();
			const title = this.newSongExistingALbumForm.querySelector("[name='song_title']").value;
			const selectedGenre = this.newSongExistingALbumForm.querySelector("#genre option:checked").value;
			const audioFile = this.newSongExistingALbumForm.querySelector("[name='audioFile']").files[0];
			const albumId = this.newSongExistingALbumForm.querySelector("[name='albumId']").value;
			
			const formData = new FormData();
			formData.append("song_title", title);
			formData.append("audioFile", audioFile);
			formData.append("song_genre", selectedGenre);
			formData.append("albumId", albumId);
			
			console.log(formData);
			
			this.handleFormSubmit(event, formData);
		}
        
        this.handleNewSongNewALbum = function(event) {
            event.preventDefault();
            const title = this.newSongNewALbumForm.querySelector("[name='song_title']").value;
           	const selectedGenre = this.newSongNewALbumForm.querySelector("#genre option:checked").value;
           	const audioFile = this.newSongNewALbumForm.querySelector("[name='audioFile']").files[0];
           	const albumTitle = this.newSongNewALbumForm.querySelector("[name='album_title']").value;
           	const albumArtist = this.newSongNewALbumForm.querySelector("[name='album_artist']").value; 
           	const albumYear = this.newSongNewALbumForm.querySelector("[name='album_year']").value;
           	const albumCover = this.newSongNewALbumForm.querySelector("[name='album_cover']").files[0];
           	
           	const formData = new FormData();
           	formData.append("song_title", title);
           	formData.append("audioFile", audioFile);
           	formData.append("album_title", albumTitle);
           	formData.append("album_artist", albumArtist);
           	formData.append("album_year", albumYear);
           	formData.append("song_genre", selectedGenre);
           	formData.append("album_cover", albumCover);
       
       		console.log(formData);

            this.handleFormSubmit(event, formData);
        };

		this.handlePlaylist = function(event){
			//TODO rivedere questo metodo, non funziona
			event.preventDefault();
			const playlistTitle = this.newPlaylistForm.querySelector("[name='playlist_title']").value;
			const playlistDescription = this.newPlaylistForm.querySelector("[name='playlist_description']").value;
			const selectedSongsIds = Array.from(this.newPlaylistForm.querySelectorAll("#newPlaylist input[type='checkbox']:checked"))
										  .map(checkbox => checkbox.value);
										  
			console.log(selectedSongsIds);							  
			
			const formData = new FormData();
			
			selectedSongsIds.forEach(id => {
                formData.append("songIds", id);
            });
			formData.append("playlist_title", playlistTitle);
			formData.append("playlist_description", playlistDescription);
			
			//formData.append("songIds", selectedSongsIds);
				
			console.log(formData);
			
			this.handleUrlFormSubmit(event, formData);
				
		}

        this.newSongNewALbumForm.addEventListener('submit', this.handleNewSongNewALbum.bind(this));
        this.newSongExistingALbumForm.addEventListener('submit', this.handleNewSongExistinAlbum.bind(this));
        this.newPlaylistForm.addEventListener('submit', this.handlePlaylist.bind(this));
		
		this.show = function(){
			//per  fetchare le playlist dell'utente
            fetch(contextPath + "GetHome", {
                method: 'GET'
            })
                .then(response => response.json())
                .then(data => {
					const songs = data.songs;
					const albums = data.albums;
					const playlists = data.playlists;	    
					
					console.log(data);    

                    this.update({ songs, albums, playlists });
                    
                    
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
                
		}
		
		this.update = function(data){
			const songs = data.songs;
			const albums = data.albums;
			const playlists = data.playlists;
			
			const playlistsContainer = this.mainElement.querySelector("#playlistShowDiv #playlist");
			const albumsContainer = this.mainElement.querySelector("#homeMain #existingAlbum");
			const songsContainer = this.mainElement.querySelector("#homeMain #songsPossible");
			
			console.log(data, playlistsContainer, albumsContainer, songsContainer);

			while(playlistsContainer.firstChild) {
				playlistsContainer.removeChild(playlistsContainer.firstChild);
			}
			while(albumsContainer.firstChild) {
				albumsContainer.removeChild(albumsContainer.firstChild);
			}
			while(songsContainer.firstChild) {
				songsContainer.removeChild(songsContainer.firstChild);
			}

			for(let i = 0; i < playlists.length; i++){
				const playlist = playlists[i];
				let playlistEl = document.createElement("div");
				playlistEl.classList.add("card");
				playlistEl.classList.add("wide");

				let playlistSpanEl = document.createElement("a");
        		playlistSpanEl.addEventListener("click", this.playlistManager.goToPlaylist.bind(this.playlistManager, playlist.id));
        		playlistSpanEl.textContent = playlist.title;
        		playlistSpanEl.classList.add("interactable");
        		playlistSpanEl.setAttribute("href", "#playlistReference");
        		playlistEl.appendChild(playlistSpanEl);
        		
        		let playlistReorderEl = document.createElement("a");
        		playlistReorderEl.setAttribute("href",contextPath + "GoToReorder?playlistId=" + playlist.id);
        		playlistReorderEl.textContent = "reorder";
        		playlistReorderEl.classList.add("interactable");
        		playlistReorderEl.classList.add("right");
        		playlistEl.appendChild(playlistReorderEl);
        		
        		playlistsContainer.appendChild(playlistEl);     		
        		        		
        		console.log(playlistEl);
			}
			for(let i = 0; i < albums.length; i++){
				const album = albums[i];
				let albumEl = document.createElement("option");
				albumEl.setAttribute("value",album.id);
        		albumEl.textContent = album.title;
        		albumsContainer.appendChild(albumEl);
        		
        		console.log(albumEl);
			}
			for(let i = 0; i < songs.length; i++){
				const song = songs[i];
				let songInputEl = document.createElement("input");
				let songEl = document.createElement("span");
				songInputEl.setAttribute("type","checkbox");
				songInputEl.setAttribute("value",song.id);
				songInputEl.setAttribute("name","songIds");

        		songEl.textContent = song.title;
        		songsContainer.appendChild(songInputEl);
        		songsContainer.appendChild(songEl);
        		songsContainer.appendChild(document.createElement("br"));
        		
        		
        		console.log(songEl);
			}
			
		}
		
		this.show();
	}
	
	//dopo che aggiungo una playlist devo rigenerare le playlist chiamando this.show
	//dopo che aggiungo una canzone devo rigenerare la playlist corrente
	
	
	//gestione dei form in home facendo la fetch (guarda playlist.js, getPlaylist per capire come fare il json)
	//dopo nua fetch fare consollog per vedere i risultati della fetch
	//in goToHome appena viene chiamato, home.js crea l'homeManager che gestisce tutto lui 
	//se passo il -1 al playlistManager non mostra playlist
	//i form non devono passare i json, il getHome deve passarlo
	//i form non hanno reidrect e fanno le stesse cose di prima
	//tutto quello che passavo come variabili di contesto in html qua bisogna passarlo con i json
	//serve servlet getHome
	//c'è bisogno di fare il fetch del form a mano perché nel caso ci sia un errore devo farlo vedere all'utente (consol log)
	//se il fetch non è 200 alert qualcosa è andata male
}

new HomeManager();