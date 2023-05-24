/**
 * 
 */
{
	console.log("playlist started");
	// Set the initial display count to 5 if not present in the query string
	let pageNumber = parseInt(urlParams.get('playlistPage')) || 1;
	
	// Get references to the previous and next buttons
	const previousButton = document.getElementById('previousPlaylistButton');
	const nextButton = document.getElementById('nextPlaylistButton');
	
	// Get references to all playlist elements
	const songs = document.getElementsByClassName('playlistSong');
	
	
	// Function to update the URL search params without reloading the page
	function updateQueryParams() {
	  const updatedUrlParams = new URLSearchParams(window.location.search);
	  updatedUrlParams.set('playlistPage', pageNumber.toString());
	
	  const newUrl = window.location.pathname + '?' + updatedUrlParams.toString();
	  window.history.pushState({ path: newUrl }, '', newUrl);
	}
	
	// Hide all playlists except the first pageNumber number of playlists
	function hideSongs() {
	  for (let i = 0; i < songs.length; i++) {
	    if (i < (pageNumber - 1) * 5 || i >= (pageNumber) * 5) {
	      songs[i].hidden = true;
	    } else {
	      songs[i].hidden = false;
	    }
	  }
	}
	
	// Event listener for "Next" button
	nextButton.addEventListener('click', () => {
	  if (pageNumber * 5 < songs.length) {
	    pageNumber++;
	    hideSongs();
	    updateQueryParams();
	  }
	});
	
	// Event listener for "Previous" button
	previousButton.addEventListener('click', () => {
	  if (pageNumber > 1) {
	    pageNumber--;
	    hideSongs();
	    updateQueryParams();
	  }
	});
	
	// Hide all playlists except the initial pageNumber number of playlists on page load
	hideSongs();
	
		
	document.getElementById('addSongsToPlaylistForm').addEventListener('submit', handleAddFormSubmit);
	document.getElementById('removeSongsFromPlaylistForm').addEventListener('submit', handleRemoveFormSubmit);

	function handleAddFormSubmit(event) {
		event.preventDefault(); // Prevent form submission
	
	  // Get the selected songIds
	  const selectedSongs = Array.from(document.querySelectorAll('#addSongsToPlaylistForm option:checked')).map(option => option.value);

	  // Create a new FormData object and append the selected songIds
	  const formData = new FormData();
	  selectedSongs.forEach(songId => {
	    formData.append('songIds', songId);
	  });
	  
	  handleFormSubmit(event, formData)
	}
	
	function handleRemoveFormSubmit(event) {
		event.preventDefault(); // Prevent form submission
	
	  // Get the selected songIds
	  const selectedSongs = Array.from(document.querySelectorAll('#removeSongsFromPlaylistForm input[type="checkbox"]:checked')).map(checkbox => checkbox.value);

	  // Create a new FormData object and append the selected songIds
	  const formData = new FormData();
	  selectedSongs.forEach(songId => {
	    formData.append('songIds', songId);
	  });
	  
	  handleFormSubmit(event, formData)
	}
	
	function handleFormSubmit(event, formData) {
	  // Perform the fetch request
	  fetch(event.target.action, {
	      method: 'POST',
	      headers: {
	        'Content-Type': 'application/x-www-form-urlencoded'
	      },
	      // sending them encoded as params, to make reading easier and consistent with the html only code
	      body: new URLSearchParams(formData).toString()
	  })
	  .then(response => {
	    // Handle the response as needed
	    console.log('Request 1 succeeded with response:', response);
	    show();
	  })
	  .catch(error => {
	    // Handle any errors that occurred during the fetch request
	    console.error('Request 1 failed:', error);
	  });
	}	
	
	function show() {
	  // Perform the fetch request
	  fetch("http://localhost:8080/TIW-PlaylistMusicale/GetPlaylist?playlistId=" + urlParams.get('playlistId'), {
		  method: 'GET'
	  })
	  .then(response => response.json())
	  .then(data => {
	    // Access the response data here
	    const playlistSongsWithAlbum = data.playlistSongsWithAlbum;
	    const userSongs = data.userSongs;
	
	    // Process the data further or update your UI
	    console.log(playlistSongsWithAlbum);
	    console.log(userSongs);
	
	    // Call the update function passing the retrieved data
	    update({ playlistSongsWithAlbum, userSongs });
	  })
	  .catch(error => {
	    // Handle any errors that occurred during the fetch request
	    console.error('Request 1 failed:', error);
	  });
	}
	
	function update(playlistData) {
	  console.log("updating...", playlistData)
	  const removeSongsForm = document.getElementById('removeSongsFromPlaylistForm');
	  const addSongsForm = document.getElementById('addSongsToPlaylistForm');
	
	  // Update playlistSongsWithAlbum section
	  const playlistSongsRow = removeSongsForm.querySelector('table tr');
	  playlistSongsRow.innerHTML = ''; // Clear existing table rows
	
	  // Create new table rows for updated data
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
	    const anchor = document.createElement('a');
	    anchor.setAttribute("data-id", songId);
	    anchor.textContent = songTitle;
	    anchor.addEventListener('click', goToSong.bind(this, songId));
	    label.appendChild(anchor);
	    
	    wrapper.appendChild(checkbox);
	    wrapper.appendChild(label);
	
	    const image = document.createElement('img');
	    image.setAttribute('loading', 'lazy');
	    image.src = `http://localhost:8080/TIW-PlaylistMusicale/FileHandler?fileName=${imageSrc}`;
	    image.alt = imageAlt;
	
	    cell.appendChild(wrapper);
	    cell.appendChild(image);
	    playlistSongsRow.appendChild(cell);
	  });
	
	  // Update the playlistId value in the removeSongsForm
	  const playlistIdInput = removeSongsForm.querySelector('input[name="playlistId"]');
	  playlistIdInput.value = playlistData.playlistId;
	
	  // Update addSongsToPlaylistForm section
	  const songSelect = addSongsForm.querySelector('select#song');
	  songSelect.innerHTML = ''; // Clear existing options
	
	  // Create new options for the select element
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
	  
	  hideSongs();
	}
}