console.log("homejs started");

class HomeManager{
	constructor (){
		this.playlistManager = new PlaylistManager();
		
		this.newSongNewALbumForm = document.querySelector("#newSongNewAlbum");
		this.newSongExistingALbumForm = document.querySelector("#newSongExistingAlbum");
		this.newPlaylistForm = document.querySelector("#newPlaylist");
		
		
		
		this.handleFormSubmit = function(event, formData) {
            fetch(event.target.action, {
                method: 'POST',
                
                body: formData
            })
                .then(response => {
                    console.log('Request succeeded with response:', response);
                    this.show();
                })
                .catch(error => {
                    console.error('Request failed:', error);
                });
        };
        
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

        this.handleRemoveFormSubmit = function(event) {
            event.preventDefault();
            const selectedSongs = Array.from(document.querySelectorAll('#removeSongsFromPlaylistForm input[type="checkbox"]:checked')).map(checkbox => checkbox.value);
            const formData = new FormData();
            selectedSongs.forEach(songId => {
                formData.append('songIds', songId);
            });

            this.handleFormSubmit(event, formData);
        };

        this.newSongNewALbumForm.addEventListener('submit', this.handleNewSongNewALbum.bind(this));
        //this.newSongExistingALbumForm.addEventListener('submit', this.handleNewSongExistingALbum.bind(this));
        //this.newPlaylistForm.addEventListener('submit', this.handlePlaylist.bind(this));
		
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
			
			const playlistsContainer = document.querySelector("#formsDiv #playlistShowDiv #playlist");
			const albumsContainer = document.querySelector("#formsDiv #existingAlbum");
			const songsContainer = document.querySelector("#formsDiv #songsPossible");
			
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
				playlistEl.addEventListener("click", this.playlistManager.goToPlaylist.bind(this.playlistManager, playlist.id));
        		playlistEl.textContent = playlist.title;
        		playlistsContainer.appendChild(playlistEl);
        		
        		let playlistReorderEl = document.createElement("a");
        		playlistReorderEl.setAttribute("href",contextPath + "GoToReorder?playlistId=" + playlist.id);
        		playlistReorderEl.textContent = ("Reorder " + playlist.title);
        		playlistEl.appendChild(playlistReorderEl);
        		
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