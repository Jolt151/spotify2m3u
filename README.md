# spotify2m3u

Convert spotify playlists to local m3u files

## How it works

spotify2m3u finds and indexes all songs in your music library, and creates a 
local web server to interface with the application. You can choose the spotify playlist
you want to convert, and it will find all the direct matches by title, and give you a choice to
specify the path of any track it's not sure about. 

## How to use
 - Create a file named application.properties in the directory you'll be running the program. That's 
 in the root folder of the project, or the directory the jar will be run from. Add a key named
 "library_folder" and set it to the root path of your music library. For example: `library_folder = 
 C:\\Users\\me\\Desktop\\music`
 
 - Run the app with ``java -jar spotify2m3u.jar``
 
 - Navigate to ``localhost:8080``
 
 - Enter the spotify playlist id and press match
 
 - Fill in the missing matches by selecting from the dropdown list,
 or add your own path to the file. Press Update Selection to update the matches. If a 
 custom path wasn't found, it won't be added.
 
 - Download the m3u file once all your items are filled. Missing tracks won't be part
 of the final m3u.
 
 ## What it uses
  - Spring Boot
  - REST-oriented architecture. The server and the client are completely independent
  and only communicate via HTTP calls.
  - Kotlin
  - Raw HTML and JS. This would be nice to update.