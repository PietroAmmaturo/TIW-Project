// Get references to all playlist elements
const songs = document.getElementsByClassName('playerSong');
{
	console.log("song started");
	// Set the initial display count to 5 if not present in the query string
	let pageNumber = parseInt(urlParams.get('songPage')) || 1;
	
	// Get references to the previous and next buttons
	const previousButton = document.getElementById('previousSongButton');
	const nextButton = document.getElementById('nextSongButton');
	
	// Get references to all playlist elements
	const songs = document.getElementsByClassName('playerSong');
	
	
	// Function to update the URL search params without reloading the page
	function updateQueryParams() {
	  const updatedUrlParams = new URLSearchParams(window.location.search);
	  updatedUrlParams.set('songPage', pageNumber.toString());
	
	  const newUrl = window.location.pathname + '?' + updatedUrlParams.toString();
	  window.history.pushState({ path: newUrl }, '', newUrl);
	}
	
	// Hide all playlists except the first pageNumber number of playlists
	function hideSongs() {
	  for (let i = 0; i < songs.length; i++) {
	    if (i < pageNumber - 1 || i >= pageNumber) {
	      songs[i].hidden = true;
	    } else {
	      songs[i].hidden = false;
	    }
	  }
	}
	
	// Event listener for "Next" button
	nextButton.addEventListener('click', () => {
	  if (pageNumber < songs.length) {
		  console.log("nxt s")
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
	
	// Hide all songs except the initial pageNumber number of playlists on page load
	hideSongs();
}
function goToSong(songId) {
  console.log("going to song")
  for (let i = 0; i < songs.length; i++) {
    const song = songs[i];
    const id = song.getAttribute("data-id");
    console.log("going to song", songId, song, id)
    if (id == songId) {
	  pageNumber = i;
	  console.log("really going to song", songId)
	  updateQueryParams();
	  hideSongs();
      return;
    }
  }
  return; // Song not found
}