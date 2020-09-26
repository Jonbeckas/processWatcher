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
  local IFS=$'\n'
  # remove \r for Cygwin
  local lines=$("$_java" -Xms32M -Xmx32M -version 2>&1 | tr '\r' '\n')
  if [[ -z $_java ]]
  then
    result=no_java
  else
    for line in $lines; do
      if [[ (-z $result) && ($line = *"version \""*) ]]
      then
        local ver=$(echo $line | sed -e 's/.*version "\(.*\)"\(.*\)/\1/; 1q')
        # on macOS, sed doesn't support '?'
        if [[ $ver = "1."* ]]
        then
          result=$(echo $ver | sed -e 's/1\.\([0-9]*\)\(.*\)/\1/; 1q')
        else
          result=$(echo $ver | sed -e 's/\([0-9]*\)\(.*\)/\1/; 1q')
        fi
      fi
    done
    echo Detect Java "$result"
    if [ "$result" -ge 8 ]; then
          echo "Java version check ok"
    else
        echo "No java 8 or higher found"
        echo "Please install java >= 8 first"
        exit
   fi
  fi
}

echo PROCESSWATCHER INSTALL SCRIPT
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
