With ProcessWatcher you can track processes and running a commandline command if there are running or not runnung.


## Install

### With Script
1. ```sh
   curl -s https://raw.githubusercontent.com/Jonbeckas/processWatcher/master/install.sh | sudo bash
   ```
    
1. Make sure its run when your system boots

### Manual

1. Clone repo
1. ```sh
    sudo ./gradlew testInstallUnix
   ```
  
## Configure
 1. Run watcher one times manualy
 1. Watcher creates a `config.json`. This is located at /usr/share/processwatcher/ when it can write to this directory.
 Else it writes the configuration to the path of its executible or to the current working directory 
 1. Open config.json in a text editor 
 1. For Example:
 ```json
 {
  "attemps": 5,
  "refresh": 10,
  "link": ["link to another config.json","link to another config.json"],
  "work": [
     {
       "process": "the programm",
       "arguments": ["list","of","arguments"],
       "id": "id",
       "test": {
          "test": "test command",
          "expect": "expected return value of test command"
        } 
     }
    ]
   }
  ```
  ### Additional Hints
  **attempts**: How often ProcessWatcher should try to start keepAlive programes in one refresh circle. No impact in child confs
  
  **refresh:** How long ProcessWatcher should wait between the refresh circles. No impact in child confs
  
  **Keepalive:** Immediately Restart the Process if it stops. Logs output in logFile and try the number of attempts to restart. The attempts resets if one refresh circle is over.

  **Test:** Is not necessary
  
## CLI
Use watchercli for accessing the api
