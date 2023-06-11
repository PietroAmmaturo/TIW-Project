class ReorderManager {
    constructor() {
		this.songs = document.querySelectorAll(".drag-item");
		this.draggedItem;

        this.handleDragStart = function (event) {
			console.log("hds")
            this.draggedItem = event.target;
            event.dataTransfer.effectAllowed = 'move';
            event.dataTransfer.setData('text/html', this.draggedItem.innerHTML);
            event.target.style.opacity = '0.4';
        }
        
        this.handleDragOver = function (event) {
            event.preventDefault();
            event.dataTransfer.dropEffect = 'move';
            return false;
        }
        
        this.handleDragEnter = function (event) {
            event.target.classList.add('drag-over');
        }
        
        this.handleDragLeave = function (event) {
            event.target.classList.remove('drag-over');
        }
        
        this.handleDrop = function (event) {
            event.stopPropagation();
            event.target.classList.remove('drag-over');
            if (this.draggedItem !== event.target) {
                this.draggedItem.innerHTML = event.target.innerHTML;
                event.target.innerHTML = event.dataTransfer.getData('text/html');
            }
            return false;
        }
        
        this.handleDragEnd = function (event) {
            event.target.style.opacity = '1';
            var dragItems = document.getElementsByClassName('drag-item');
            for (var i = 0; i < dragItems.length; i++) {
                dragItems[i].classList.remove('drag-over');
            }
        }
        
        this.songs.forEach(songEl => songEl.addEventListener('dragstart', this.handleDragStart.bind(this)));
        this.songs.forEach(songEl => songEl.addEventListener('dragover', this.handleDragOver.bind(this)));
        this.songs.forEach(songEl => songEl.addEventListener('dragenter', this.handleDragEnter.bind(this)));
        this.songs.forEach(songEl => songEl.addEventListener('dragleave', this.handleDragLeave.bind(this)));
        this.songs.forEach(songEl => songEl.addEventListener('drop', this.handleDrop.bind(this)));
        this.songs.forEach(songEl => songEl.addEventListener('dragend', this.handleDragEnd.bind(this)));
    }
}
const reorderManager = new ReorderManager();