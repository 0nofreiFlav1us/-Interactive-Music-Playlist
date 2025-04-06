const socket = new WebSocket("ws://localhost:8080/playlist/songs/ws");

socket.onmessage = function(event) {
    if (event.data !== "new_song") {
        let song = document.getElementById(event.data);

        if (song) {
            let cloneSong = song.cloneNode(true);
            document.getElementById("current_song_playing").replaceChildren(cloneSong);
        } else {
            console.log("The element with that ID doesn't exist.");
        }
    }

};

function addSong() {
    socket.send("new_song")
}