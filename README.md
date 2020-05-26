With ProcessWatcher you can track processes and running a commandline command if there are running or not runnung.


## Install
1. Download jar from [Releases](https://github.com/Jonbeckas/watcher/releases)
1. Make sure its run when your system boots
  
## Configurate
 1. Run watcher one times manualy
 1. Watcher creates a `config.json` in the home directory or in the directory where the watcher.jar is
 1. Open config.json in a text editor 
 1. For Example:
 ```json
 {
  "attemps": 5,
  "refresh": 10,
  "work": [
    {
      "procname": "process Name like in tasklist.exe or ps -e",
      "if": "command if process run",
       "else": "command if process is not running "
     },
     {
       "linkconf": "link to another config.json"
     },
     {
       "keepalive": "restarts the programm if it is not running"
     }
    ]
   }
  ```
  ### Additional Hints
  **attempts**: How often ProcessWatcher should try to start keepAlive programes in one refresh circle
  
  **refresh:** How long ProcessWatcher should wait between the refresh circles
  
  **Keepalive:** Imedeatly Restart the Process if it stops. Logs output in logFile and try the number of attempts to restart. The attempts resets if one refresh circle is over.
