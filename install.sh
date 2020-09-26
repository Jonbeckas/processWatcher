#!/bin/bash
if [ "$EUID" -ne 0 ]
  then echo "Bitte führen sie das script als root aus!"
  exit
fi

cd /usr/bin/
if test -f "watcher"; then
    rm watcher
fi
if test -f "watchercli"; then
    rm watchercli
fi
curl -s https://api.github.com/repos/Jonbeckas/processWatcher/releases/latest \
| grep "watcher" \
| cut -d : -f 2,3 \
| tr -d \" \
| wget -q --show-progress -i -

chmod a+x watcher
chmod a+x watchercli
