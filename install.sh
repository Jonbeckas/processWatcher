#!/bin/bash
function oldversion() {
    OLDVERSION=false
    if test -f "watcher"; then
        OLDVERSION=true
    fi
    if test -f "watchercli"; then
      OLDVERSION=true
    fi
}

function checkjava() {
  if type -p java; then
    echo found java executable in PATH
    _java=java
  elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
      echo Found java executable in JAVA_HOME
      _java="$JAVA_HOME/bin/java"
  else
      echo "No java executable found!"
      echo "Please install java >= 8 first"
      exit
  fi

  if [[ "$_java" ]]; then
      Version=$($_java -version 2>&1 | sed -E -n 's/.* version "([^.-]*).*"/\1/p' | cut -d' ' -f1)
      echo Detect java Version "$Version"
      if [ "$Version" -ge 8 ]; then
          echo "Java version check oK"
      else
        echo "No java 8 or higher found"
        echo "Please install java >= 8 first"
        exit
      fi
  fi
}


if [ "$EUID" -ne 0 ]
  then echo "Bitte führen sie das script als root aus!"
  exit
fi
checkjava
cd /usr/bin/
oldversion
if [ $OLDVERSION == true ]; then
  echo "Detect an processWatcher Version!"
  echo "Do you want to perform an update?[Y/N]"
  read input </dev/tty
  if [ "$input" == "Y" ] || [ "$input" == "y" ]; then
      echo "Update ProcessWatcher"
    if test -f "watcher"; then
        rm watcher
    fi
    if test -f "watchercli"; then
      rm watchercli
    fi
  elif [ "$input" == "N" ] || [ "$input" == "n" ]; then
    echo "Nothing to Do!"
    echo "exit"
    exit
  else
    echo "No Valid Input!"
    echo "exit"
    exit
  fi
fi

curl -s https://api.github.com/repos/Jonbeckas/processWatcher/releases/latest \
| grep "watcher" \
| cut -d : -f 2,3 \
| tr -d \" \
| wget -q --show-progress -i -

chmod a+x watcher
chmod a+x watchercli
