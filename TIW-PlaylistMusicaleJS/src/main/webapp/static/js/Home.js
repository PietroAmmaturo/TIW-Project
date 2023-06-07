console.log("homejs started");
// Get the current URL search params
const urlParams = new URLSearchParams(window.location.search);
var contextPath = document.querySelector('#contextPath').textContent;
console.log(contextPath);

class HomeManager{
	constructor (){
		this.playlistManager = new PlaylistManager();
		
		this.show = function(){
			//per  fetchare le playlist dell'utente
		}
	
		this.update = function(data){ //data è quello che arriva dalla richiesta di show
			//chiamato da this.show per generare l'html e i listener
			//quanto clicco su una playlist faccio playlistManager.goToPlaylist(playlistId)
		}
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
