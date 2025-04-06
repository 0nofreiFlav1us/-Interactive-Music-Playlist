// Initialize the YouTube Player
let player;
function onYouTubeIframeAPIReady() {
    player = new YT.Player("player", {
        height: "360",
        width: "640",
        playerVars: {
            listType: "playlist",
            list: "PLtQdJXqk9qF8ME0N8hjVxL5fea2j5ZrJK",
        },
        events: {
            "onReady": onPlayerReady,
            "onStateChange": onPlayerStateChange
        }
    });
}

var videoIndex;
function onPlayerReady(event) {
    videoIndex = getCookie("videoIndex");

    // If the playlist previously ended, I'll try to get the next probable added song.
    if (getCookie("ended") !== null) {
        videoIndex++;
        document.cookie = "videoIndex=" + videoIndex + "; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
        document.cookie = "ended=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
    }

    let playlistLength = player.getPlaylist().length
    if (videoIndex == null || playlistLength === parseInt(videoIndex, 10)) {
        document.cookie = "videoIndex=0; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
        videoIndex = 0;
        document.cookie = "playingClientSongs=0; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
    }

    player.playVideoAt(videoIndex)
}

const socket = new WebSocket("ws://localhost:8080/playlist/songs/ws");

socket.onmessage = function(event) {
    if (event.data === "new_song") {
        if (getCookie("playingClientSongs") !== null && getCookie("playingClientSongs") === "0") {
            document.cookie = "playingClientSongs=1; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
            player.getPlaylist().length
            document.cookie = "videoIndex=" + player.getPlaylist().length + "; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/"
            document.cookie = "newSongAdded=1; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
            console.log("new song message received")
        }
    }
}

var randomVideoPlayed = 0
function onPlayerStateChange(event) {
    if (event.data === YT.PlayerState.PLAYING) {

        if (getCookie("newSongAdded") !== null && getCookie("newSongAdded") === "1") {
            document.cookie = "newSongAdded=; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
            location.href = location.href.split('?')[0] + '?cache_bust=' + new Date().getTime();
            return;
        }

        // Display the current playing song's title
        let videoData = player.getVideoData();
        document.getElementById("song-title").innerText = videoData.title;

        let playingClientSongs = getCookie("playingClientSongs")
        if (playingClientSongs !== null && playingClientSongs === "0" && randomVideoPlayed === 0) {
            randomVideoPlayed = 1
            let randomVideoIndex = Math.floor(Math.random() * player.getPlaylist().length);
            player.playVideoAt(randomVideoIndex)
            return;
        }

        // Detect when the playlist moves to the next song
        let currentIndex = player.getPlaylistIndex();
        if (currentIndex !== videoIndex) {
            document.cookie = "videoIndex=" + currentIndex + "; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
            videoIndex = currentIndex;
        }
        randomVideoPlayed = 0
        socket.send(player.getVideoData().video_id)
    }

    // Check if the playlist has ended
    if (event.data === YT.PlayerState.ENDED) {
        document.cookie = "ended=" + 1 + "; expires=Fri, 31 Dec 2025 23:59:59 GMT; path=/";
        location.reload()
    }
}


